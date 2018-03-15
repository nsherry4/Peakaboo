package peakaboo.curvefit.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import peakaboo.curvefit.model.EnergyCalibration;
import peakaboo.curvefit.model.FittingResult;
import peakaboo.curvefit.model.FittingResultSet;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import plural.streams.StreamExecutor;
import plural.streams.StreamExecutorSet;
import scitypes.Pair;
import scitypes.Range;
import scitypes.ReadOnlySpectrum;

public class AutoEnergyCalibration {


	private static List<EnergyCalibration> allEnergies(int dataWidth) {
		List<EnergyCalibration> energies = new ArrayList<>();
		for (float max = 0.05f; max <= 100f; max += 0.05f) {
			for (float min = -10.0f; min < 10.0f; min += 0.05) {
				if (min >= max) continue;
				energies.add(new EnergyCalibration(min, max, dataWidth));
			}
		}
		return energies;
	}
	
	private static FittingSet fitModel(List<TransitionSeries> tsList, int dataWidth) {
		FittingSet fits = new FittingSet();
		fits.setDataWidth(dataWidth);
		for (TransitionSeries ts : tsList) {
			fits.addTransitionSeries(ts);
		}
		return fits;
	}
	

	/**
	 * Accepts a spectrum, a list of transition series, and a data width, 
	 * and uses the transition series to quickly find any potential good 
	 * energy calibration values. 
	 */
	private static StreamExecutor<List<EnergyCalibration>> roughOptions(List<EnergyCalibration> energies, ReadOnlySpectrum spectrum, List<TransitionSeries> tsList, int dataWidth) {
		
		
		
		//SCORE THE ENERGY PAIRS AND CREATE AN INDEX -> SCORE MAP
		StreamExecutor<List<EnergyCalibration>> scorer = new StreamExecutor<>("Searching", energies.size() / 100);
		
		scorer.setTask(new Range(0, energies.size()-1), stream -> {

			//build a new model for experimenting with
			FittingSet fits = fitModel(tsList, dataWidth);
					
			//Score each energy value using our observed stream
			List<Pair<Integer, Float>> scores = stream.map(index -> {
				
				Map<TransitionSeries, Float> heights = fits.roughIndivudualHeights(spectrum, energies.get(index));
				float score = 0;
				for (Float f : heights.values()) {
					score += Math.sqrt(f);
				}
				return new Pair<>(index, score);
				
			}).collect(Collectors.toList());
			
			
			//Sort the scores
			scores.sort((s1, s2) -> s1.second.compareTo(s2.second));
			Collections.reverse(scores);
			
			
			
			//Take energy pairs based on scored index until we've taken some % or the score has dropped below some % of the best score
			List<EnergyCalibration> filteredScores = new ArrayList<>();
			float bestScore = scores.get(0).second;
			
			int cursor = 0;
			int maxCursor = Math.min(200, scores.size());
			while (cursor < maxCursor) {
				float score = scores.get(cursor).second;
				if (score < bestScore * 0.8f) break;
				filteredScores.add(energies.get(scores.get(cursor).first));
				cursor++;
			}
						
			return filteredScores;

		});

		
		return scorer;
		
	}
	
	
	/**
	 * Uses a slower algorithm to choose the best calibration from the rough options
	 */
	private static StreamExecutor<EnergyCalibration> chooseFromRoughOptions(Supplier<List<EnergyCalibration>> energies, ReadOnlySpectrum spectrum, List<TransitionSeries> tsList, int dataWidth) {
		
		StreamExecutor<EnergyCalibration> scorer = new StreamExecutor<>("Refining Results", 5);
		scorer.setTask(energies, stream -> {
			
			//build a new model for experimenting with
			FittingSet fits = fitModel(tsList, dataWidth);
			
			//Score each energy value using our observed stream
			List<Float> scores = stream.map(calibration -> {
				
				FittingResultSet results;
				synchronized(fits) {
					fits.setEnergy(calibration.getMinEnergy(), calibration.getMaxEnergy());
					results = fits.calculateFittings(spectrum);
				}
				
				float score = 0f;
				for (FittingResult fit : results.fits) {
					score += Math.sqrt(fit.fit.sum());
				}
				return score;
				
			}).collect(Collectors.toList());
			
			
			
			//Find the best score
			float bestScore = 0;
			int bestIndex = 0;
			for (int i = 0; i < scores.size(); i++) {
				float score = scores.get(i);
				if (score > bestScore) {
					bestScore = score;
					bestIndex = i;
				}
			}

			EnergyCalibration best = energies.get().get(bestIndex);
			return fineTune(best, spectrum, tsList, 0.1f);
			
		});
		
		
		
		
		return scorer;
		
	}
	
	
	private static EnergyCalibration fineTune(EnergyCalibration calibration, ReadOnlySpectrum spectrum, List<TransitionSeries> tsList, float window) {
		
		//build a new model for experimenting with
		FittingSet fits = fitModel(tsList, calibration.getDataWidth());
		
		//Find the best score, and its energy
		float bestScore = 0f;
		float bestMin = calibration.getMinEnergy();
		float bestMax = calibration.getMaxEnergy();
		
		float lomin = calibration.getMinEnergy() - window;
		float himin = calibration.getMinEnergy() + window;
		float lomax = calibration.getMaxEnergy() - window;
		float himax = calibration.getMaxEnergy() + window;

		
		for (float min = lomin; min <= himin; min += 0.01f) {
			for (float max = lomax; max <= himax; max += 0.01f) {
				if (max <= min) continue;
				
				fits.setEnergy(min, max);
				FittingResultSet results = fits.calculateFittingsUnsynchronized(spectrum);
				
				float score = 0f;
				for (FittingResult fit : results.fits) {
					score += Math.sqrt(fit.fit.sum());
				}
				
				if (score > bestScore) {
					bestScore = score;
					bestMin = min;
					bestMax = max;
				}
			}
		}
		
		return new EnergyCalibration(bestMin, bestMax, calibration.getDataWidth());
	}
	
	
	public static StreamExecutorSet<EnergyCalibration> propose(ReadOnlySpectrum spectrum, List<TransitionSeries> tsList, int dataWidth) {
		
		StreamExecutor<List<EnergyCalibration>> rough = roughOptions(allEnergies(dataWidth), spectrum, tsList, dataWidth);
		StreamExecutor<EnergyCalibration> quality = chooseFromRoughOptions(() -> rough.getResult().get(), spectrum, tsList, dataWidth);
		rough.then(quality);
		
		return new StreamExecutorSet<>(rough, quality);
		
	}
	
}
