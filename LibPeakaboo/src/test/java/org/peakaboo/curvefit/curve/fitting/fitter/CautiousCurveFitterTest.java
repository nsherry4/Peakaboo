package org.peakaboo.curvefit.curve.fitting.fitter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.KrausePeakTable;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class CautiousCurveFitterTest {

	@BeforeClass
	public static void initRegistry() {
		FittingFunctionRegistry.init();
	}

	// Fe K-alpha is ~6.4 keV. A 0-5 keV calibration puts it out of range.
	private static final EnergyCalibration OUT_OF_RANGE_CALIB = new EnergyCalibration(0f,  5f, 2048);
	private static final EnergyCalibration IN_RANGE_CALIB     = new EnergyCalibration(0f, 20f, 2048);

	private CurveView makeKCurve(Element element, EnergyCalibration calibration) {
		FittingSet fittingSet = new FittingSet();
		fittingSet.getFittingParameters().setCalibration(calibration);
		fittingSet.addTransitionSeries(new KrausePeakTable().get(element, TransitionShell.K));
		return fittingSet.getCurves().get(0);
	}

	private CurveView makeFeKCurve(EnergyCalibration calibration) {
		return makeKCurve(Element.Fe, calibration);
	}

	private float fit(CurveView curve, Spectrum data) {
		return new CautiousCurveFitter()
				.fit(new CurveFitter.CurveFitterContext(data, curve))
				.getCurveScale();
	}

	private float fitWith(CurveFitter fitter, CurveView curve, Spectrum data) {
		return fitter.fit(new CurveFitter.CurveFitterContext(data, curve)).getCurveScale();
	}

	/**
	 * Fe at 500 and Mn at 40, with Fe then drawn back out at an inflated scale: an
	 * unfloored remainder of the kind IterativeFittingSolver produces, where Mn's
	 * K-beta window sits below zero while its K-alpha core is untouched.
	 */
	private Spectrum contestedMnRemainder(CurveView fe, CurveView mn, float feOverSubtraction) {
		Spectrum data = new ArraySpectrum(IN_RANGE_CALIB.getDataWidth());
		fe.scaleOnto(500f, data);
		mn.scaleOnto(40f, data);
		fe.scaleOnto(-feOverSubtraction, data);
		return data;
	}


	// ---- the invariants every fitter in this family holds ----

	@Test
	public void testFitOutOfEnergyRange() {
		CurveView curve = makeFeKCurve(OUT_OF_RANGE_CALIB);
		Assert.assertEquals(0, curve.getIntenseChannelList().length);

		Spectrum data = new ArraySpectrum(OUT_OF_RANGE_CALIB.getDataWidth());

		Assert.assertEquals(0f, fit(curve, data), 0f);
	}

	@Test
	public void testFitRecoverKnownScale() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);
		float K = 500f;
		Spectrum data = SpectrumCalculations.multiplyBy(curve.get(), K);

		Assert.assertEquals(K, fit(curve, data), 1f);
	}

	@Test
	public void testFitZeroDataReturnsZeroScale() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);
		Spectrum data = new ArraySpectrum(IN_RANGE_CALIB.getDataWidth());

		Assert.assertEquals(0f, fit(curve, data), 0.001f);
	}

	@Test
	public void testFitScaleIsNonNegative() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);
		Spectrum data = new ArraySpectrum(IN_RANGE_CALIB.getDataWidth(), -1000f);

		Assert.assertTrue(fit(curve, data) >= 0f);
	}

	/** Clamping is a no-op where the data never goes negative. */
	@Test
	public void testMatchesUnclampedWhenNothingOvershoots() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);
		Spectrum data = SpectrumCalculations.multiplyBy(curve.get(), 500f);

		float unclamped = fitWith(new UnclampedCurveFitter(), curve, data);

		Assert.assertEquals(unclamped, fit(curve, data), 1e-4f);
	}


	// ---- what the clamp buys us ----

	/**
	 * Mn's true weight is 40 and its K-alpha peak is genuinely in the data;
	 * over-subtracting Fe only digs a pit around its K-beta. Plain least squares
	 * still recovers most of Mn, so a collapse toward zero is the penalty being
	 * charged for Fe's mistake rather than Mn being absent.
	 * <p>
	 * Across Fe 500 / 520 / 540 / 560: unclamped runs 40.00 / 16.59 / 0.00 / 0.00,
	 * this fitter 40.00 / 35.10 / 34.34 / 34.13, plain least squares 40.00 / 38.65
	 * / 37.29 / 35.94. The ~5% shortfall is the over-drawn K-beta window still
	 * contributing 9*c^2 to the denominator of the solve while contributing
	 * nothing to its numerator.
	 */
	@Test
	public void testContestedCurveSurvives() {
		CurveView fe = makeFeKCurve(IN_RANGE_CALIB);
		CurveView mn = makeKCurve(Element.Mn, IN_RANGE_CALIB);
		Spectrum remainder = contestedMnRemainder(fe, mn, 530f);

		float unclamped = fitWith(new UnclampedCurveFitter(), mn, remainder);
		float unbiased = fitWith(new LeastSquaresCurveFitter(), mn, remainder);
		float cautious = fit(mn, remainder);

		Assert.assertTrue("unbiased fit should recover most of Mn's 40", unbiased > 30f);
		Assert.assertTrue("unclamped fitter should be heavily suppressed", unclamped < 0.25f * unbiased);
		Assert.assertTrue(
				"cautious (" + cautious + ") should hold most of unbiased (" + unbiased + ")",
				cautious > 0.85f * unbiased);
	}

	/**
	 * As a neighbour's over-subtraction grows smoothly, an unclamped fit falls off
	 * a cliff to exactly zero and stays there. Clamping removes the cliff rather
	 * than moving it, so Mn should survive the whole sweep.
	 */
	@Test
	public void testSweepNeverCollapsesToZero() {
		CurveView fe = makeFeKCurve(IN_RANGE_CALIB);
		CurveView mn = makeKCurve(Element.Mn, IN_RANGE_CALIB);

		int steps = 100;
		float[] cautious = new float[steps];
		float[] unclamped = new float[steps];
		for (int i = 0; i < steps; i++) {
			Spectrum remainder = contestedMnRemainder(fe, mn, 500f + i * 0.6f);
			cautious[i] = fit(mn, remainder);
			unclamped[i] = fitWith(new UnclampedCurveFitter(), mn, remainder);
		}

		Assert.assertTrue("sweep should start with a real fit", cautious[0] > 0f);
		Assert.assertTrue("unclamped fitter should lose Mn somewhere in this sweep", reachesZero(unclamped));
		Assert.assertFalse("cautious should keep Mn throughout", reachesZero(cautious));

		// Step to step, relative to the weight itself, it should also move less
		// abruptly -- the unclamped fitter's final step to zero is a 100% change.
		Assert.assertTrue(
				"cautious max relative step (" + maxRelativeStep(cautious) + ") should be below"
						+ " unclamped fitter's (" + maxRelativeStep(unclamped) + ")",
				maxRelativeStep(cautious) < maxRelativeStep(unclamped));
	}

	/**
	 * The penalty is narrowed, not disabled: against data the curve genuinely
	 * exceeds, the fit must still come in below the unbiased answer.
	 */
	@Test
	public void testGenuineOverfitIsStillPenalised() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);

		// A curve-shaped spectrum with its peak shaved down: any scale that fits
		// the flanks overshoots the middle, and all of the data is positive, so
		// there is no over-drawn channel to excuse it.
		Spectrum data = SpectrumCalculations.multiplyBy(curve.get(), 500f);
		for (int ch : curve.getIntenseChannelList()) {
			if (curve.get().get(ch) > 0.8f) {
				data.set(ch, data.get(ch) * 0.5f);
			}
		}

		float unbiased = fitWith(new LeastSquaresCurveFitter(), curve, data);
		float cautious = fit(curve, data);

		Assert.assertTrue("unbiased fit should be positive", unbiased > 0f);
		Assert.assertTrue(
				"cautious (" + cautious + ") should stay under unbiased (" + unbiased + ")",
				cautious < 0.95f * unbiased);
	}


	// ---- identity ----

	@Test
	public void testPluginIdentity() {
		CautiousCurveFitter fitter = new CautiousCurveFitter();
		Assert.assertEquals("ac5dae42-cc2e-4a9e-a282-2e13ef65a916", fitter.pluginUUID());
		// Distinct from its heavier-penalty subclass, so sessions resolve to
		// whichever they were saved with.
		Assert.assertNotEquals(new OptimizingCurveFitter().pluginUUID(), fitter.pluginUUID());
	}


	// ---- helpers ----

	private static boolean reachesZero(float[] values) {
		for (float v : values) {
			if (v <= 0f) return true;
		}
		return false;
	}

	/**
	 * The largest step between adjacent points, as a fraction of the larger of the
	 * two, so a drop to zero registers as a total loss however small the weight
	 * was beforehand.
	 */
	private static float maxRelativeStep(float[] values) {
		float max = 0f;
		for (int i = 1; i < values.length; i++) {
			float scale = Math.max(Math.max(values[i], values[i - 1]), 1e-6f);
			max = Math.max(max, Math.abs(values[i] - values[i - 1]) / scale);
		}
		return max;
	}

}
