package org.peakaboo.curvefit.curve.fitting.fitter;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginPreset;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class CurveFitterRegistry extends BoltPluginRegistry<CurveFitter> implements PluginPreset<CurveFitter> {

	
	private static CurveFitterRegistry SYSTEM;
	public static void init() {
		try {
			if (SYSTEM == null) {
				SYSTEM = new CurveFitterRegistry();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load curve fit plugins", e);
		}
	}
	public static CurveFitterRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	public CurveFitterRegistry() {
		super("curvefit");
		
		var builtins = new BoltJavaBuiltinLoader<>(this, CurveFitter.class);
		builtins.load(UnderCurveFitter.class);
		builtins.load(OptimizingCurveFitter.class);
		builtins.load(LeastSquaresCurveFitter.class);
		
		addLoader(builtins);
		
	}

	@Override
	public String getInterfaceName() {
		return "Curve Fitting";
	}

	@Override
	public String getInterfaceDescription() {
		return "Curve fitters match curves for individual Transition Series to real signal";
	}
	@Override
	public BoltPluginPrototype<? extends CurveFitter> getPreset() {
		return this.getByClass(UnderCurveFitter.class).orElseThrow();
	}


}
