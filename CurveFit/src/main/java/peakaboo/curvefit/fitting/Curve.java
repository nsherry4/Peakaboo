package peakaboo.curvefit.fitting;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.fitting.functions.FittingFunction;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.transition.EscapePeakType;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import peakaboo.curvefit.transition.TransitionSeriesType;
import scitypes.ISpectrum;
import scitypes.Range;
import scitypes.RangeSet;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



/**
 * A Curve represents the curve created by applying a {@link FittingFunction} 
 * to a {@link TransitionSeries}. It can then be applied to signal to determine the scale of fit.
 * 
 * @author NAS
 */

public class Curve implements Serializable
{

	//The {@link TransitionSeries} that this fitting is based on
	private TransitionSeries		transitionSeries;
		
	//The details of how we generate our fitting curve
	private FittingParameters 		parameters;
	
	
	
	//When a fitting is generated, it must be scaled to a range of 0.0-1.0, as
	//a FittingFunction won't do that automatically.
	//This is the value it's original max intensity, which the fitting is
	//then divided by
	private float					normalizationScale;
	//This is the curve created by applying a FittingFunction to the TransitionSeries 
	Spectrum						normalizedCurve;	

	
	
	//How broad an area around each transition to consider important
	private static final float		DEFAULT_RANGE_MULT = 0.5f; //HWHM is default significant area
	private float					rangeMultiplier;
	
	//Areas where the curve is strong enough that we need to consider it.
	private RangeSet				intenseRanges;
	
	//how large a footprint this curve has, used in scoring fittings
	private int						baseSize;
	

	/**
	 * Create a new Curve.
	 * 
	 * @param ts the TransitionSeries to fit
	 * @param parameters the fitting parameters to use to model this curve
	 */
	public Curve(TransitionSeries ts, FittingParameters parameters)
	{

		this.parameters = parameters;
		rangeMultiplier = DEFAULT_RANGE_MULT;
		
		//constraintMask = DataTypeFactory.<Boolean> listInit(dataWidth);
		intenseRanges = new RangeSet();
		
		if (ts != null) setTransitionSeries(ts);
		
	}
	
	public void setTransitionSeries(TransitionSeries ts)
	{
		this.transitionSeries = ts;
		calculateConstraintMask();
		calcUnscaledFit(ts.type != TransitionSeriesType.COMPOSITE);
		
	}
	
	public TransitionSeries getTransitionSeries() {
		return transitionSeries;
	}
	
	/**
	 * Returns a scaled fit based on the given scale value
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @return a scaled fit
	 */
	Spectrum scale(float scale)
	{
		return SpectrumCalculations.multiplyBy(normalizedCurve, scale);
	}
	






	/**
	 * The scale by which the original collection of curves was scaled by to get it into the range of 0.0 - 1.0
	 * 
	 * @return the normalization scale value
	 */
	public float getNormalizationScale()
	{
		return normalizationScale;
	}
	
	/**
	 * Gets the width in channels of the base of this TransitionSeries.
	 * For example, L and M series will likely be broader than K
	 * series
	 * @return
	 */
	public int getSizeOfBase()
	{
		return baseSize;
	}
	
	
	public boolean isOverlapping(Curve other)
	{
		return intenseRanges.isTouching(other.intenseRanges);
		
	}
	

	public RangeSet getIntenseRanges() {
		return intenseRanges;
	}
	
	

	/**
	 * Given a TransitionSeries, calculate the range of channels which are important
	 */
	private void calculateConstraintMask()
	{

		
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
			if (start < 0) start = 0;
			if (stop > calibration.getDataWidth() - 1) stop = calibration.getDataWidth() - 1;
			if (start > calibration.getDataWidth() - 1) start = calibration.getDataWidth() - 1;

			baseSize += stop - start + 1;
			
			intenseRanges.addRange(new Range(start, stop));
			
		}
		
		

	}
	

	// generates an initial unscaled curvefit from which later curves are scaled as needed
	private void calcUnscaledFit(boolean fitEscape)
	{

		EnergyCalibration calibration = parameters.getCalibration();
		if (calibration.getDataWidth() == 0) {
			throw new RuntimeException("DataWidth cannot be 0");
		}
		
		Spectrum fit = new ISpectrum(calibration.getDataWidth());
		List<FittingFunction> functions = new ArrayList<FittingFunction>();
		

		//Build a list of fitting functions
		for (Transition t : this.transitionSeries)
		{

			functions.add(parameters.forTransition(t, this.transitionSeries.type));

			if (fitEscape && parameters.getEscapeType().hasOffset()) {
				for (Transition esc : parameters.getEscapeType().offset()) {
					functions.add(parameters.forEscape(t, esc, this.transitionSeries.element, this.transitionSeries.type));
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


	}

	
	public String toString()
	{
		return "[" + transitionSeries + "] x " + normalizationScale;
	}

	
	

	/**
	 * Fits this curve against spectrum data
	 */
	public FittingResult fit(ReadOnlySpectrum data) {
		float scale = this.getRatioForCurveUnderData(data);
		ReadOnlySpectrum scaledData = this.scale(scale);
		FittingResult result = new FittingResult(scaledData, this, scale);
		return result;
	}
	

	/**
	 * Calculates the amount that this fitting should be scaled by to best fit the given data set
	 * 
	 * @param data
	 *            the data to scale the fit to match
	 * @return a scale value
	 */
	private float getRatioForCurveUnderData(ReadOnlySpectrum data)
	{
			
		float topIntensity = Float.MIN_VALUE;
		boolean dataConsidered = false;
		float currentIntensity;
		float cutoff;
		
		//look at every point in the ranges covered by transitions, find the max intensity
		for (Integer i : this.getIntenseRanges())
		{
			if (i < 0 || i >= data.size()) continue;
			currentIntensity = data.get(i);
			if (currentIntensity > topIntensity) topIntensity = currentIntensity;
			dataConsidered = true;
			
		}
		if (! dataConsidered) return 0.0f;	
		
		
		
		// calculate cut-off point where we do not consider any signal weaker than this when trying to fit
		if (topIntensity > 0.0)
		{
			cutoff = (float) Math.log(topIntensity * 2);
			cutoff = cutoff / topIntensity; // expresessed w.r.t strongest signal
		}
		else
		{
			cutoff = 0.0f;
		}

		float thisFactor;
		float smallestFactor = Float.MAX_VALUE;
		boolean ratiosConsidered = false;

		
		//look at every point in the ranges covered by transitions 
		for (Integer i : this.getIntenseRanges())
		{
			if (i < 0 || i >= data.size()) continue;
			
			if (this.normalizedCurve.get(i) >= cutoff)
			{
				
				thisFactor = data.get(i) / this.normalizedCurve.get(i);
				if (thisFactor < smallestFactor && !Float.isNaN(thisFactor)) 
				{
					smallestFactor = thisFactor;
					ratiosConsidered = true;
				}
			}
		}

		if (! ratiosConsidered) return 0.0f;

		return smallestFactor;

	}

	

}
