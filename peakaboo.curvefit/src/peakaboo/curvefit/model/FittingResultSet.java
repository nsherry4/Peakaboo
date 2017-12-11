package peakaboo.curvefit.model;


import java.util.ArrayList;
import java.util.List;

import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
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

	public Spectrum				totalFit;
	public ReadOnlySpectrum		residual;
	public List<FittingResult>	fits;

	
	public FittingResultSet(int size)
	{
		fits = new ArrayList<FittingResult>();
		totalFit = new ISpectrum(size);
		residual = new ISpectrum(size);
	}
	
}
