package peakaboo.curvefit.peak.search.scoring;

import java.util.ArrayList;
import java.util.List;

import cyclops.ReadOnlySpectrum;
import peakaboo.curvefit.curve.FittingParameters;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;

/**
 * Scores a TransitionSeries based on how well a rough, 
 * Transition-by-Transition fit estimate matches the 
 * given data 
 * @author NAS
 *
 */
public class FastFittingScorer implements FittingScorer {

	private ReadOnlySpectrum data;
	private FittingParameters parameters;
	private float energy;
	private float max;
	
	
	public FastFittingScorer(float energy, ReadOnlySpectrum data, FittingParameters parameters) {
		this.data = data;
		this.parameters = parameters;
		this.energy = energy;
		this.max = data.max();
	}
	
	
	@Override
	public float score(TransitionSeries ts) {
		
		List<Transition> transitions = new ArrayList<>(ts.getAllTransitions());	
		
		//find the lowest multiplier as a constraint on signal fitted
		float lowestMult = Float.MAX_VALUE;
		if (transitions.size() == 0) { return 1; }
		
		for (Transition t : transitions) {
			if (t.relativeIntensity == 0) { continue; }
			
			int channel = parameters.getCalibration().channelFromEnergy(t.energyValue);
			if (channel >= data.size()) continue;
			if (channel < 0) continue;
			//add 1 for a little wiggle room, and to prevent /0 errors
			float channelHeight = 1+Math.max(0, data.get(channel));
			
			float mult = channelHeight / t.relativeIntensity;
			lowestMult = Math.min(lowestMult, mult);
			
		}
		if (lowestMult == Float.MAX_VALUE) {
			return 1;
		}
	
		
		//scale each transition by the lowest mult, and find out how "snugly" 
		//each transition fits the data.
		float snugness = 0;
		int count = 0;
		for (Transition t : transitions) {			
			if (Math.abs(t.energyValue - energy) > 0.25f) {
				continue;
			}
			
			//snugness/correctness of fit
			float fit = t.relativeIntensity * lowestMult;			
			snugness += fitPercent(fit, t.energyValue);
			
			count++;
			
		}
		
		if (count == 0) { return 0; }
		snugness /= (float)count;
		snugness = (float)(Math.sqrt(snugness));
			

		return Math.abs(snugness);

	}

	private float fitPercent(float fit, float energy) {
		int channel = parameters.getCalibration().channelFromEnergy(energy);
		if (channel >= data.size()) return 1f;
		if (channel < 0) return 1f;
		
		float channelHeight = 1+Math.max(0, data.get(channel));
		float fitPercent = fit / (float)channelHeight;
		return fitPercent;
	}


}
