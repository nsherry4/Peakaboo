package org.peakaboo.controller.plotter.fitting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver.FittingSolverContext;
import org.peakaboo.curvefit.peak.search.scoring.FastPeakSearchingScorer;
import org.peakaboo.curvefit.peak.search.searcher.DerivativePeakSearcher;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.framework.accent.Pair;
import org.peakaboo.framework.accent.numeric.Range;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.plural.streams.StreamExecutorSet;

public class AutoEnergyCalibration {

	// Caps how many rough candidates make the cut.
	private static final int MAX_ROUGH_SURVIVORS = 50;

	// We don't spam the stage 2 scorer with junk fits, so we filter out anything less than
	// a certain fraction of the best score.
	private static final float SURVIVOR_CUTOFF = 0.7f;

	// Some candidates cram several fitted series onto the same detected peak, so
	// we penalize that. Each duplicate claim takes a portion off the ideal score.
	private static final float DUPLICATE_CLAIM_PENALTY = 0.5f;

	// Detectors may put a large spike at zero energy. We need to prevent this peak from
	// being used in the scoring process, so we drop the first few percent of channels from
	// the peaks we match against. This value represents the percent of channels from the
	// left-hand side we ignore peaks from
	private static final float ZERO_PEAK_CHANNELS = 0.04f;

	// Not all data sets start at channel 0 == energy 0, but we need to have
	// practical bounds for how far off of zero we look. Assume that detectors
	// generally aren't clipping signal as often as they're pushing zero a little
	// to the right.
	private static final float MIN_ENERGY_LOWER_BOUND = -1.0f;
	private static final float MIN_ENERGY_UPPER_BOUND = 0.5f;

	// We use a two-step refinement process. First a coarse pass to find the approx.
	// position, and then a fine-tuning pass to get a more exact value.
	private static final float COARSE_WINDOW = 0.1f;
	private static final float COARSE_STEP = 0.01f;
	private static final float FINE_WINDOW = 0.01f;
	private static final float FINE_STEP = 0.002f;

	private AutoEnergyCalibration() {
		// Not Constructable
	}

	/**
	 * Generates a list of all possible energy calibration candidates
	 */
	private static List<Supplier<EnergyCalibration>> allEnergies(int dataWidth, boolean varyMinumum) {
		List<Supplier<EnergyCalibration>> energies = new ArrayList<>();
		for (float max = 1f; max <= 100f; max += 0.05f) {
			if (varyMinumum) {
				for (float min = MIN_ENERGY_LOWER_BOUND; min < MIN_ENERGY_UPPER_BOUND; min += 0.05) {
					if (min >= max-1f) continue;
					energies.add(buildEnergySupplier(min, max, dataWidth));
				}
			} else {
				energies.add(buildEnergySupplier(0f, max, dataWidth));
			}
		}
		return energies;
	}
	
	private static Supplier<EnergyCalibration> buildEnergySupplier(float min, float max, int dataWidth) {
		return () -> new EnergyCalibration(min, max, dataWidth);
	}

	/**
	 * Checks if the fitted serieses provide at least two strong, separate lines.
	 * Two of these lines (eg Ka+Kb) lets us find both the slope and offset,
	 * so the min energy can be searched instead of fixed at 0
	 */
	private static boolean hasMultipleStrongLines(List<ITransitionSeries> tsList) {
		// Go through all transitions in all series which are at least 10% of the
		// strongest overall transition. Track the lowest and highest energy levels
		// of these transitions and only return true if there is a good distance
		// between them.
		float lowest = Float.MAX_VALUE;
		float highest = -Float.MAX_VALUE;
		for (ITransitionSeries ts : tsList) {
			float strongest = ts.getStrongestTransition().relativeIntensity;
			for (Transition t : ts.getAllTransitions()) {
				if (t.relativeIntensity < strongest * 0.1f) continue;
				lowest = Math.min(lowest, t.energyValue);
				highest = Math.max(highest, t.energyValue);
			}
		}
		return highest - lowest > 0.5f;
	}
	
	private static FittingSet fitModel(List<ITransitionSeries> tsList, int dataWidth) {
		FittingSet fits = new FittingSet();
		EnergyCalibration old = fits.getFittingParameters().getCalibration();
		fits.getFittingParameters().setCalibration(old.getMinEnergy(), old.getMaxEnergy(), dataWidth);
		for (ITransitionSeries ts : tsList) {
			fits.addTransitionSeries(ts);
		}
		return fits;
	}
	

