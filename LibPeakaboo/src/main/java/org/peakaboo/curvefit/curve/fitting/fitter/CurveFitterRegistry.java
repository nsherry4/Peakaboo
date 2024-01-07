package org.peakaboo.curvefit.curve.fitting.fitter;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class CurveFitterRegistry extends BoltPluginRegistry<CurveFitter> {

	
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
	
	private BoltJavaBuiltinLoader<CurveFitter> builtins;
	
	public CurveFitterRegistry() {
		super("curvefit");
		
		builtins = new BoltJavaBuiltinLoader<>(this, CurveFitter.class);
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


}
