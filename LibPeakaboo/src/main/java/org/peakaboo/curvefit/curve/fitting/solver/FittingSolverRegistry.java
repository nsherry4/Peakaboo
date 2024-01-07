package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.controller.session.v2.SavedPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class FittingSolverRegistry extends BoltPluginRegistry<FittingSolver> {

	
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
	
	private BoltJavaBuiltinLoader<FittingSolver> builtins;
	
	public FittingSolverRegistry() {
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
	
	
	public static Optional<FittingSolver> fromSaved(SavedPlugin saved) {
		var proto = FittingSolverRegistry.system().getByUUID(saved.uuid);
		if (proto == null) {
			return Optional.empty();
		}
		var solver = proto.create();
		return Optional.of(solver);
	}

}
