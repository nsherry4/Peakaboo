package peakaboo.curvefit.scoring;

import java.util.List;

import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

/**
 * Scores a TransitionSeries based on how well a rough, 
 * Transition-by-Transition fit estimate fits the given
 * data 
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
		
		
		//find the lowest multiplier as a constraint on signal fitted
		float lowestMult = Float.MAX_VALUE;
		int count = 0;
		List<Transition> transitions = ts.getAllTransitions();
		if (transitions.size() == 0) { return 0; }
		
		for (Transition t : transitions) {
			
			
			int channel = calibration.channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			//add 1 for a little wiggle room, and to prevent /0 errors
			float channelHeight = 1+data.get(channel);
			
			float mult = channelHeight / t.relativeIntensity;
			lowestMult = Math.min(lowestMult, mult);
			count++;
		}
		if (lowestMult == Float.MAX_VALUE) {
			return 0;
		}
	
		
		//scale each transition by the lowest mult, and find out how "snugly" 
		//each transition fits the data.
		float score = 0;
		for (Transition t : transitions) {			
			
			float fit = t.relativeIntensity * lowestMult;
			
			int channel = 1+calibration.channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			//add 1 for a little wiggle room, and to prevent /0 errors
			float channelHeight = 1+data.get(channel);
			
			//we calculate how good the fit is
			score += fit / (float)channelHeight;
			
		}
				
		return score/(float)count;

	}



}
