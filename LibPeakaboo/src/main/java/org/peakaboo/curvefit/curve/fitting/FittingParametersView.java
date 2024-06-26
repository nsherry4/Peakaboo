package org.peakaboo.curvefit.curve.fitting;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.EscapeFittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.fitting.TransitionFittingContext;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;

public interface FittingParametersView {

	FittingParametersView copy();

	/**
	 * The FWHM value for a {@link Transition} changes based on the energy level. This method 
	 * calculates the FWHM value which should be used for this energy level.
	 */
	float getFWHM(float energy);
	
	/**
	 * The FWHM value for a {@link Transition} changes based on the energy level. This method 
	 * calculates the FWHM value which should be used for this energy level.
	 */
	default float getFWHM(Transition t) {
		return getFWHM(t.energyValue);
	}
	
	float getFWHMBase();

	EnergyCalibration getCalibration();

	DetectorMaterialType getDetectorMaterial();

	PluginDescriptor<FittingFunction> getFittingFunction();

	boolean getShowEscapePeaks();

	
	default FittingFunction forTransition(Transition transition) {
		FittingContext context = new TransitionFittingContext(this, transition);
		return buildFunction(this, context);
	}

	default FittingFunction forEscape(Transition transition, Transition escape, Element element) {
		FittingContext context = new EscapeFittingContext(this, transition, escape, element);
		return buildFunction(this, context);
	}
	
	public static FittingFunction buildFunction(FittingParametersView params, FittingContext context) {
		FittingFunction function;
		
		var created = params.getFittingFunction().create();
		if (created.isPresent()) {
			function = created.get();
		} else {
			PeakabooLog.get().log(Level.SEVERE, "Failed to create fitting function, using default");
			function = FittingFunctionRegistry.system().getPresetInstance();		
		}
		function.initialize(context);
		return function;

	}

}