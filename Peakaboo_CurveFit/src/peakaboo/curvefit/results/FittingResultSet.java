package peakaboo.curvefit.results;


import java.util.List;

import peakaboo.common.DataTypeFactory;

import scitypes.Spectrum;

/**
 * 
 * This class stores a set of {@link FittingResult}s, as well as the residual data after all fits have been
 * subtracted, and the total fit (the sum of all fittings).
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResultSet
{

	public Spectrum			totalFit;
	public Spectrum			residual;
	public List<FittingResult>	fits;

	
	public FittingResultSet(int size)
	{
		fits = DataTypeFactory.<FittingResult>list();
		totalFit = new Spectrum(size);
		residual = new Spectrum(size);
	}
	
}
