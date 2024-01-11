package org.peakaboo.curvefit.curve.fitting;


import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * 
 * This class stores a set of {@link FittingResult}s, as well as the residual data after all fits have been
 * subtracted, and the total fit (the sum of all fittings).
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResultSet implements FittingResultSetView
{

	protected Spectrum totalFit;
	protected SpectrumView residual;
	protected List<FittingResultView> fits;
	protected FittingParametersView parameters;
	
	public FittingResultSet(int size)
	{
		fits = new ArrayList<>();
		totalFit = new ArraySpectrum(size);
		residual = new ArraySpectrum(size);
	}

	public FittingResultSet(
			Spectrum totalFit, 
			SpectrumView residual, 
			List<FittingResultView> fits, 
			FittingParametersView parameters) {
		this.totalFit = totalFit;
		this.residual = residual;
		this.fits = fits;
		this.parameters = parameters;
	}
	
	
	@Override
	public Spectrum getTotalFit() {
		return totalFit;
	}

	@Override
	public SpectrumView getResidual() {
		return residual;
	}

	@Override
	public List<FittingResultView> getFits() {
		return fits;
	}

	@Override
	public FittingParametersView getParameters() {
		return parameters;
	}
	
	

	

	
	
}
