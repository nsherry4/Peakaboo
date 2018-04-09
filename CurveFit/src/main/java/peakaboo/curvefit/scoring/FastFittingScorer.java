package peakaboo.curvefit.scoring;

import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

public class FastFittingScorer implements Scorer {

	private ReadOnlySpectrum data;
	private EnergyCalibration calibration;
	
	public FastFittingScorer(ReadOnlySpectrum data, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
	}
	
	@Override
	public float score(TransitionSeries ts) {

		float bestHeight = 0;
		for (Transition t : ts.getAllTransitions()) {
			
			int channel = calibration.channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			float channelHeight = data.get(channel);
			
			bestHeight = Math.max(bestHeight, channelHeight / t.relativeIntensity);
			
		}
		return bestHeight;

	}

}
