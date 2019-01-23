package org.peakaboo.curvefit.peak.search.scoring;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

import cyclops.ReadOnlySpectrum;

/**
 * Prefers pileup peaks which are a small percent of the 
 * strength of their source peaks.
 * @author NAS
 *
 */
public class PileupSourceScorer implements FittingScorer {

	private ReadOnlySpectrum data;
	private EnergyCalibration calibration;
	
	public PileupSourceScorer(ReadOnlySpectrum data, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
	}
	
	@Override
	public float score(ITransitionSeries ts) {
		
		if (ts.getShell() != TransitionShell.COMPOSITE) { return 1; }
	
		float sourceScore = 0;
		float tsCount = 0;
		for (ITransitionSeries ots : ts.getPrimaryTransitionSeries()) {
			sourceScore += tsHeight(ots);
			tsCount++;
		}
		sourceScore /= tsCount;
		sourceScore /= data.max();
		
		float thisScore = tsHeight(ts);
		thisScore /= data.max();
		
		
		float score = 1f - (thisScore / sourceScore);
		score = Math.max(1f, Math.min(0f, score));
		
		
		score = sourceScore;
		score = (float) Math.pow(sourceScore, 2);
		return score;
		
	}

	private float tsHeight(ITransitionSeries ts) {
		float height = 0;
		for (Transition t : ts.getAllTransitions()) {
			int channel = calibration.channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			height = Math.max(data.get(channel), height);
		}
		return height;
	}
	
	
}
