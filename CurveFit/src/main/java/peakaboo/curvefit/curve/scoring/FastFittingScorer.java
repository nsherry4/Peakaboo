package peakaboo.curvefit.curve.scoring;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

/**
 * Scores a TransitionSeries based on how well a rough, 
 * Transition-by-Transition fit estimate matches the 
 * given data 
 * @author NAS
 *
 */
public class FastFittingScorer implements Scorer {

	private ReadOnlySpectrum data;
	private FittingParameters parameters;
	
	public FastFittingScorer(ReadOnlySpectrum data, FittingParameters parameters) {
		this.data = data;
		this.parameters = parameters;
	}
	
	
	@Override
	public float score(TransitionSeries ts) {
		
		List<Transition> transitions = new ArrayList<>(ts.getAllTransitions());	
		
		//find the lowest multiplier as a constraint on signal fitted
		float lowestMult = Float.MAX_VALUE;
		if (transitions.size() == 0) { return 0; }
		
		for (Transition t : transitions) {
			
			
			int channel = parameters.getCalibration().channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			//add 1 for a little wiggle room, and to prevent /0 errors
			float channelHeight = 1+data.get(channel);
			
			float mult = channelHeight / t.relativeIntensity;
			lowestMult = Math.min(lowestMult, mult);
			
		}
		if (lowestMult == Float.MAX_VALUE) {
			return 0;
		}
	
		
		//scale each transition by the lowest mult, and find out how "snugly" 
		//each transition fits the data.
		float snugness = 0;
		float signal = 0;
		int count = 0;
		for (Transition t : transitions) {			
			
			float fit = t.relativeIntensity * lowestMult;
			
			int channel = 1+parameters.getCalibration().channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			//add 1 for a little wiggle room, and to prevent /0 errors
			float channelHeight = 1+data.get(channel);
			
			//we calculate how good the fit is
			snugness += fit / (float)channelHeight;
			signal += fit;
			count++;
			
		}
				
		snugness /= (float)count;
		signal /= (float)count;
		
		return signal * snugness;

	}



}
