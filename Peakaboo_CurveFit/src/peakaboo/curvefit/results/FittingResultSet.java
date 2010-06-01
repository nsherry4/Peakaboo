package peakaboo.curvefit.results;


import java.util.List;

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

	public List<Double>			totalFit;
	public List<Double>			residual;
	public List<FittingResult>	fits;

}
