package peakaboo.curvefit.fitting.parameters;

import peakaboo.curvefit.fitting.functions.FittingFunction;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transitionseries.TransitionSeriesType;

public interface FittingParameters {

	public FittingFunction forTransition(Transition transition, TransitionSeriesType type);
	public FittingFunction forEscape(Transition transition, Transition escape, Element e, TransitionSeriesType type);
	
	
}
