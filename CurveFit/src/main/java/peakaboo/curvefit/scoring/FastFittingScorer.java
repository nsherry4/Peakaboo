package peakaboo.curvefit.scoring;

import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

/**
 * Scores a TransitionSeries based on 
 * @author NAS
 *
 */
public class FastFittingScorer implements Scorer {

	private ReadOnlySpectrum data;
	private EnergyCalibration calibration;
	
	public FastFittingScorer(ReadOnlySpectrum data, EnergyCalibration calibration) {
		this.data = data;
		this.calibration = calibration;
	}
	
	@Override
	public float score(TransitionSeries ts) {
		if (ts.getAllTransitions().size() == 0) { return 0; }
		
		//find the lowest multiplier as a constraint on signal fitted
		float lowestMult = Float.MAX_VALUE;
		for (Transition t : ts.getAllTransitions()) {
			
			int channel = calibration.channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			float channelHeight = data.get(channel);
			
			float mult = channelHeight / t.relativeIntensity;
			lowestMult = Math.min(lowestMult, mult);
		}
		if (lowestMult == Float.MAX_VALUE) {
			lowestMult = 0;
		}
	
		
		//multiply each transition's channel by the lowest multiplier
		float score = 0;
		for (Transition t : ts.getAllTransitions()) {			
			score += t.relativeIntensity * lowestMult;
		}
				
		return score;

	}



}
