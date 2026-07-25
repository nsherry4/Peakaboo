package org.peakaboo.curvefit.peak.search.scoring;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

/**
 * Detects peaks in the given data, and then scores each TransitionSeries based
 * on how close each of it's Transitions is
 */
public class FastPeakSearchingScorer implements FittingScorer {

	// Tolerance in energy when matching a peak and a transition
	private static final float MATCH_TOLERANCE = 0.10f;
	// Tolerance in channels when matching a peak and a transition.
	private static final float TOLERANCE_CHANNELS = 3f;
	// Applied to closeness score (generally in range 0-1) to make
	// the closeness score non-linear.
	private static final float CLOSENESS_EXPONENT = 6f;

	SpectrumView data;
	EnergyCalibration calibration;
	List<Integer> peakIndexes;
	float datamax;

	public FastPeakSearchingScorer(SpectrumView data, List<Integer> peakIndexes, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
		this.peakIndexes = peakIndexes;
		this.datamax = (float) Math.log1p(data.max());
	}
	
	@Override
	public float score(ITransitionSeries ts) {
		//Score the few strongest transitions, the spacing between them is telling
		List<Transition> transitions = new ArrayList<>(ts.getAllTransitions());
		transitions.sort((a, b) -> Float.compare(b.relativeIntensity, a.relativeIntensity));

		float totalWeight = 0;
		float weighted = 0;
		int count = Math.min(3, transitions.size());
		for (int i = 0; i < count; i++) {
			// For each of the top transitions, add up the relative
			// intensities and the score-adjusted intensities. At the
			// end we make a single ratio into the score
			Transition t = transitions.get(i);
			weighted += t.relativeIntensity * scoreTransition(t);
			totalWeight += t.relativeIntensity;
		}
		if (totalWeight == 0) {
			return 0;
		}
		return weighted / totalWeight;
	}

	/*
	 * A transition's score is two independent things multiplied together:
	 *  - Closeness: how near a transition is to the peak
	 *  - Intensity: how tall that peak is and therefore how much it matters.
	 */
	private float scoreTransition(Transition t) {
		// Nothing to match against in an empty spectrum
		if (datamax <= 0) { return 0; }

		int peakIndex = closestPeak(t);
		float peakEnergy = calibration.energyFromChannel(peakIndex);

		float delta = Math.abs(peakEnergy - t.energyValue);
		float tolerance = Math.max(MATCH_TOLERANCE, TOLERANCE_CHANNELS * calibration.energyPerChannel());
		float closeness = Math.max(0f, 1f - delta / tolerance);

		float intensity = (float) (Math.log1p(data.get(peakIndex)) / datamax);

		//final score is higher=better
		return (float) Math.pow(closeness, CLOSENESS_EXPONENT) * intensity;
	}
	
	/**
	 * The detected peak channel nearest this transition under the current calibration.
	 */
	public int closestPeak(Transition t) {
		float bestDelta = Float.MAX_VALUE;
		int bestPeak = 0;
		
		for (int i : peakIndexes) {
			float peak = calibration.energyFromChannel(i);
			float delta = Math.abs(peak - t.energyValue);
			if (delta < bestDelta) {
				bestPeak = i;
				bestDelta = delta;
			}
		}
		
		return bestPeak;
		
	}

}
