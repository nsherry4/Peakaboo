package peakaboo.curvefit.scoring;

import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

public class FastSignalMatchScorer implements Scorer{

	private ReadOnlySpectrum data;
	private EnergyCalibration calibration;
	
	public FastSignalMatchScorer(ReadOnlySpectrum data, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
	}
	
	@Override
	public float score(TransitionSeries ts) {
		float height = 0;
		for (Transition t : ts.getAllTransitions()) {
			
			int channel = calibration.channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			height += data.get(channel);
		}
		return height;
	}

}
