package org.peakaboo.filter.plugins.noise;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

/**
 * A Savitsky-Golay fit of order p reproduces any polynomial up to degree p exactly, so
 * anything the filter changes about one is error it introduced itself. That single
 * property covers most of what can go wrong, including the edges, where the window has
 * to shrink and the filter used to clip the full window's coefficients instead -- which
 * still renormalized to 1, so it passed a flat signal, but moved the window's weighted
 * centre off the point being smoothed and bent any slope.
 */
public class SavitskyGolayNoiseFilterTest {

	private static final float TOLERANCE = 0.01f;

	/** Spectrum sizes worth covering: below the window, at it, and well past it */
	private static final int[] SIZES = { 1, 3, 5, 200 };

	private SavitskyGolayNoiseFilter makeFilter() {
		SavitskyGolayNoiseFilter filter = new SavitskyGolayNoiseFilter();
		filter.initialize();
		return filter;
	}

	@SuppressWarnings("unchecked")
	private SavitskyGolayNoiseFilter makeFilter(int reach, int order) {
		SavitskyGolayNoiseFilter filter = makeFilter();
		((Parameter<Integer>) filter.getParameters().get(0)).setValue(reach);
		((Parameter<Integer>) filter.getParameters().get(1)).setValue(order);
		return filter;
	}

	/**
	 * A polynomial of the given degree over the spectrum. The x axis is scaled to [0,1]
	 * so the values stay in a range where float rounding is well under the tolerance --
	 * the coefficient sums run into the thousands, and a steep ramp over 200 channels
	 * would otherwise lose the low digits.
	 */
	private Spectrum polynomial(int size, int degree) {
		float[] coefs = { 100, 50, -30, 20, -10 };
		Spectrum data = new ArraySpectrum(size);
		for (int i = 0; i < size; i++) {
			double t = size == 1 ? 0 : (double) i / (size - 1);
			double value = 0;
			for (int d = 0; d <= degree; d++) {
				value += coefs[d] * Math.pow(t, d);
			}
			data.set(i, (float) value);
		}
		return data;
	}

	/**
	 * Every polynomial the given order must reproduce, at every window we have
	 * coefficients for, on spectra both longer and shorter than the window.
	 */
	private void assertPreservesPolynomials(int order, int maxDegree, int minReach, int maxReach) {
		for (int reach = minReach; reach <= maxReach; reach++) {
			for (int size : SIZES) {
				for (int degree = 0; degree <= maxDegree; degree++) {

					Spectrum data = polynomial(size, degree);
					SpectrumView filtered = makeFilter(reach, order).filter(data);
					String what = "order " + order + ", reach " + reach + ", degree " + degree
							+ ", size " + size;

					Assert.assertEquals(what + ": wrong size", data.size(), filtered.size());
					for (int i = 0; i < data.size(); i++) {
						Assert.assertEquals(what + " at channel " + i,
								data.get(i), filtered.get(i), TOLERANCE);
					}

				}
			}
		}
	}

	/**
	 * The regression test. Order 3 smooths as an order 2 fit, since for a symmetric
	 * window the odd terms vanish at the centrepoint, and we have tables for reach 2-12.
	 */
	@Test
	public void testOrderThreePreservesPolynomials() {
		assertPreservesPolynomials(3, 2, 2, 12);
	}

	/** Order 5 smooths as order 4, and only reach 3 and 4 have tables */
	@Test
	public void testOrderFivePreservesPolynomials() {
		assertPreservesPolynomials(5, 4, 3, 4);
	}

	/** Smoothing still has to happen -- preserving polynomials alone would let a no-op pass */
	@Test
	public void testNoiseIsSmoothed() {
		Spectrum data = polynomial(200, 0);
		data.set(100, 500f);
		SpectrumView filtered = makeFilter().filter(data);

		Assert.assertTrue("the spike was not brought down", filtered.get(100) < 500f);
		Assert.assertTrue("the spike was erased entirely", filtered.get(100) > 100f);
	}

	/** Signal above the cutoff is left alone when the filter is asked to spare it */
	@SuppressWarnings("unchecked")
	@Test
	public void testStrongSignalIsSkipped() {
		SavitskyGolayNoiseFilter filter = makeFilter();
		((Parameter<Boolean>) filter.getParameters().get(3)).setValue(true);
		((Parameter<Float>) filter.getParameters().get(4)).setValue(4.0f);

		Spectrum data = polynomial(200, 0);
		data.set(100, 500f);

		Assert.assertEquals(500f, filter.filter(data).get(100), TOLERANCE);
	}

}
