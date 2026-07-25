package org.peakaboo.controller.plotter.fitting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.SyntheticSpectrumFixture;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;

/**
 * Synthetic tests for {@link AutoEnergyCalibration}: generates spectra at
 * a known calibration, runs the auto-detection, and measures the error in eV.
 * Each test prints a one-line result so runs can be compared across commits.
 *
 * Run from the repo root with: mvn -pl LibPeakaboo -am test -Dtest=AutoEnergyCalibrationTest
 *
 * @author NAS
 */
public class AutoEnergyCalibrationTest {

	// 2048 channels at 10eV/channel — a typical detector setup
	private static final int WIDTH = 2048;
	private static final float MAX_KEV = 20.48f;

	@BeforeClass
	public static void setup() {
		FittingFunctionRegistry.init();
	}

	@Test
	public void multiElementClean() throws InterruptedException {
		runScenario("multi-clean", wellSeparated(), cal(0f, MAX_KEV), 20f, 1,
				0.03f, 0.03f);
	}

	@Test
	public void multiElementOffsetMin() throws InterruptedException {
		runScenario("multi-offset", wellSeparated(), cal(0.24f, MAX_KEV), 20f, 1,
				0.03f, 0.03f);
	}

	@Test
	public void singleElementClean() throws InterruptedException {
		runScenario("single-clean", weights(Element.Fe, TransitionShell.K, 900.0), cal(0f, MAX_KEV), 20f, 1,
				0.03f, 0.08f);
	}

	@Test
	public void singleElementOffsetMin() throws InterruptedException {
		// A single fitted element with two strong lines (Fe Ka + Kb) is enough to
		// search min energy rather than pinning it to 0
		runScenario("single-offset", weights(Element.Fe, TransitionShell.K, 900.0), cal(0.30f, MAX_KEV), 20f, 1,
				0.10f, 0.25f);
	}

	@Test
	public void dominantPeak() throws InterruptedException {
		// One huge peak and two trace elements — easy to satisfy the big peak and
		// ignore the small ones
		runScenario("dominant", weights(
				Element.Fe, TransitionShell.K, 5000.0,
				Element.Ca, TransitionShell.K, 40.0,
				Element.Ni, TransitionShell.K, 60.0), cal(0f, MAX_KEV), 20f, 1,
				0.03f, 0.05f);
	}

	@Test
	public void lowCounts() throws InterruptedException {
		runScenario("low-counts", weights(
				Element.Fe, TransitionShell.K, 40.0,
				Element.Sr, TransitionShell.K, 18.0,
				Element.Pb, TransitionShell.L, 8.0), cal(0f, MAX_KEV), 2f, 2,
				0.06f, 0.05f);
	}

	@Test
	public void unfittedElement() throws InterruptedException {
		// The spectrum contains a strong element the user hasn't fitted, which is
		// the normal case early in a workflow. The stray peak must not pull the
		// calibration off to where a fitted line lands on it.
		Map<ITransitionSeries, Double> synth = weights(
				Element.Fe, TransitionShell.K, 800.0,
				Element.Sr, TransitionShell.K, 350.0,
				Element.Ca, TransitionShell.K, 600.0);
		List<ITransitionSeries> fitted = List.of(
				SyntheticSpectrumFixture.series(Element.Fe, TransitionShell.K),
				SyntheticSpectrumFixture.series(Element.Sr, TransitionShell.K));
		runScenario("unfitted-extra", synth, fitted, cal(0f, MAX_KEV), 20f, 1,
				0.03f, 0.03f);
	}

	@Test
	public void noiseOnly() throws InterruptedException {
		// Worst case: an element is fitted but the spectrum is pure background, so
		// every stage-1 score is 0. We only care that this completes in reasonable
		// time and returns something.
		Spectrum data = synthesize(weights(Element.Fe, TransitionShell.K, 0.0), cal(0f, MAX_KEV), 20f, 3);
		List<ITransitionSeries> tsList = List.of(SyntheticSpectrumFixture.series(Element.Fe, TransitionShell.K));

		long start = System.currentTimeMillis();
		EnergyCalibration guessed = proposeBlocking(data, tsList);
		long elapsed = System.currentTimeMillis() - start;

		System.out.printf("%-14s guessed=(%.2f, %.2f)                          %6d ms%n",
				"noise-only", guessed.getMinEnergy(), guessed.getMaxEnergy(), elapsed);
		Assert.assertNotNull(guessed);
	}


	private void runScenario(
			String name,
			Map<ITransitionSeries, Double> weights,
			EnergyCalibration trueCal,
			float background,
			long seed,
			float tolMin,   // permitted error in keV, or negative to skip the assertion
			float tolMax
		) throws InterruptedException {
		runScenario(name, weights, new ArrayList<>(weights.keySet()), trueCal, background, seed, tolMin, tolMax);
	}

