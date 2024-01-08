package org.peakaboo.curvefit.peak.fitting;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.curvefit.peak.fitting.functions.ConvolvingVoigtFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.GaussianFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.LorentzFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class FittingFunctionRegistry extends BoltPluginRegistry<FittingFunction> {


	private static FittingFunctionRegistry SYSTEM;
	public static void init() {
		try {
			if (SYSTEM == null) {
				SYSTEM = new FittingFunctionRegistry();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load fitting function plugins", e);
		}
	}
	public static FittingFunctionRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	public FittingFunctionRegistry() {
		super("fitting-function");
		
		var builtins = new BoltJavaBuiltinLoader<>(this, FittingFunction.class);
		builtins.load(PseudoVoigtFittingFunction.class);
		builtins.load(ConvolvingVoigtFittingFunction.class);
		builtins.load(GaussianFittingFunction.class);
		builtins.load(LorentzFittingFunction.class);
		
		addLoader(builtins);
		
	}
	
	public BoltPluginPrototype<? extends FittingFunction> preset() {
		return this.getPrototypeForClass(PseudoVoigtFittingFunction.class).orElseThrow();
	}

	@Override
	public String getInterfaceName() {
		return "Fitting Function";
	}

	@Override
	public String getInterfaceDescription() {
		return "Fitting functions describe an ideal shape for a peak";
	}
	
}
