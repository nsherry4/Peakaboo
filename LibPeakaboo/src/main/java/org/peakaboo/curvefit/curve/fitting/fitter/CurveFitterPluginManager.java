package org.peakaboo.curvefit.curve.fitting.fitter;

import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasink.plugin.JavaDataSinkPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class CurveFitterPluginManager extends BoltPluginManager<CurveFitter> {

	
	private static CurveFitterPluginManager SYSTEM;
	public static void init() {
		try {
			if (SYSTEM == null) {
				SYSTEM = new CurveFitterPluginManager();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load curve fit plugins", e);
		}
	}
	public static CurveFitterPluginManager system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	private BoltJavaBuiltinLoader<CurveFitter> builtins;
	
	public CurveFitterPluginManager() {
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
