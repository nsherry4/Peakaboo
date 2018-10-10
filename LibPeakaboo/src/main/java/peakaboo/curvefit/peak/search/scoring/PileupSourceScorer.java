package peakaboo.curvefit.peak.search.scoring;

import cyclops.ReadOnlySpectrum;
import peakaboo.curvefit.curve.EnergyCalibration;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

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
	public float score(TransitionSeries ts) {
		
		if (ts.type != TransitionSeriesType.COMPOSITE) { return 1; }
	
		float sourceScore = 0;
		float tsCount = 0;
		for (TransitionSeries ots : ts.getBaseTransitionSeries()) {
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

	private float tsHeight(TransitionSeries ts) {
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
