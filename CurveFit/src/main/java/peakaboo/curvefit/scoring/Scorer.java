package peakaboo.curvefit.scoring;

import peakaboo.curvefit.transition.TransitionSeries;

public interface Scorer {

	float score(TransitionSeries ts);
	
}
