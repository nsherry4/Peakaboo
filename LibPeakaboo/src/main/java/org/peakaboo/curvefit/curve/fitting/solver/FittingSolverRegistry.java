package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.PeakabooPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginPreset;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class FittingSolverRegistry extends PeakabooPluginRegistry<FittingSolver> implements PluginPreset<FittingSolver> {

	
	private static FittingSolverRegistry SYSTEM;
	public static void init() {
		try {
			if (SYSTEM == null) {
				SYSTEM = new FittingSolverRegistry();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load fitting solver plugins", e);
		}
	}
	public static FittingSolverRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	public FittingSolverRegistry() {
		super("fittingsolver");
		
		var builtins = new BoltJavaBuiltinLoader<>(this, FittingSolver.class);
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
	@Override
	public PluginDescriptor<FittingSolver> getPreset() {
		return this.getByClass(GreedyFittingSolver.class).orElseThrow();
	}
	

}
