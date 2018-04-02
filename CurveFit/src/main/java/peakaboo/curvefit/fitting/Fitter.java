package peakaboo.curvefit.fitting;

import java.util.List;

import scitypes.ReadOnlySpectrum;
import scitypes.SpectrumCalculations;

public class Fitter {

	
	public static FittingResultSet fit(ReadOnlySpectrum data, FittingSet fittings) {

		
		
		FittingResultSet results = new FittingResultSet(data.size());
		results.parameters = FittingParameters.copy(fittings.getFittingParameters());
		
		// calculate the curves
		for (Curve curve : fittings.getCurves()) {
			if (!curve.getTransitionSeries().visible) { continue; }
			
			FittingResult result = fit(data, curve);
			data = SpectrumCalculations.subtractLists(data, result.getFit(), 0.0f);
			
			//should this be done through a method addFit?
			results.fits.add(result);
			SpectrumCalculations.addLists_inplace(results.totalFit, result.getFit());
		}

		results.residual = data;
		

		return results;
		
	}
	
	public static FittingResult fit(ReadOnlySpectrum data, Curve curve) {
		float scale = getRatioForCurveUnderData(data, curve);
		ReadOnlySpectrum scaledData = curve.scale(scale);
		FittingResult result = new FittingResult(scaledData, curve, scale);
		return result;
	}
	

	/**
	 * Calculates the amount that this fitting should be scaled by to best fit the given data set
	 * 
	 * @param data
	 *            the data to scale the fit to match
	 * @return a scale value
	 */
	private static float getRatioForCurveUnderData(ReadOnlySpectrum data, Curve curve)
	{
			
		float topIntensity = Float.MIN_VALUE;
		boolean dataConsidered = false;
		float currentIntensity;
		float cutoff;
		
		//look at every point in the ranges covered by transitions, find the max intensity
		for (Integer i : curve.getIntenseRanges())
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
		for (Integer i : curve.getIntenseRanges())
		{
			if (i < 0 || i >= data.size()) continue;
			
			if (curve.normalizedCurve.get(i) >= cutoff)
			{
				
				thisFactor = data.get(i) / curve.normalizedCurve.get(i);
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
