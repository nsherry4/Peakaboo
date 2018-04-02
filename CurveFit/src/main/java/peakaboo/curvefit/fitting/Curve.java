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
	
	private EscapePeakType			escape	= EscapePeakType.SILICON;
	
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
	public static final float		DEFAULT_RANGE_MULT = 0.5f; //HWHM is default significant area
	private float					rangeMultiplier;
	
	//Areas where the curve is strong enough that we need to consider it.
	RangeSet						intenseRanges;
	
	//how large a footprint this curve has, used in scoring fittings
	private int						baseSize;
	

	/**
	 * Create a new Curve.
	 * 
	 * @param ts the TransitionSeries to fit
	 * @param calibration the energy settings to use
	 * @param escape the type of escape peaks to model
	 */
	public Curve(TransitionSeries ts, FittingParameters parameters, EnergyCalibration calibration, EscapePeakType escape)
	{

		this.escape = escape;
		this.parameters = parameters;
		rangeMultiplier = DEFAULT_RANGE_MULT;
		
		//constraintMask = DataTypeFactory.<Boolean> listInit(dataWidth);
		intenseRanges = new RangeSet();
		
		if (ts != null) setTransitionSeries(ts, calibration, false);
		
	}

	public void setTransitionSeries(TransitionSeries ts, EnergyCalibration calibration)
	{
		setTransitionSeries(ts, calibration, false);
	}
	
	public void setTransitionSeries(TransitionSeries ts, EnergyCalibration calibration, boolean fitEscapes)
	{
		calculateConstraintMask(ts, calibration, fitEscapes);
		calcUnscaledFit(ts, calibration, (ts.type != TransitionSeriesType.COMPOSITE));
		this.transitionSeries = ts;
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
	
	

	
	/**
	 * Given a TransitionSeries, calculate the range of channels which are important
	 */
	private void calculateConstraintMask(TransitionSeries ts, EnergyCalibration calibration, boolean fitEscapes)
	{

		
		intenseRanges.clear();

		float range;
		float mean;
		int start, stop;

		baseSize = 0;
		
		
		for (Transition t : ts)
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
			
			
			
			if (fitEscapes && escape.hasOffset())
			{
				for (Transition esc : escape.offset()) {
					mean = calibration.channelFromEnergy(t.energyValue-esc.energyValue);
					
					start = (int) (mean - range);
					stop = (int) (mean + range);
					if (start < 0) start = 0;
					if (stop > calibration.getDataWidth() - 1) stop = calibration.getDataWidth() - 1;
					if (start > calibration.getDataWidth() - 1) start = calibration.getDataWidth() - 1;
	
					baseSize += stop - start + 1;
					
					intenseRanges.addRange(new Range(start, stop));
					
				}
			}
			
		}
		
		

	}
	

	// generates an initial unscaled curvefit from which later curves are scaled as needed
	private void calcUnscaledFit(TransitionSeries ts, EnergyCalibration calibration, boolean fitEscape)
	{

		if (calibration.getDataWidth() == 0) {
			throw new RuntimeException("DataWidth cannot be 0");
		}
		
		Spectrum fit = new ISpectrum(calibration.getDataWidth());
		List<FittingFunction> functions = new ArrayList<FittingFunction>();
		

		//Build a list of fitting functions
		for (Transition t : ts)
		{

			functions.add(parameters.forTransition(t, ts.type));

			if (fitEscape && escape.hasOffset()) {
				for (Transition esc : escape.offset()) {
					functions.add(parameters.forEscape(t, esc, ts.element, ts.type));
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

	
	

}
