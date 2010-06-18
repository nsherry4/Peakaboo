package peakaboo.curvefit.fitting;



import java.util.Collections;
import java.util.List;

import peakaboo.calculations.SpectrumCalculations;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.Transition;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;



/**
 * A TransitionSeriesFitting represents the fitting of the set of {@link Transition}s in a {@link TransitionSeries} to a
 * set of raw data using a {@link GaussianFittingFunction}
 * 
 * @author Nathaniel Sherry, 2009
 */

public class TransitionSeriesFitting
{

	// private List<Double> unscaledFit;
	Spectrum					normalizedUnscaledFit;
	private List<Boolean>		constraintMask;
	private float				normalizationScale;

	/**
	 * The {@link TransitionSeries} that this fitting is based on.
	 */
	public TransitionSeries		transitionSeries;

	private int					dataWidth;

	public static final float	SIGMA	= 0.062f;
	private float		escape	= 1.74f;


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
	public TransitionSeriesFitting(TransitionSeries ts, int dataWidth, float energyPerChannel, float escape)
	{

		this.dataWidth = dataWidth;
		this.escape = escape;
		
		constraintMask = DataTypeFactory.<Boolean> list();
		for (int i = 0; i < dataWidth; i++)
		{
			constraintMask.add(false);
		}

		float range;
		float mean;
		int start, stop;

		for (Transition t : ts)
		{

			range = getSigmaForTransition(SIGMA, t) / energyPerChannel;

			mean = t.energyValue / energyPerChannel;

			start = (int) (mean - range);
			stop = (int) (mean + range);
			if (start < 0) start = 0;
			if (stop > dataWidth - 1) stop = dataWidth - 1;

			for (int i = start; i <= stop; i++)
			{
				constraintMask.set(i, true);
			}
		}

		calcUnscaledFit(ts, energyPerChannel, (ts.type != TransitionSeriesType.COMPOSITE));

		this.transitionSeries = ts;

	}


	// generates an initial unscaled curvefit from which later curves are scaled as needed
	private void calcUnscaledFit(TransitionSeries ts, float energyPerChannel, boolean fitEscape)
	{

		Spectrum fit = new Spectrum(dataWidth);
		List<FittingFunction> functions = DataTypeFactory.<FittingFunction> list();

		for (Transition t : ts)
		{

			// GaussianFittingFunction g = new GaussianFittingFunction(t.energyValue / energyPerChannel, SIGMA
			// / energyPerChannel, t.relativeIntensity / 100.0);
			FittingFunction g = new GaussianFittingFunction(

			t.energyValue / energyPerChannel, getSigmaForTransition(SIGMA, t) / energyPerChannel, t.relativeIntensity);

			functions.add(g);

			if (fitEscape)
			{
				g = new GaussianFittingFunction((t.energyValue - escape) / energyPerChannel, getSigmaForTransition(
						SIGMA,
						t)
						/ energyPerChannel, t.relativeIntensity * escapeIntensity(ts.element));
			}

			functions.add(g);

		}

		float value;
		for (int i = 0; i < dataWidth; i++)
		{

			value = 0.0f;
			for (FittingFunction f : functions)
			{

				value += f.getHeightAtPoint(i);

			}
			fit.set(i, value);

		}

		if (dataWidth > 0)
		{
			// //ListCalculations.normalize_inplace(fit);
			normalizationScale = SpectrumCalculations.max(fit);
			if (normalizationScale == 0.0)
			{
				normalizedUnscaledFit = SpectrumCalculations.multiplyBy(fit, 0.0f);
			}
			else
			{
				normalizedUnscaledFit = SpectrumCalculations.divideBy(fit, normalizationScale);
			}
			// unscaledFit = fit;
			// normalizedRatio = ListCalculations.max(fit) / ListCalculations.max(unscaledFit);
			// unscaledFit = fit;
		}

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
	public float getRatioForCurveUnderData(Spectrum data)
	{

		List<Float> dataConsidered = DataTypeFactory.<Float> list();
		List<Float> ratios = DataTypeFactory.<Float> list();
		for (int i = 0; i < data.size(); i++)
		{
			if (constraintMask.get(i) == true)
			{
				dataConsidered.add(data.get(i));
			}

		}

		Collections.sort(dataConsidered);

		if (dataConsidered.size() == 0) return 0.0f;

		float topIntensity = dataConsidered.get(dataConsidered.size() - 1);
		float cutoff;

		// calculate cut-off point where we do not consider any signal weaker than this when trying to fit
		if (topIntensity != 0.0)
		{
			cutoff = (float) Math.log(topIntensity * 2);
			cutoff = cutoff / topIntensity; // expresessed as a percentage
		}
		else
		{
			cutoff = 0.0f;
		}

		float thisFactor;

		for (int i = 0; i < data.size(); i++)
		{

			if (constraintMask.get(i) == true && normalizedUnscaledFit.get(i) >= cutoff)
			{

				thisFactor = data.get(i) / normalizedUnscaledFit.get(i);
				if (thisFactor != Float.NaN) ratios.add(thisFactor);
			}
		}

		Collections.sort(ratios);

		if (ratios.size() == 0) return 0.0f;

		return ratios.get(0);

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

}
