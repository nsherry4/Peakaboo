package org.peakaboo.curvefit.peak.search.scoring;

import java.util.List;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.peak.search.searcher.DerivativePeakSearcher;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;

/**
 * Detects peaks in the given data, and then scores each TransitionSeries based
 * on how close each of it's Transitions is
 */
public class FastPeakSearchingScorer implements FittingScorer {

	ReadOnlySpectrum data;
	EnergyCalibration calibration;
	List<Integer> peakIndexes;
	float datamax;
	
	public FastPeakSearchingScorer(ReadOnlySpectrum data, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
		this.peakIndexes = new DerivativePeakSearcher().search(data);
		this.datamax = (float) Math.log1p(data.max());
	}
	
	@Override
	public float score(ITransitionSeries ts) {
		float score = 0;

		Transition t = ts.getStrongestTransition();
		int peakIndex = closestPeak(t);
		float peakEnergy = calibration.energyFromChannel(peakIndex);
		
		float scoreDelta = Math.abs(peakEnergy - t.energyValue);
		float scoreSignal = (float) (datamax - Math.log1p(data.get(peakIndex)));
		score = scoreDelta * scoreSignal;
		
		//final score is higher=better
		if (score > 1) {
			return 0;
		}
		score = (float) Math.pow(1f - score, 10);
		return score;
	}
	
	private int closestPeak(Transition t) {
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