	/**
	 * As above, but the list of fitted series is given separately from the ones
	 * synthesized into the spectrum, so a scenario can include an element the
	 * user hasn't fitted.
	 */
	private void runScenario(
			String name,
			Map<ITransitionSeries, Double> weights,
			List<ITransitionSeries> fitted,
			EnergyCalibration trueCal,
			float background,
			long seed,
			float tolMin,
			float tolMax
		) throws InterruptedException {

		Spectrum data = synthesize(weights, trueCal, background, seed);

		long start = System.currentTimeMillis();
		EnergyCalibration guessed = proposeBlocking(data, fitted);
		long elapsed = System.currentTimeMillis() - start;

		Assert.assertNotNull("no calibration proposed for " + name, guessed);
		float errMin = Math.abs(guessed.getMinEnergy() - trueCal.getMinEnergy());
		float errMax = Math.abs(guessed.getMaxEnergy() - trueCal.getMaxEnergy());

		System.out.printf("%-14s errMin=%5.0f eV  errMax=%5.0f eV  (guessed %.2f, %.2f)  %6d ms%n",
				name, errMin * 1000, errMax * 1000, guessed.getMinEnergy(), guessed.getMaxEnergy(), elapsed);

		if (tolMin >= 0) {
			Assert.assertTrue(name + ": min energy off by " + errMin + " keV (tolerance " + tolMin + ")", errMin <= tolMin);
		}
		if (tolMax >= 0) {
			Assert.assertTrue(name + ": max energy off by " + errMax + " keV (tolerance " + tolMax + ")", errMax <= tolMax);
		}
	}

	/**
	 * Runs the auto-calibration and blocks until the last stage finishes.
	 */
	private static EnergyCalibration proposeBlocking(SpectrumView spectrum, List<ITransitionSeries> tsList) throws InterruptedException {
		var task = AutoEnergyCalibration.propose(spectrum, tsList, new GreedyFittingSolver(), new UnderCurveFitter(), WIDTH);
		CountDownLatch done = new CountDownLatch(1);
		task.last().addListener(event -> {
			if (event != Event.PROGRESS) done.countDown();
		});
		task.start();
		Assert.assertTrue("auto-calibration timed out", done.await(10, TimeUnit.MINUTES));
		return task.last().getResult().orElse(null);
	}

	/**
	 * Sum of curves at the given weights on top of a constant background, with
	 * Poisson-approximate noise (negative seed for noiseless). Same approach as
	 * {@link SyntheticSpectrumFixture} but at an arbitrary true calibration.
	 */
	private static Spectrum synthesize(Map<ITransitionSeries, Double> weights, EnergyCalibration trueCal, float background, long seed) {
		FittingSet fits = new FittingSet();
		fits.getFittingParameters().setCalibration(trueCal);
		for (ITransitionSeries ts : weights.keySet()) {
			fits.addTransitionSeries(ts);
		}

		Spectrum data = new ArraySpectrum(trueCal.getDataWidth(), background);
		for (CurveView curve : fits.getVisibleCurves()) {
			curve.scaleOnto(weights.get(curve.getTransitionSeries()).floatValue(), data);
		}

		if (seed >= 0) {
			Random rng = new Random(seed);
			float[] backing = data.backingArray();
			for (int i = 0; i < backing.length; i++) {
				float noisy = backing[i] + (float) (Math.sqrt(Math.max(backing[i], 1f)) * rng.nextGaussian());
				backing[i] = Math.max(noisy, 0f);
			}
		}
		return data;
	}

	private static EnergyCalibration cal(float min, float max) {
		return new EnergyCalibration(min, max, WIDTH);
	}

	private static Map<ITransitionSeries, Double> wellSeparated() {
		return weights(
				Element.Fe, TransitionShell.K, 800.0,
				Element.Sr, TransitionShell.K, 350.0,
				Element.Pb, TransitionShell.L, 150.0);
	}

	private static Map<ITransitionSeries, Double> weights(Object... seriesAndWeights) {
		Map<ITransitionSeries, Double> weights = new LinkedHashMap<>();
		for (int i = 0; i < seriesAndWeights.length; i += 3) {
			Element element = (Element) seriesAndWeights[i];
			TransitionShell shell = (TransitionShell) seriesAndWeights[i + 1];
			double weight = ((Number) seriesAndWeights[i + 2]).doubleValue();
			weights.put(SyntheticSpectrumFixture.series(element, shell), weight);
		}
		return weights;
	}

}
