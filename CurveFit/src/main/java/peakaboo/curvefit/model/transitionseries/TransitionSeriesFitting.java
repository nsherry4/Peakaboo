package peakaboo.curvefit.model.transitionseries;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.model.EnergyCalibration;
import peakaboo.curvefit.model.FittingSet;
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
 * A TransitionSeriesFitting represents the fitting of the set of {@link Transition}s in a {@link TransitionSeries} to a
 * set of raw data using a {@link GaussianFittingFunction}
 * 
 * @author Nathaniel Sherry, 2009
 */

public class TransitionSeriesFitting implements Serializable
{

	// private List<Double> unscaledFit;
	Spectrum					normalizedUnscaledFit;
	//private List<Boolean>		constraintMask;
	
	//When a fitting is generated, it must be scaled to a range of 0.0-1.0
	//This is the value it's original max intensity, which the fitting is
	//then divided by
	private float				normalizationScale;
	

	private RangeSet			transitionRanges;
	
	/**
	 * The {@link TransitionSeries} that this fitting is based on.
	 */
	public TransitionSeries		transitionSeries;

	private int					baseSize;
	private EnergyCalibration 	calibration;

	public static final float	SIGMA	= 0.062f;
	private EscapePeakType		escape	= EscapePeakType.SILICON;

	public static float			defaultStandardDeviations = 1f;
	private float				standardDeviations;

	/**
	 * Create a new TransitionSeriesFitting.
	 * 
	 * @param ts
	 *            the TransitionSeries to fit
	 * @param dataWidth
	 *            the size of the source data
	 * @param energyPerChannel
	 *            the energy per data point in the source data
	 */
	public TransitionSeriesFitting(TransitionSeries ts, EnergyCalibration calibration, EscapePeakType escape, float standardDeviations)
	{
		this(ts, calibration, escape);
		this.standardDeviations = standardDeviations;
		
	}
	public TransitionSeriesFitting(TransitionSeries ts, EnergyCalibration calibration, EscapePeakType escape)
	{

		this.calibration = calibration;
		this.escape = escape;
		standardDeviations = defaultStandardDeviations;
		
		//constraintMask = DataTypeFactory.<Boolean> listInit(dataWidth);
		transitionRanges = new RangeSet();
		
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
	
	
	/**
	 * Returns a scaled fit based on the given scale value
	 * 
	 * @param scale
	 *            amount to scale the fitting by
	 * @return a scaled fit
	 */
	public Spectrum scaleFitToData(float scale)
	{
		return SpectrumCalculations.multiplyBy(normalizedUnscaledFit, scale);
	}
	


	/**
	 * Calculates the amount that this fitting should be scaled by to best fit the given data set
	 * 
	 * @param data
	 *            the data to scale the fit to match
	 * @return a scale value
	 */
	public float getRatioForCurveUnderData(ReadOnlySpectrum data)
	{
			
		float topIntensity = Float.MIN_VALUE;
		boolean dataConsidered = false;
		float currentIntensity;
		float cutoff;
		

		//look at every point in the ranges covered by transitions, find the max intensity
		for (Integer i : transitionRanges)
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
		for (Integer i : transitionRanges)
		{
			if (i < 0 || i >= data.size()) continue;
			
			if (normalizedUnscaledFit.get(i) >= cutoff)
			{
				
				thisFactor = data.get(i) / normalizedUnscaledFit.get(i);
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
	 * The sigma value for a {@link GaussianFittingFunction} changes based on the energy level of the {@link Transition}
	 * in question. This method calculates the sigma value which should be used for a given Transition.
	 * 
	 * @param SIGMA
	 *            a base sigma value
	 * @param t
	 *            the {@link Transition} to calculate a specific sigma value for
	 * @return the sigma value to be used for the given {@link Transition}
	 */
	public static float getSigmaForTransition(float SIGMA, Transition t)
	{
		float sigma = (SIGMA - 0.01f) + (t.energyValue / 500.0f);
		// double sigma = (SIGMA - 0.015) + (t.energyValue / 100.0);
		return sigma;
	}


	/**
	 * The scale by which the original collection of gaussian curves was scaled by to get it into the range of 0.0 - 1.0
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
	
	
	public boolean isOverlapping(TransitionSeriesFitting other)
	{
		return transitionRanges.isTouching(other.transitionRanges);
		
	}
	
	

	
	
	private void calculateConstraintMask(TransitionSeries ts, boolean fitEscapes)
	{

		
		transitionRanges.clear();

		float range;
		float mean;
		int start, stop;

		baseSize = 0;
		
		
		for (Transition t : ts)
		{

			//get the range of the peak
			range = getSigmaForTransition(SIGMA, t);
			range *= standardDeviations;
			
			//get the centre of the peak in channels
			mean = t.energyValue;

			start = calibration.channelFromEnergy(mean - range);
			stop = calibration.channelFromEnergy(mean + range);
			if (start < 0) start = 0;
			if (stop > calibration.getDataWidth() - 1) stop = calibration.getDataWidth() - 1;
			if (start > calibration.getDataWidth() - 1) start = calibration.getDataWidth() - 1;

			baseSize += stop - start + 1;
			
			transitionRanges.addRange(new Range(start, stop));
			
			
			
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
					
					transitionRanges.addRange(new Range(start, stop));
					
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

			
			FittingFunction g = FittingFunctionFactory.get(
					t.energyValue, 
					getSigmaForTransition(SIGMA, t), 
					t.relativeIntensity
				);

			functions.add(g);

			if (fitEscape && escape.hasOffset())
			{
				for (Transition esc : escape.offset()) {
									
					g = FittingFunctionFactory.get(
							t.energyValue - esc.energyValue, 
							getSigmaForTransition(SIGMA, t), 
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
				normalizedUnscaledFit = SpectrumCalculations.multiplyBy(fit, 0.0f);
			}
			else
			{
				normalizedUnscaledFit = SpectrumCalculations.divideBy(fit, normalizationScale);
			}

		}

	}

	
	public String toString()
	{
		return "[" + transitionSeries + "] x " + normalizationScale;
	}

}
