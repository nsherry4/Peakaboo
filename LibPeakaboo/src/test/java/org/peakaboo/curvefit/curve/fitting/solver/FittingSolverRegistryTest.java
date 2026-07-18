package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Verifies that every expected solver is registered and instantiable. Plugin
 * instantiation failures (e.g. a missing no-arg constructor) only log warnings
 * at runtime, so this guards against solvers silently vanishing from the UI.
 */
public class FittingSolverRegistryTest {

	@BeforeClass
	public static void init() {
		FittingSolverRegistry.init();
	}

	@Test
	public void testAllSolversRegisteredAndInstantiable() {
		List<String> names = FittingSolverRegistry.system().getPlugins().stream()
				.map(descriptor -> {
					var instance = descriptor.create();
					Assert.assertTrue("Plugin " + descriptor.getName() + " must be instantiable",
							instance.isPresent());
					return instance.get().pluginName();
				})
				.toList();

		for (String expected : List.of(
				"Greedy",
				"Iterative",
				"Optimizing",
				"MultiSampling")) {
			Assert.assertTrue("Registry must contain solver: " + expected + ", has: " + names,
					names.contains(expected));
		}
	}

}
