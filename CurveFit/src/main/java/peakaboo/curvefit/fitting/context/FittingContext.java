package peakaboo.curvefit.fitting.context;

import peakaboo.curvefit.fitting.functions.FittingFunction;
import peakaboo.curvefit.fitting.parameters.FittingParameters;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transitionseries.TransitionSeriesType;

public interface FittingContext {

	float getFWHM();
	float getHeight();
	float getEnergy();
	
}
