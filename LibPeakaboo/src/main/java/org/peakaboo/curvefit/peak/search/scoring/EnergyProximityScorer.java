package org.peakaboo.curvefit.peak.search.scoring;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingParametersView;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;


/**
 * Scores a TransitionSeries based on how close it is to a given energy 
 * value. Individual transitions are scored by the distance from their 
 * energy level as well as their relative intensity, and the best score
 * is chosen to represent the whole TransitionSeries.
 * @author NAS
 *
 */
public class EnergyProximityScorer implements FittingScorer {

	private float energy;
	private FittingParametersView parameters;
	
	public EnergyProximityScorer(float energy, FittingParametersView parameters) {
		this.energy = energy;
		this.parameters = parameters;
	}

	@Override
	public float score(ITransitionSeries ts) {
		
		List<Transition> transitions = new ArrayList<>(ts.getAllTransitions());
		
		float maxRel = Float.MIN_VALUE;
		for (Transition t : transitions) {
			maxRel = Math.max(maxRel, t.relativeIntensity);
		}
		
		EnergyCalibration calibration = parameters.getCalibration();
		
		float score = 0;
		for (Transition t : transitions) {
			score += proxScore(t, maxRel, calibration);
		}

		score *= score;
		return score;
		
		
		
	}
	
	private float proxScore(Transition t, float maxRel, EnergyCalibration calibration) {
		float proxScore = 0;
		if (t.energyValue < calibration.getMinEnergy() || t.energyValue > calibration.getMaxEnergy()) {
			return 0;
		}
		
		proxScore = Math.abs(t.energyValue - this.energy);
		
		//More precision than the 2x energy per channel is just noise, don't 
		//reward the 0.0001keV fit over the 0.001keV fit...
		proxScore = Math.max(proxScore, calibration.energyPerChannel()*2f);
		
		//Because larger scores are better
		proxScore = calibration.energyPerChannel()*10 - proxScore;
		if (proxScore <= 0) {
			return 0;
		}
		
		proxScore *= t.relativeIntensity / maxRel;
		return proxScore;
	}

}
