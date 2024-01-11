package org.peakaboo.curvefit.curve.fitting;

import java.util.List;
import java.util.Set;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.RangeSet;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public interface CurveView extends Comparable<CurveView> {

	ITransitionSeries getTransitionSeries();

	SpectrumView get();
	
	float getNormalizedSum();
	float getNormalizedMax();

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
	
	/**
	 * Returns a sorted List of Integers containing the channels for which this
	 * Curve is intense or significant.
	 */
	List<Integer> getIntenseChannelList();
	
	
	/**
	 * The scale by which the original collection of curves was scaled by to get it into the range of 0.0 - 1.0
	 * 
	 * @return the normalization scale value
	 */
	float getNormalizationScale();
	

	
	default int compareTo(CurveView o) {
		return this.getTransitionSeries().compareTo(o.getTransitionSeries());
	}
	
	
	/**
	 * Returns a scaled fit based on the given scale value
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @return a scaled fit
	 */
	default Spectrum scale(float scale) {
		return SpectrumCalculations.multiplyBy(get(), scale);
	}

	/**
	 * Returns the sum of the scaled curve. This is generally faster than calling
	 * scale() and calculating the sum
	 */
	default float scaleSum(float scale) {
		return getNormalizedSum() * scale;
	}

	/**
	 * Returns the max of the scaled curve. This is generally faster than calling
	 * scale() and calculating the sum
	 */
	default float scaleMax(float scale) {
		return getNormalizedMax() * scale;
	}

	/**
	 * Returns a scaled fit based on the given scale value in the target Spectrum
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @param target
	 *            target Spectrum to store results
	 * @return a scaled fit
	 */
	default Spectrum scaleInto(float scale, Spectrum target) {
		return SpectrumCalculations.multiplyBy_target(get(), target, scale);
	}

	/**
	 * Adds a scaled fit to an existing Spectrum.
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @param target
	 *            target Spectrum to which results will be added
	 */
	default void scaleOnto(float scale, Spectrum target) {
		// We can take advantage of a special instruction to perform a "fused multiply
		// add" where the multiply scales the normalizedCurve and then add it to the
		// target spectrum
		SpectrumCalculations.fma(get(), scale, target, target);
	}
	
	/**
	 * Adds a scaled fit to an existing Spectrum.
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @param target
	 *            target Spectrum to which results will be added
	 * @param firstChannel
	 *            first channel to perform the operation on
	 * @param lastChannel
	 *            last channel to perform the operation on
	 */
	default void scaleOnto(float scale, Spectrum target, int firstChannel, int lastChannel) {
		SpectrumCalculations.fma(get(), scale, target, target, firstChannel, lastChannel);
	}
	
	/**
	 * Adds a scaled fit to an existing Spectrum.
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @param target
	 *            target Spectrum to which results will be added
	 * @param firstChannel
	 *            first channel to perform the operation on
	 * @param lastChannel
	 *            last channel to perform the operation on
	 */
	default void scaleOnto(float scale, SpectrumView source, Spectrum target, int firstChannel, int lastChannel) {
		SpectrumCalculations.fma(get(), scale, source, target, firstChannel, lastChannel);
	}


	default boolean isOverlapping(CurveView other) {
		return getIntenseRanges().isTouching(other.getIntenseRanges());
	}



}