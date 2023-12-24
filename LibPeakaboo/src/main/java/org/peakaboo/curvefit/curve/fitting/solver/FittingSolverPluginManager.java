package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class FittingSolverPluginManager extends BoltPluginManager<FittingSolver> {

	
	private static FittingSolverPluginManager SYSTEM;
	public static void init() {
		try {
			if (SYSTEM == null) {
				SYSTEM = new FittingSolverPluginManager();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load fitting solver plugins", e);
		}
	}
	public static FittingSolverPluginManager system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	private BoltJavaBuiltinLoader<FittingSolver> builtins;
	
	public FittingSolverPluginManager() {
		super("fittingsolver");
		
		builtins = new BoltJavaBuiltinLoader<>(this, FittingSolver.class);
		builtins.load(GreedyFittingSolver.class);
		builtins.load(OptimizingFittingSolver.class);
		builtins.load(MultisamplingOptimizingFittingSolver.class);
		
		addLoader(builtins);
		
	}

	@Override
	public String getInterfaceName() {
		return "Fitting Solver";
	}

	@Override
	public String getInterfaceDescription() {
		return "Fitting solvers determine how to match signal with curves for overlapping Transition Series";
	}

}
