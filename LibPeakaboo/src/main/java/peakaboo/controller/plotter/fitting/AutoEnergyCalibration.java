package peakaboo.controller.plotter.fitting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cyclops.Pair;
import cyclops.Range;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.peak.search.scoring.FastSignalMatchScorer;
import peakaboo.curvefit.peak.search.scoring.FittingScorer;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import plural.streams.StreamExecutor;
import plural.streams.StreamExecutorSet;

public class AutoEnergyCalibration {


	private static List<EnergyCalibration> allEnergies(int dataWidth) {
		List<EnergyCalibration> energies = new ArrayList<>();
		for (float max = 0.25f; max <= 100f; max += 0.05f) {
			for (float min = -0.25f; min < 0.25f; min += 0.05) {
				if (min >= max-1f) continue;
				energies.add(new EnergyCalibration(min, max, dataWidth));
			}
		}
		return energies;
	}
	
	private static FittingSet fitModel(List<TransitionSeries> tsList, int dataWidth) {
		FittingSet fits = new FittingSet();
		EnergyCalibration old = fits.getFittingParameters().getCalibration();
		fits.getFittingParameters().setCalibration(old.getMinEnergy(), old.getMaxEnergy(), dataWidth);
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
		StreamExecutor<List<EnergyCalibration>> scorer = new StreamExecutor<>("Searching for Calibrations", energies.size() / 100);
		
		scorer.setTask(new Range(0, energies.size()-1), stream -> {

			//build a new model for experimenting with
			FittingSet fits = fitModel(tsList, dataWidth);
					
			//Score each energy value using our observed stream
			List<Pair<Integer, Float>> scores = stream.map(index -> {
				
				EnergyCalibration calibration = energies.get(index);
				
				float score = scoreFitFast(fits, spectrum, calibration);
				return new Pair<>(index, score);
				
			}).collect(Collectors.toList());
			
			
			//Sort the scores
			scores.sort((s1, s2) -> s1.second.compareTo(s2.second));
			Collections.reverse(scores);
			
			
			
			//Take energy pairs based on scored index until we've taken some % or the score has dropped below some % of the best score
			List<EnergyCalibration> filteredScores = new ArrayList<>();
			float bestScore = scores.get(0).second;
			
			for (Pair<Integer, Float> score : scores) {
				if (score.second < bestScore * 0.5f) break;
				filteredScores.add(energies.get(score.first));
			}
						
			return filteredScores;

		});

		
		return scorer;
		
	}
	
	
	/**
	 * Uses a slower algorithm to choose the best calibration from the rough options
	 */
	private static StreamExecutor<EnergyCalibration> chooseFromRoughOptions(
			Supplier<List<EnergyCalibration>> energies, 
			ReadOnlySpectrum spectrum, 
			List<TransitionSeries> tsList,
			FittingController controller,
			int dataWidth
		) {
		
		StreamExecutor<EnergyCalibration> scorer = new StreamExecutor<>("Evaluating Candidates", 5);
		scorer.setTask(energies, stream -> {
			
			//build a new model for experimenting with
			ThreadLocal<FittingSet> fits = ThreadLocal.withInitial(() -> fitModel(tsList, dataWidth));
			
			//Score each energy value using our observed stream
			List<Float> scores = stream.map(calibration -> {
				
				FittingResultSet results;
				fits.get().getFittingParameters().setCalibration(calibration);
				results = controller.getFittingSolver().solve(spectrum, fits.get(), controller.getCurveFitter());
				return scoreFitGood(results, spectrum);
				
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
			return fineTune(best, spectrum, tsList, controller, 0.1f);
			
		});
		
		
		
		
		return scorer;
		
	}
	
	
	private static float scoreFitFast(FittingSet fits, ReadOnlySpectrum spectrum, EnergyCalibration calibration) {
		float score = 0;

		FittingScorer scorer = new FastSignalMatchScorer(spectrum, calibration);		
		for (TransitionSeries ts : fits.getVisibleTransitionSeries()) {
			if (ts.isVisible()) {
				score += Math.sqrt(scorer.score(ts));
			}
		}
		
		return score;
	}
	
	public static float scoreFitGood(FittingResultSet results, ReadOnlySpectrum spectrum) {
		float score = 0f;

		Spectrum fit = results.getTotalFit();

		//Method #2: find the percentage of signal fit
		float percent = 0;
		for (int i = 0; i < spectrum.size(); i++) {
			if (spectrum.get(i) <= 1f) { continue; }
			percent = fit.get(i) / spectrum.get(i);
			
			//Signal beyond a certain percent is as good as a perfect fit.
			percent = (float) Math.min(percent*1.1, 1);
			//square root because middling fit should not be rewarded too much
			score += Math.sqrt(percent);
		}
		
			
		
		return score;
	}
	
	
	private static EnergyCalibration fineTune(
			EnergyCalibration calibration, 
			ReadOnlySpectrum spectrum, 
			List<TransitionSeries> tsList, 
			FittingController controller,
			float window
		) {
		
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
				
				fits.getFittingParameters().setCalibration(min, max, calibration.getDataWidth());
				FittingResultSet results = controller.getFittingSolver().solve(spectrum, fits, controller.getCurveFitter());
				
				float score = scoreFitGood(results, spectrum);
				
				if (score > bestScore) {
					bestScore = score;
					bestMin = min;
					bestMax = max;
				}
			}
		}
		
		return new EnergyCalibration(bestMin, bestMax, calibration.getDataWidth());
	}
	
	
	public static StreamExecutorSet<EnergyCalibration> propose(
			ReadOnlySpectrum spectrum, 
			List<TransitionSeries> tsList, 
			FittingController controller, 
			int dataWidth
		) {
		
		StreamExecutor<List<EnergyCalibration>> rough = roughOptions(allEnergies(dataWidth), spectrum, tsList, dataWidth);
		StreamExecutor<EnergyCalibration> quality = chooseFromRoughOptions(() -> rough.getResult().get(), spectrum, tsList, controller, dataWidth);
		rough.then(quality);
		
		return new StreamExecutorSet<>(rough, quality);
		
	}
	
}
