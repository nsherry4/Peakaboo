package peakaboo.curvefit.fitting.parameters;

import peakaboo.curvefit.fitting.context.FittingContext;
import peakaboo.curvefit.fitting.context.StandardFittingContext;
import peakaboo.curvefit.fitting.functions.FittingFunction;
import peakaboo.curvefit.fitting.functions.PseudoVoigtFittingFunction;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transitionseries.TransitionSeriesType;

public class StandardFittingParameters implements FittingParameters {

	@Override
	public FittingFunction forTransition(Transition transition, TransitionSeriesType type) {
		FittingContext context = new StandardFittingContext(this, transition, type);
		return buildFunction(context);
	}

	@Override
	public FittingFunction forEscape(Transition transition, Transition escape, Element element, TransitionSeriesType type) {
		FittingContext context = new StandardFittingContext(this, transition, escape, element, type);
		return buildFunction(context);
	}

	private FittingFunction buildFunction(FittingContext context) {
		//TODO: This should be parameterized instead of being hard-coded
		FittingFunction function = new PseudoVoigtFittingFunction();
		function.initialize(context);
		return function;
	}
	
	
	
}
