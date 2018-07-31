package peakaboo.curvefit.peak.search.scoring;

import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import scitypes.ReadOnlySpectrum;

/**
 * Scores primary {@link TransitionSeries} as 1, penalizes pileup if 
 * it's signal is stronger than the originating base {@link TransitionSeries}
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
		
		float score = (float) Math.sqrt(Math.sqrt(sourceScore));
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
