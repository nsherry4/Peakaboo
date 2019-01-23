package org.peakaboo.curvefit.peak.search.scoring;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;

import cyclops.ReadOnlySpectrum;

/**
 * Scores a TransitionSeries based on a rough calculation of
 * how much signal it would match
 * @author NAS
 *
 */
public class FastSignalMatchScorer implements FittingScorer{

	private ReadOnlySpectrum data;
	private EnergyCalibration calibration;
	
	public FastSignalMatchScorer(ReadOnlySpectrum data, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
	}
	
	@Override
	public float score(ITransitionSeries ts) {
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
