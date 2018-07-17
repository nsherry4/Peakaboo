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
public class ProportionalPileupScorer implements FittingScorer {

	private ReadOnlySpectrum data;
	private EnergyCalibration calibration;
	
	public ProportionalPileupScorer(ReadOnlySpectrum data, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
	}
	
	@Override
	public float score(TransitionSeries ts) {
		
		if (ts.type != TransitionSeriesType.COMPOSITE) { return 1; }
		
		float sum = sumTransitionSeries(ts);
		
		float baseSum = 0f;
		for (TransitionSeries baseTS : ts.getBaseTransitionSeries()) {
			baseSum += sumTransitionSeries(baseTS);
		}
		baseSum /= ts.getBaseTransitionSeries().size();

		//if the summation takes up more signal than the average base ts sum, score it lower
		if (sum  > baseSum) {
			return 0.1f;
		} else {
			return 1f;
		}
		
	}
	
	private float sumTransitionSeries(TransitionSeries ts) {
		float sum = 0f;
		for (Transition t : ts) {
			int channel = calibration.channelFromEnergy(t.energyValue);
			if (channel > 0 && channel < data.size()) {
				sum += data.get(channel);
			}
		}
		return sum;
	}

	
	
}