	/**
	 * Accepts a spectrum, a list of transition series, and a data width, 
	 * and uses the transition series to quickly find any potential good 
	 * energy calibration values. 
	 */
	private static StreamExecutor<List<EnergyCalibration>> roughOptions(List<Supplier<EnergyCalibration>> energies, SpectrumView spectrum, List<ITransitionSeries> tsList, int dataWidth) {
		
		// The spike at zero is an artefact, not a line, so don't let anything match it
		List<Integer> found = new DerivativePeakSearcher().search(spectrum);
		int zeroCut = (int) (dataWidth * ZERO_PEAK_CHANNELS);
		List<Integer> peakIndexes = found.stream().filter(i -> i >= zeroCut).toList();

		//SCORE THE ENERGY PAIRS AND CREATE AN INDEX -> SCORE MAP
		StreamExecutor<List<EnergyCalibration>> scorer = new StreamExecutor<>("Searching for Calibrations", energies.size() / 100);

		scorer.setTask(new Range(0, energies.size() - 1), stream -> {

			//build a new model for experimenting with
			FittingSet fits = fitModel(tsList, dataWidth);

			//Score each energy value using our observed stream
			List<Pair<Integer, Float>> scores = stream.map(index -> {

				EnergyCalibration calibration = energies.get(index).get();

				float score = scoreFitFast(fits, peakIndexes, spectrum, calibration);
				return new Pair<>(index, score);

			}).collect(Collectors.toList());
			
			
			//Sort the scores
			scores.sort((s1, s2) -> s1.second.compareTo(s2.second));
			Collections.reverse(scores);
			
			
			
			//Take energy pairs based on scored index until we've taken some % or the score has dropped below some % of the best score
			List<EnergyCalibration> filteredScores = new ArrayList<>();
			float bestScore = scores.get(0).second;

			for (Pair<Integer, Float> score : scores) {
				if (score.second < bestScore * SURVIVOR_CUTOFF) break;
				filteredScores.add(energies.get(score.first).get());
				if (filteredScores.size() >= MAX_ROUGH_SURVIVORS) break;
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
			SpectrumView spectrum,
			List<ITransitionSeries> tsList,
			FittingSolver solver,
			CurveFitter fitter,
			int dataWidth
		) {
			
		StreamExecutor<EnergyCalibration> scorer = new StreamExecutor<>("Evaluating Candidates", 5);
		scorer.setTask(energies, stream -> {
			
			//build a new model for experimenting with
			ThreadLocal<FittingSet> fits = ThreadLocal.withInitial(() -> fitModel(tsList, dataWidth));
			
			//Score each energy value using our observed stream
			List<Float> scores = stream.map(calibration -> {
				
				FittingResultSetView results;
				fits.get().getFittingParameters().setCalibration(calibration);
				results = solver.solve(new FittingSolverContext(spectrum, fits.get(), fitter));
				return scoreFitGood(results, spectrum);
				
			}).toList();
			
			
			
			//Find the best score
			float bestScore = -Float.MAX_VALUE;
			int bestIndex = 0;
			for (int i = 0; i < scores.size(); i++) {
				float score = scores.get(i);
				if (score > bestScore) {
					bestScore = score;
					bestIndex = i;
				}
			}

			EnergyCalibration best = energies.get().get(bestIndex);
			EnergyCalibration coarse = fineTune(best, spectrum, tsList, solver, fitter, COARSE_WINDOW, COARSE_STEP);
			return fineTune(coarse, spectrum, tsList, solver, fitter, FINE_WINDOW, FINE_STEP);

		});
		
		
		
		
		return scorer;
		
	}
	
	
	private static float scoreFitFast(FittingSetView fits, List<Integer> peakIndexes, SpectrumView spectrum, EnergyCalibration calibration) {
		float score = 0;

		FastPeakSearchingScorer scorer = new FastPeakSearchingScorer(spectrum, peakIndexes, calibration);

		// Each series should sit on its own peak, so candidates that cram several
		// series onto the same peak are penalized.
		Set<Integer> claimed = new HashSet<>();
		int duplicates = 0;

		// For each TS, accumulate the score. Also get the strongest transition
		// and find its closest peak. Add that peak index to `claimed`, and if
		// it already contained the entry, count this as a penalized duplicate.
		for (ITransitionSeries ts : fits.getVisibleTransitionSeries()) {
			if (ts.isVisible()) {
				score += Math.sqrt(scorer.score(ts));
				var strongest = ts.getStrongestTransition();
				if (!claimed.add(scorer.closestPeak(strongest))) {
					duplicates++;
				}
			}
		}
		score -= duplicates * DUPLICATE_CLAIM_PENALTY;

		// Enough duplicates can drive the score negative, which would invert the
		// proportional cutoff the caller applies to the best score
		return Math.max(score, 0f);
	}


	/**
	 * Scores how well a calibration's fit explains the given spectrum, as a kind of
	 * Poisson chi-squared over the residual. In this scoring system, higher is better, so we
	 * flip the sign when we return the value.
	 *
	 * We're effectively summing up squared error (r*r, like least squares) in units of
	 * noise (normally sqrt(spectrum), but we're already in units of squared error/residual)
	 */
	public static float scoreFitGood(FittingResultSetView results, SpectrumView spectrum) {
		Spectrum fit = results.getTotalFit();
		
		// Calculate the residual as a float[]
		float[] residual = SpectrumCalculations.subtractLists(spectrum, fit).backingArray();
		int n = spectrum.size();
		
		// Quick background estimate by sorting the spectrum and taking the median signal
		float[] sorted = residual.clone();
		Arrays.sort(sorted);
		float background = sorted[n / 2];

		float chi = 0f;
		for (int i = 0; i < n; i++) {
			float r = residual[i] - background;
			chi += (r * r) / Math.max(spectrum.get(i), 1f);
		}

		// Negated because every higher scores are better, but this is measuring/counting error
		return -chi;
	}
	
	
	/**
	 * A scored point on the refinement grid. Ordering is by score, then by position, so
	 * that a tie resolves the same way no matter what order the grid was evaluated in.
	 */
	private record Scored(float score, float min, float max) {}

	private static final Comparator<Scored> BY_SCORE = Comparator
			.comparingDouble(Scored::score)
			.thenComparingDouble(Scored::min)
			.thenComparingDouble(Scored::max);

	/**
	 * The (min, max) pairs to try when refining around a calibration. Stepping with an
	 * integer counter and multiplying out every value avoids floating point drift
	 */
	private static List<float[]> refinementGrid(EnergyCalibration centre, float window, float step) {
		int steps = Math.round((window * 2f) / step) + 1;
		List<float[]> grid = new ArrayList<>(steps * steps);
		for (int i = 0; i < steps; i++) {
			float min = centre.getMinEnergy() - window + i * step;
			for (int j = 0; j < steps; j++) {
				float max = centre.getMaxEnergy() - window + j * step;
				if (max <= min) continue;
				grid.add(new float[] { min, max });
			}
		}
		return grid;
	}

	private static EnergyCalibration fineTune(
			EnergyCalibration calibration,
			SpectrumView spectrum,
			List<ITransitionSeries> tsList,
			FittingSolver solver,
			CurveFitter fitter,
			float window,
			float step
		) {

		int dataWidth = calibration.getDataWidth();

		//build a new model for experimenting with
		FittingSet fits = fitModel(tsList, dataWidth);

		Optional<Scored> best = refinementGrid(calibration, window, step).stream()
				.map(pair -> {
					fits.getFittingParameters().setCalibration(pair[0], pair[1], dataWidth);
					var ctx = new FittingSolverContext(spectrum, fits, fitter);
					FittingResultSetView results = solver.solve(ctx);
					return new Scored(scoreFitGood(results, spectrum), pair[0], pair[1]);
				})
				.max(BY_SCORE);

		//an empty grid means there was nothing to improve on
		return best
				.map(s -> new EnergyCalibration(s.min(), s.max(), dataWidth))
				.orElse(calibration);
	}
	
	
	public static StreamExecutorSet<EnergyCalibration> propose(
			SpectrumView spectrum,
			List<ITransitionSeries> tsList,
			FittingSolver solver,
			CurveFitter fitter,
			int dataWidth
		) {

		StreamExecutor<List<EnergyCalibration>> rough = roughOptions(allEnergies(dataWidth, hasMultipleStrongLines(tsList)), spectrum, tsList, dataWidth);
		StreamExecutor<EnergyCalibration> quality = chooseFromRoughOptions(() -> rough.getResult().get(), spectrum, tsList, solver, fitter, dataWidth);
		rough.then(quality);
		
		return new StreamExecutorSet<>(rough, quality);
		
	}
	
}
