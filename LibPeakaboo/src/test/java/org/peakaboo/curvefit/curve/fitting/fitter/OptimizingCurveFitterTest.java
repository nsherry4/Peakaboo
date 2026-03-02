package org.peakaboo.curvefit.curve.fitting.fitter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.KrausePeakTable;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class OptimizingCurveFitterTest {

	@BeforeClass
	public static void initRegistry() {
		FittingFunctionRegistry.init();
	}

	// Fe K-alpha is ~6.4 keV. A 0-5 keV calibration puts it out of range.
	private static final EnergyCalibration OUT_OF_RANGE_CALIB  = new EnergyCalibration(0f,  5f, 2048);
	private static final EnergyCalibration IN_RANGE_CALIB      = new EnergyCalibration(0f, 20f, 2048);

	private CurveView makeFeKCurve(EnergyCalibration calibration) {
		FittingSet fittingSet = new FittingSet();
		fittingSet.getFittingParameters().setCalibration(calibration);
		fittingSet.addTransitionSeries(new KrausePeakTable().get(Element.Fe, TransitionShell.K));
		return fittingSet.getCurves().get(0);
	}

	private FittingResult fit(CurveView curve, Spectrum data) {
		return new OptimizingCurveFitter().fit(new CurveFitter.CurveFitterContext(data, curve));
	}

	/**
	 * Regression: when a curve has no intense channels because its energies fall
	 * outside the calibrated range, fit() must return scale 0 without throwing.
	 * Previously this caused TooManyEvaluationsException as the optimizer tried
	 * to minimise a constant scoring function.
	 */
	@Test
	public void testFitOutOfEnergyRange() {
		CurveView curve = makeFeKCurve(OUT_OF_RANGE_CALIB);
		Assert.assertEquals(0, curve.getIntenseChannelList().length);

		Spectrum data = new ArraySpectrum(OUT_OF_RANGE_CALIB.getDataWidth());
		FittingResult result = fit(curve, data);

		Assert.assertEquals(0f, result.getCurveScale(), 0f);
	}

	/**
	 * Core correctness: when the data spectrum is exactly the curve scaled by K,
	 * the optimizer should recover K as the best-fit scale.
	 */
	@Test
	public void testFitRecoverKnownScale() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);
		float K = 500f;
		Spectrum data = SpectrumCalculations.multiplyBy(curve.get(), K);

		FittingResult result = fit(curve, data);

		Assert.assertEquals(K, result.getCurveScale(), 1f);
	}

	/**
	 * Edge case: all-zeros data should produce a zero-scale fit.
	 * Any positive scale overshoots (negative residuals), which the overfit
	 * penalty amplifies, so the minimum of the scoring function is at scale = 0.
	 */
	@Test
	public void testFitZeroDataReturnsZeroScale() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);
		Spectrum data = new ArraySpectrum(IN_RANGE_CALIB.getDataWidth());

		FittingResult result = fit(curve, data);

		Assert.assertEquals(0f, result.getCurveScale(), 0.001f);
	}

	/**
	 * Invariant: the fitted scale must never be negative.
	 * The search interval is bounded at 0, so even adversarial data cannot
	 * produce a negative scale.
	 */
	@Test
	public void testFitScaleIsNonNegative() {
		CurveView curve = makeFeKCurve(IN_RANGE_CALIB);
		Spectrum data = new ArraySpectrum(IN_RANGE_CALIB.getDataWidth(), -1000f);

		FittingResult result = fit(curve, data);

		Assert.assertTrue(result.getCurveScale() >= 0f);
	}

}
