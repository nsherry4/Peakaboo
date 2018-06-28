package peakaboo.curvefit.fitting;


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

	Spectrum			totalFit;
	ReadOnlySpectrum	residual;
	List<FittingResult>	fits;
	FittingParameters	parameters;
	
	public FittingResultSet(int size)
	{
		fits = new ArrayList<FittingResult>();
		totalFit = new ISpectrum(size);
		residual = new ISpectrum(size);
	}

	public FittingResultSet(
			Spectrum totalFit, 
			ReadOnlySpectrum residual, 
			List<FittingResult> fits, 
			FittingParameters parameters) {
		this.totalFit = totalFit;
		this.residual = residual;
		this.fits = fits;
		this.parameters = parameters;
	}
	
	
	public Spectrum getTotalFit() {
		return totalFit;
	}

	public ReadOnlySpectrum getResidual() {
		return residual;
	}

	public List<FittingResult> getFits() {
		return fits;
	}

	public FittingParameters getParameters() {
		return parameters;
	}
	
	
	
	
	
}
