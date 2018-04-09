package peakaboo.curvefit.scoring;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import peakaboo.curvefit.fitting.Curve;
import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

public class EnergyProximityScorer implements Scorer {

	private float energy;
			
	public EnergyProximityScorer(float energy) {
		this.energy = energy;
	}

	@Override
	public float score(TransitionSeries ts) {
		
		//curve.setTransitionSeries(ts);
		
		float maxRel = 0f;
		for (Transition t : ts.getAllTransitions()) {
			maxRel = Math.max(maxRel, t.relativeIntensity);
		}
		
		float bestScore = 0l;
		float proxScore = 0;
		for (Transition t : ts.getAllTransitions()) {
			proxScore = Math.abs(t.energyValue - this.energy);
			proxScore *= t.relativeIntensity / maxRel;
			bestScore = Math.max(proxScore, bestScore);
		}
		
		return bestScore;		
		
		
	}

}
