package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver.FittingSolverContext;
import org.peakaboo.curvefit.curve.fitting.solver.SyntheticSpectrumFixture.Scenario;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class FittingSolverCorrectnessTest {

	@BeforeClass
	public static void initRegistry() {
		FittingFunctionRegistry.init();
	}

	private static List<FittingSolver> solvers() {
		return List.of(new IterativeFittingSolver());
	}

	private static void assertRecovery(FittingSolver solver, Scenario scenario, double relativeTolerance) {
		Spectrum data = scenario.synthesize(0f, -1);
		FittingResultSetView results = solver.solve(scenario.context(data));
		Map<ITransitionSeries, Double> recovered = SyntheticSpectrumFixture.recoveredWeights(results);

		for (var entry : scenario.trueWeights.entrySet()) {
			double truth = entry.getValue();
			double found = recovered.get(entry.getKey());
			Assert.assertTrue("Weight for " + entry.getKey() + " must be non-negative, was " + found, found >= 0);
			if (truth > 0) {
				Assert.assertEquals("Weight for " + entry.getKey() + " in scenario " + scenario.name,
						truth, found, truth * relativeTolerance);
			}
		}
	}

	/**
	 * On noiseless data with no background, the iterative solver's correction
	 * passes must recover the true weights closely, even with overlapping
	 * curves that a single greedy pass would misattribute.
	 */
	@Test
	public void testNoiselessRecovery() {
		List<Scenario> scenarios = List.of(
				SyntheticSpectrumFixture.wellSeparated(),
				SyntheticSpectrumFixture.overlapChain(),
				SyntheticSpectrumFixture.pathologicalOverlap());
		for (FittingSolver solver : solvers()) {
			for (Scenario scenario : scenarios) {
				assertRecovery(solver, scenario, 0.01);
			}
		}
	}

	/**
	 * Elements not present in the data must recover (close to) zero weight on
	 * noiseless data.
	 */
	@Test
	public void testAbsentElementsRecoverZero() {
		Scenario scenario = SyntheticSpectrumFixture.absentElements();
		Spectrum data = scenario.synthesize(0f, -1);
		for (FittingSolver solver : solvers()) {
			FittingResultSetView results = solver.solve(scenario.context(data));
			Map<ITransitionSeries, Double> recovered = SyntheticSpectrumFixture.recoveredWeights(results);
			for (var entry : scenario.trueWeights.entrySet()) {
				if (entry.getValue() == 0) {
					double found = recovered.get(entry.getKey());
					Assert.assertTrue("Absent element " + entry.getKey() + " must recover ~zero weight, was " + found,
							found < 1.0);
				}
			}
		}
	}

	/**
	 * Weights must be non-negative even on noisy data with unmodelled background.
	 */
	@Test
	public void testNonNegativityUnderNoise() {
		Scenario scenario = SyntheticSpectrumFixture.overlapChain();
		for (long seed = 0; seed < 5; seed++) {
			Spectrum data = scenario.synthesize(20f, seed);
			for (FittingSolver solver : solvers()) {
				FittingResultSetView results = solver.solve(scenario.context(data));
				for (var fit : results.getFits()) {
					Assert.assertTrue(fit.getCurveScale() >= 0);
				}
			}
		}
	}

	/**
	 * No visible curves: solvers must return an empty result without throwing.
	 */
	@Test
	public void testNoCurves() {
		FittingSet empty = new FittingSet();
		empty.getFittingParameters().setCalibration(SyntheticSpectrumFixture.CALIBRATION);
		Spectrum data = new ArraySpectrum(SyntheticSpectrumFixture.CALIBRATION.getDataWidth(), 100f);
		var ctx = new FittingSolverContext(data, empty, new UnderCurveFitter());
		for (FittingSolver solver : solvers()) {
			FittingResultSetView results = solver.solve(ctx);
			Assert.assertTrue(results.getFits().isEmpty());
		}
	}

	/**
	 * All-zero data must produce all-zero weights.
	 */
	@Test
	public void testZeroData() {
		Scenario scenario = SyntheticSpectrumFixture.overlapChain();
		Spectrum data = new ArraySpectrum(SyntheticSpectrumFixture.CALIBRATION.getDataWidth());
		for (FittingSolver solver : solvers()) {
			FittingResultSetView results = solver.solve(scenario.context(data));
			for (var fit : results.getFits()) {
				Assert.assertEquals(0f, fit.getCurveScale(), 1e-6f);
			}
		}
	}

	/**
	 * Curves whose energies fall outside the calibrated range have no intense
	 * channels; solvers must handle this without throwing.
	 */
	@Test
	public void testOutOfCalibrationCurves() {
		// Sr K-alpha is ~14.1 keV and Pb L-alpha ~10.5 keV; a 0-5 keV calibration
		// puts both out of range
		Scenario scenario = SyntheticSpectrumFixture.wellSeparated();
		scenario.fittings.getFittingParameters().setCalibration(new EnergyCalibration(0f, 5f, 2048));
		Spectrum data = new ArraySpectrum(2048, 10f);
		for (FittingSolver solver : solvers()) {
			FittingResultSetView results = solver.solve(scenario.context(data));
			for (var fit : results.getFits()) {
				Assert.assertTrue(Float.isFinite(fit.getCurveScale()));
				Assert.assertTrue(fit.getCurveScale() >= 0);
			}
		}
	}

	/**
	 * A cache shared across solves of different spectra must produce results
	 * identical to fresh per-solve caches.
	 */
	@Test
	public void testSharedCacheMatchesFreshCache() {
		Scenario scenario = SyntheticSpectrumFixture.overlapChain();
		int[] channels = FittingSolver.getIntenseChannels(scenario.fittings.getVisibleCurves());
		SolverCache shared = new SolverCache();

		for (FittingSolver solver : solvers()) {
			for (long seed = 0; seed < 3; seed++) {
				Spectrum data = scenario.synthesize(10f, seed);

				FittingResultSetView fresh = solver.solve(scenario.context(data));

				var sharedCtx = new FittingSolverContext(data, scenario.fittings, new UnderCurveFitter(), channels);
				sharedCtx.cache = shared;
				FittingResultSetView cached = solver.solve(sharedCtx);

				for (int i = 0; i < fresh.getFits().size(); i++) {
					Assert.assertEquals(
							fresh.getFits().get(i).getCurveScale(),
							cached.getFits().get(i).getCurveScale(),
							1e-6f);
				}
			}
		}
	}

	/**
	 * Sharing a cache across a parallel stream (as Mapping does) must give the
	 * same results as solving serially.
	 */
	@Test
	public void testSharedCacheParallelStream() {
		Scenario scenario = SyntheticSpectrumFixture.overlapChain();
		int[] channels = FittingSolver.getIntenseChannels(scenario.fittings.getVisibleCurves());
		int pixels = 100;

		List<Spectrum> spectra = new ArrayList<>();
		for (int i = 0; i < pixels; i++) {
			spectra.add(scenario.synthesize(10f, i));
		}

		for (FittingSolver solver : solvers()) {
			float[][] serial = new float[pixels][];
			for (int i = 0; i < pixels; i++) {
				FittingResultSetView results = solver.solve(scenario.context(spectra.get(i)));
				serial[i] = weightsOf(results);
			}

			SolverCache shared = new SolverCache();
			float[][] parallel = new float[pixels][];
			IntStream.range(0, pixels).parallel().forEach(i -> {
				var ctx = new FittingSolverContext(spectra.get(i), scenario.fittings, new UnderCurveFitter(), channels);
				ctx.cache = shared;
				parallel[i] = weightsOf(solver.solve(ctx));
			});

			for (int i = 0; i < pixels; i++) {
				Assert.assertArrayEquals("pixel " + i + " for solver " + solver, serial[i], parallel[i], 1e-6f);
			}
		}
	}

	/**
	 * The iterative solver's bounds/overlap data depends only on the curve
	 * list, so a shared cache must compute it once and reuse the same instance
	 * across every solve, not recompute it per pixel.
	 */
	@Test
	public void testSharedCacheComputesBoundsOnce() {
		Scenario scenario = SyntheticSpectrumFixture.overlapChain();
		int[] channels = FittingSolver.getIntenseChannels(scenario.fittings.getVisibleCurves());
		SolverCache shared = new SolverCache();
		FittingSolver solver = new IterativeFittingSolver();

		IterativeFittingSolver.IterativeBounds first = null;
		for (long seed = 0; seed < 3; seed++) {
			Spectrum data = scenario.synthesize(10f, seed);
			var ctx = new FittingSolverContext(data, scenario.fittings, new UnderCurveFitter(), channels);
			ctx.cache = shared;
			solver.solve(ctx);

			IterativeFittingSolver.IterativeBounds bounds = IterativeFittingSolver.IterativeBounds.cached(ctx);
			if (first == null) {
				first = bounds;
			} else {
				Assert.assertSame("bounds must be computed once and reused across shared-cache solves", first, bounds);
			}
		}
	}

	private static float[] weightsOf(FittingResultSetView results) {
		float[] weights = new float[results.getFits().size()];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = results.getFits().get(i).getCurveScale();
		}
		return weights;
	}

}
