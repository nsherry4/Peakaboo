package peakaboo.curvefit.fitting;


import java.util.Collections;
import java.util.List;

import peakaboo.calculations.ListCalculations;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.Transition;
import peakaboo.datatypes.peaktable.TransitionSeries;

/**
 * 
 * A TransitionSeriesFitting represents the fitting of the set of {@link Transition}s in a
 * {@link TransitionSeries} to a set of raw data using a {@link GaussianFittingFunction}
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class TransitionSeriesFitting
{

	//private List<Double>		unscaledFit;
	List<Double> normalizedUnscaledFit;
	private List<Boolean>		constraintMask;
	private double normalizationScale;

	/**
	 * The {@link TransitionSeries} that this fitting is based on.
	 */
	public TransitionSeries		transitionSeries;

	
	private int					dataWidth;
	
	public static final double	SIGMA	= 0.062;
	public static final double	escape	= 1.74;


	/**
	 * 
	 * Create a new TransitionSeriesFitting.
	 * 
	 * @param ts the TransitionSeries to fit
	 * @param dataWidth the size of the source data
	 * @param energyPerChannel the energy per data point in the source data
	 */
	public TransitionSeriesFitting(TransitionSeries ts, int dataWidth, double energyPerChannel)
	{

		this.dataWidth = dataWidth;

		constraintMask = DataTypeFactory.<Boolean> list();
		for (int i = 0; i < dataWidth; i++) {
			constraintMask.add(false);
		}

		double range;
		double mean;
		int start, stop;

		for (Transition t : ts) {

			range = getSigmaForTransition(SIGMA, t) / energyPerChannel;

			mean = t.energyValue / energyPerChannel;

			start = (int) (mean - range);
			stop = (int) (mean + range);
			if (start < 0) start = 0;
			if (stop > dataWidth - 1) stop = dataWidth - 1;

			for (int i = start; i <= stop; i++) {
				constraintMask.set(i, true);
			}
		}

		calcUnscaledFit(ts, energyPerChannel);

		this.transitionSeries = ts;

	}



	// generates an initial unscaled curvefit from which later curves are scaled as needed
	private void calcUnscaledFit(TransitionSeries ts, double energyPerChannel)
	{

		List<Double> fit = DataTypeFactory.<Double> list();
		List<FittingFunction> functions = DataTypeFactory.<FittingFunction> list();

		for (Transition t : ts) {

			// GaussianFittingFunction g = new GaussianFittingFunction(t.energyValue / energyPerChannel, SIGMA
			// / energyPerChannel, t.relativeIntensity / 100.0);
			FittingFunction g = new GaussianFittingFunction(

					t.energyValue / energyPerChannel, getSigmaForTransition(SIGMA, t) / energyPerChannel, t.relativeIntensity
			);

			functions.add(g);


			g = new GaussianFittingFunction((t.energyValue - escape) / energyPerChannel,
					getSigmaForTransition(SIGMA, t) / energyPerChannel, t.relativeIntensity * escapeIntensity(ts.element));



			functions.add(g);

		}

		double value;
		for (int i = 0; i < dataWidth; i++) {

			value = 0.0;
			for (FittingFunction f : functions) {

				value += f.getHeightAtPoint(i);

			}
			fit.add(value);

		}

		if (dataWidth > 0) {
			////ListCalculations.normalize_inplace(fit);
			normalizationScale = ListCalculations.max(fit);
			if (normalizationScale == 0.0){
				normalizedUnscaledFit = ListCalculations.multiplyBy(fit, 0.0);
			} else {
				normalizedUnscaledFit = ListCalculations.divideBy(fit, normalizationScale);
			}
			//unscaledFit = fit;
			// normalizedRatio = ListCalculations.max(fit) / ListCalculations.max(unscaledFit);
			// unscaledFit = fit;
		}

	}


	/**
	 * Returns a scaled fit based on the given scale value
	 * @param scale amount to scale the fitting by
	 * @return a scaled fit
	 */
	public List<Double> scaleFitToData(double scale)
	{
		return ListCalculations.multiplyBy(normalizedUnscaledFit, scale);
	}



	/**
	 * Calculates the amount that this fitting should be scaled by to best fit the given data set
	 * @param data the data to scale the fit to match
	 * @return a scale value
	 */
	public double getRatioForCurveUnderData(List<Double> data)
	{

		
		
		List<Double> dataConsidered = DataTypeFactory.<Double> list();
		List<Double> ratios = DataTypeFactory.<Double> list();
		for (int i = 0; i < data.size(); i++) {
			if (constraintMask.get(i) == true) {
				dataConsidered.add(data.get(i));
			}

		}

		Collections.sort(dataConsidered);
		
		if (dataConsidered.size() == 0) return 0.0;


		double topIntensity = dataConsidered.get(dataConsidered.size() - 1);
		double cutoff;
	
		//calculate cut-off point where we do not consider any signal weaker than this when trying to fit
		if (topIntensity != 0.0) {
			cutoff = Math.log(topIntensity * 2);
			cutoff = cutoff / topIntensity; // expresessed as a percentage
		} else {
			cutoff = 0.0;
		}
		
		
		double thisFactor;


		for (int i = 0; i < data.size(); i++) {

			if (constraintMask.get(i) == true && normalizedUnscaledFit.get(i) >= cutoff) {

				thisFactor = data.get(i) / normalizedUnscaledFit.get(i);
				if (thisFactor != Double.NaN) ratios.add(thisFactor);
			}
		}

		Collections.sort(ratios);

		if (ratios.size() == 0) return 0.0;
				
		return ratios.get(0);
		
	}


	public static double escapeIntensity(Element e)
	{
		/*
		 * The paper
		 * 
		 * " Measurement and calculation of escape peak intensities in synchrotron radiation X-ray
		 * fluorescence analysis S.X. Kang a, X. Sun a, X. Ju b, Y.Y. Huang b, K. Yao a, Z.Q. Wu a, D.C. Xian
		 * b"
		 * 
		 * provides a listing of escape peak intensities relative to the real peak by element. By taking this
		 * data into openoffice and fitting an exponential regression line to it, we arrive at the formula
		 * esc(z) = (543268.59 z^-4.48)%
		 */

		return 543268.59 * Math.pow((e.ordinal() + 1), -4.48) / 100.0;
	}


	/**
	 * 
	 * The sigma value for a {@link GaussianFittingFunction} changes based on the energy level of the {@link Transition} in question. This method calculates the sigma value which should be used for a given Transition.
	 * 
	 * @param SIGMA a base sigma value
	 * @param t the {@link Transition} to calculate a specific sigma value for
	 * @return the sigma value to be used for the given {@link Transition}
	 */
	public static double getSigmaForTransition(double SIGMA, Transition t)
	{
		double sigma = (SIGMA - 0.01) + (t.energyValue / 500.0);
		// double sigma = (SIGMA - 0.015) + (t.energyValue / 100.0);
		return sigma;
	}

	/**
	 * The scale by which the original collection of gaussian curves was scaled by to get it into the range of 0.0 - 1.0 
	 * @return the normalization scale value
	 */
	public double getNormalizationScale()
	{
		return normalizationScale;
	}

}
