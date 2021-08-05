package org.peakaboo.curvefit.curve.fitting;

import java.util.Set;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.RangeSet;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public interface ROCurve {

	ITransitionSeries getTransitionSeries();

	ReadOnlySpectrum get();

	/**
	 * Returns a scaled fit based on the given scale value
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @return a scaled fit
	 */
	Spectrum scale(float scale);

	/**
	 * Returns the sum of the scaled curve. This is generally faster than calling
	 * scale() and calculating the sum
	 */
	float scaleSum(float scale);

	/**
	 * Returns the max of the scaled curve. This is generally faster than calling
	 * scale() and calculating the sum
	 */
	float scaleMax(float scale);

	/**
	 * Returns a scaled fit based on the given scale value in the target Spectrum
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @param target
	 *            target Spectrum to store results
	 * @return a scaled fit
	 */
	Spectrum scaleInto(float scale, Spectrum target);

	/**
	 * The scale by which the original collection of curves was scaled by to get it into the range of 0.0 - 1.0
	 * 
	 * @return the normalization scale value
	 */
	float getNormalizationScale();

	/**
	 * Gets the width in channels of the base of this TransitionSeries.
	 * For example, L and M series will likely be broader than K
	 * series
	 * @return
	 */
	int getSizeOfBase();

	boolean isOverlapping(Curve other);

	/**
	 * Returns a RangeSet containing the channels for which this Curve is intense or
	 * significant.
	 */
	RangeSet getIntenseRanges();

	/**
	 * Returns a Set of Integers containing the channels for which this Curve is intense or
	 * significant.
	 */
	Set<Integer> getIntenseChannels();

	int compareTo(ROCurve o);

}