package peakaboo.curvefit.model.transitionseries;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.model.EnergyCalibration;
import peakaboo.curvefit.model.FittingResult;
import peakaboo.curvefit.model.fittingfunctions.FittingFunction;
import peakaboo.curvefit.model.fittingfunctions.FittingFunctionFactory;
import peakaboo.curvefit.model.transition.Transition;
import peakaboo.curvefit.peaktable.Element;
import scitypes.ISpectrum;
import scitypes.Range;
import scitypes.RangeSet;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



/**
 * A TransitionSeriesFitter represents the curve created by applying a {@link FittingFunction} 
 * to a {@link TransitionSeries}. It can then be applied to signal to determine the scale of fit.
 * 
 * @author NAS
 */

public class TransitionSeriesFitter implements Serializable
{

	//The {@link TransitionSeries} that this fitting is based on
	private TransitionSeries		transitionSeries;
	
	private EscapePeakType			escape	= EscapePeakType.SILICON;

	//Calibration for applying curve to data
	private EnergyCalibration 		calibration;
	
	
	
	//When a fitting is generated, it must be scaled to a range of 0.0-1.0, as
	//a FittingFunction won't do that automatically.
	//This is the value it's original max intensity, which the fitting is
	//then divided by
	private float					normalizationScale;
	//This is the curve created by applying a FittingFunction to the TransitionSeries 
	private Spectrum				normalizedCurve;	

	
	
	//How broad an area around each transition to consider important
	public static final float		DEFAULT_RANGE_MULT = 0.5f; //HWHM is default significant area
	private float					rangeMultiplier;
	
	//Areas where the curve is strong enough that we need to consider it.
	private RangeSet				intenseRanges;
	
	//how large a footprint this curve has, used in scoring fittings
	private int						baseSize;
	

	/**
	 * Create a new TransitionSeriesFitter.
	 * 
	 * @param ts
	 *            the TransitionSeries to fit
	 * @param dataWidth
	 *            the size of the source data
	 * @param energyPerChannel
	 *            the energy per data point in the source data
	 */
	public TransitionSeriesFitter(TransitionSeries ts, EnergyCalibration calibration, EscapePeakType escape, float standardDeviations)
	{
		this(ts, calibration, escape);
		this.rangeMultiplier = standardDeviations;
		
	}
	public TransitionSeriesFitter(TransitionSeries ts, EnergyCalibration calibration, EscapePeakType escape)
	{

		this.calibration = calibration;
		this.escape = escape;
		rangeMultiplier = DEFAULT_RANGE_MULT;
		
		//constraintMask = DataTypeFactory.<Boolean> listInit(dataWidth);
		intenseRanges = new RangeSet();
		
		if (ts != null) setTransitionSeries(ts, false);
		
	}

	public void setTransitionSeries(TransitionSeries ts)
	{
		setTransitionSeries(ts, false);
	}
	
	public void setTransitionSeries(TransitionSeries ts, boolean fitEscapes)
	{
		calculateConstraintMask(ts, fitEscapes);
		calcUnscaledFit(ts, (ts.type != TransitionSeriesType.COMPOSITE));

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
	private Spectrum scaleFitToData(float scale)
	{
		return SpectrumCalculations.multiplyBy(normalizedCurve, scale);
	}
	

	public FittingResult fit(ReadOnlySpectrum data) {
		float scale = getRatioForCurveUnderData(data);
		ReadOnlySpectrum scaledData = scaleFitToData(scale);
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
		for (Integer i : intenseRanges)
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
		for (Integer i : intenseRanges)
		{
			if (i < 0 || i >= data.size()) continue;
			
			if (normalizedCurve.get(i) >= cutoff)
			{
				
				thisFactor = data.get(i) / normalizedCurve.get(i);
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


	public static float escapeIntensity(Element e)
	{
		/*
		 * The paper
		 * 
		 * " Measurement and calculation of escape peak intensities in synchrotron radiation X-ray fluorescence analysis
		 * S.X. Kang a, X. Sun a, X. Ju b, Y.Y. Huang b, K. Yao a, Z.Q. Wu a, D.C. Xian b"
		 * 
		 * provides a listing of escape peak intensities relative to the real peak by element. By taking this data into
		 * openoffice and fitting an exponential regression line to it, we arrive at the formula esc(z) = (543268.59
		 * z^-4.48)%
		 */

		return 543268.59f * (float) Math.pow((e.ordinal() + 1), -4.48) / 100.0f;
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
	
	
	public boolean isOverlapping(TransitionSeriesFitter other)
	{
		return intenseRanges.isTouching(other.intenseRanges);
		
	}
	
	

	
	
	private void calculateConstraintMask(TransitionSeries ts, boolean fitEscapes)
	{

		
		intenseRanges.clear();

		float range;
		float mean;
		int start, stop;

		baseSize = 0;
		
		
		for (Transition t : ts)
		{

			//get the range of the peak
			range = t.getFWHM();
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
	private void calcUnscaledFit(TransitionSeries ts, boolean fitEscape)
	{

		Spectrum fit = new ISpectrum(calibration.getDataWidth());
		List<FittingFunction> functions = new ArrayList<FittingFunction>();
				
		for (Transition t : ts)
		{

			
			FittingFunction g = FittingFunctionFactory.get(t);

			functions.add(g);

			if (fitEscape && escape.hasOffset())
			{
				for (Transition esc : escape.offset()) {
									
					g = FittingFunctionFactory.get(
							t.energyValue - esc.energyValue, 
							t.getFWHM(), 
							t.relativeIntensity * escapeIntensity(ts.element) * esc.relativeIntensity
						);
					
					functions.add(g);
				}
			}

			

		}

		float value;
		for (int i = 0; i < calibration.getDataWidth(); i++)
		{

			value = 0.0f;
			for (FittingFunction f : functions)
			{

				value += f.getHeightAtPoint(calibration.energyFromChannel(i));

			}
			fit.set(i, value);

		}

		
		if (calibration.getDataWidth() > 0)
		{
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

	}

	
	public String toString()
	{
		return "[" + transitionSeries + "] x " + normalizationScale;
	}

	
	

}
