package org.peakaboo.curvefit.curve.fitting;



import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.RangeSet;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;



/**
 * A Curve represents the curve created by applying a {@link FittingFunction} 
 * to a {@link ITransitionSeries}. It can then be applied to signal to determine the scale of fit.
 * 
 * @author NAS
 */

public class Curve implements CurveView {

	//The {@link TransitionSeries} that this fitting is based on
	private ITransitionSeries		transitionSeries;
		
	//The details of how we generate our fitting curve
	private FittingParametersView 		parameters;
	
	
	
	//When a fitting is generated, it must be scaled to a range of 0.0-1.0, as
	//a FittingFunction won't do that automatically.
	//This is the value it's original max intensity, which the fitting is
	//then divided by
	private float					normalizationScale;

	//This is the curve created by applying a FittingFunction to the TransitionSeries 
	Spectrum normalizedCurve;
	private float normalizedSum;
	private float normalizedMax;

	
	
	//How broad an area around each transition to consider important
	private static final float		DEFAULT_RANGE_MULT = 0.5f; //HWHM is default significant area
	private float					rangeMultiplier;
	
	//Areas (in channels) where the curve is strong enough that we need to consider it.
	private RangeSet				intenseRanges;
	private Set<Integer>			intenseChannels;
	private List<Integer>			intenseChannelList;
	
	//how large a footprint this curve has, used in scoring fittings
	private int						baseSize;
	

	/**
	 * Create a new Curve.
	 * 
	 * @param ts the TransitionSeries to fit
	 * @param parameters the fitting parameters to use to model this curve
	 */
	public Curve(ITransitionSeries ts, FittingParametersView parameters) {

		this.parameters = parameters;
		rangeMultiplier = DEFAULT_RANGE_MULT;
		
		//constraintMask = DataTypeFactory.<Boolean> listInit(dataWidth);
		intenseRanges = new RangeSet();
		intenseChannels = new LinkedHashSet<>();
		intenseChannelList = new ArrayList<>();
		
		if (ts != null) setTransitionSeries(ts);
		
	}
	
	public void setTransitionSeries(ITransitionSeries ts) {
		this.transitionSeries = ts;
		calculateConstraintMask();
		calcUnscaledFit(ts.getShell() != TransitionShell.COMPOSITE);
		
	}
	
	@Override
	public ITransitionSeries getTransitionSeries() {
		return transitionSeries;
	}
	
	@Override
	public ReadOnlySpectrum get() {
		return normalizedCurve;
	}
	
	
	public float getNormalizedSum() {
		return normalizedSum;
	}
	
	public float getNormalizedMax() {
		return normalizedMax;
	}

	/**
	 * The scale by which the original collection of curves was scaled by to get it into the range of 0.0 - 1.0
	 * 
	 * @return the normalization scale value
	 */
	@Override
	public float getNormalizationScale() {
		return normalizationScale;
	}


	/**
	 * Returns the RangeSet containing the channels for which this Curve is intense or
	 * significant.
	 * <br/><br/>
	 * Note that this is returning the internal range value and not a copy because this
	 * code is very performance sensitive. Please be careful.
	 */
	@Override
	public RangeSet getIntenseRanges() {
		return intenseRanges;
	}
	
	/**
	 * Returns a Set of Integers containing the channels for which this Curve is intense or
	 * significant.
	 */
	@Override
	public Set<Integer> getIntenseChannels() {
		return Collections.unmodifiableSet(intenseChannels);
	}
	
	/**
	 * Returns an ordered List of Integers containing the channels for which this
	 * Curve is intense or significant.
	 */
	@Override
	public List<Integer> getIntenseChannelList() {
		return Collections.unmodifiableList(intenseChannelList);
	}
	
	@Override
	public String toString() {
		return "[" + getTransitionSeries() + "] x " + normalizationScale;
	}


	/**
	 * Given a TransitionSeries, calculate the range of channels which are important
	 */
	private void calculateConstraintMask() {

		
		intenseRanges.clear();

		float range;
		float mean;
		int start, stop;

		baseSize = 0;
		
		EnergyCalibration calibration = parameters.getCalibration();
		for (Transition t : this.transitionSeries)
		{

			//get the range of the peak
			range = parameters.getFWHM(t);
			range *= rangeMultiplier;
			
			//get the centre of the peak in channels
			mean = t.energyValue;

			start = calibration.channelFromEnergy(mean - range);
			stop = calibration.channelFromEnergy(mean + range);
			int max = calibration.getDataWidth() - 1;
			int min = 0;
			if (stop < min || start > max) { continue; }
			if (start < min) start = min;
			if (stop > max) stop = max;

			baseSize += stop - start + 1;
			
			intenseRanges.addRange(new Range(start, stop+1));
			
		}
		
		intenseChannels.clear();
		intenseChannelList.clear();
		for (int channel : intenseRanges) {
			intenseChannels.add(channel);
		}
		intenseChannelList.addAll(intenseChannels);
		intenseChannelList.sort(Integer::compare);
		
		

	}
	

	// generates an initial unscaled curvefit from which later curves are scaled as needed
	private void calcUnscaledFit(boolean fitEscape) {

		fitEscape &= parameters.getShowEscapePeaks();
		
		EnergyCalibration calibration = parameters.getCalibration();
		if (calibration.getDataWidth() == 0) {
			throw new RuntimeException("DataWidth cannot be 0");
		}
		
		Spectrum fit = new ISpectrum(calibration.getDataWidth());
		List<FittingFunction> functions = new ArrayList<FittingFunction>();
		

		//Build a list of fitting functions
		for (Transition t : this.transitionSeries)
		{

			functions.add(parameters.forTransition(t));

			if (fitEscape && parameters.getDetectorMaterial().get().hasOffset()) {
				for (Transition esc : parameters.getDetectorMaterial().get().offset()) {
					functions.add(parameters.forEscape(t, esc, this.transitionSeries.getElement()));
				}
			}

		}

		//Use the functions to generate a model
		float value;
		for (int i = 0; i < calibration.getDataWidth(); i++)
		{

			value = 0.0f;
			for (FittingFunction f : functions)
			{

				value += f.forEnergy(calibration.energyFromChannel(i));

			}
			fit.set(i, value);

		}


		normalizationScale = fit.max();
		if (normalizationScale == 0.0)
		{
			normalizedCurve = SpectrumCalculations.multiplyBy(fit, 0.0f);
		}
		else
		{
			normalizedCurve = SpectrumCalculations.divideBy(fit, normalizationScale);
		}
		normalizedSum = normalizedCurve.sum();
		normalizedMax = normalizedCurve.max();


	}

	


	
	

}
