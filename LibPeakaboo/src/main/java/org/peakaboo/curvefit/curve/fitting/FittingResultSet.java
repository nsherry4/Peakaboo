package org.peakaboo.curvefit.curve.fitting;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * 
 * This class stores a set of {@link FittingResult}s, as well as the residual data after all fits have been
 * subtracted, and the total fit (the sum of all fittings).
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResultSet implements Iterable<FittingResult>
{

	Spectrum			totalFit;
	ReadOnlySpectrum	residual;
	List<FittingResult>	fits;
	ROFittingParameters	parameters;
	
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
			ROFittingParameters parameters) {
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

	public ROFittingParameters getParameters() {
		return parameters;
	}
	
	/**
	 * Generates a subset containing the intersection of this FittingResultSet's
	 * fits and the given list of {@link ITransitionSeries}. Methods like
	 * {@link FittingResultSet#getTotalFit()} and
	 * {@link FittingResultSet#getResidual()} will return the result for the entire
	 * set (ie not just the subset).
	 * 
	 * @param tss the list of {@link ITransitionSeries} to consider
	 */
	public FittingResultSet subsetIntersect(List<ITransitionSeries> tss) {
		
		FittingResultSet subset = new FittingResultSet(totalFit.size());
		subset.totalFit = new ISpectrum(this.totalFit);
		subset.residual = new ISpectrum(this.residual);
		subset.parameters = this.parameters.copy();
		subset.fits = this.fits.stream().filter(f -> tss.contains(f.getTransitionSeries())).collect(Collectors.toList());
		
		return subset;
		
	}

	/**
	 * Generates a subset containing the difference of this FittingResultSet's fits
	 * and the given list of {@link ITransitionSeries}. Methods like
	 * {@link FittingResultSet#getTotalFit()} and
	 * {@link FittingResultSet#getResidual()} will return the result for the entire
	 * set (ie not just the subset).
	 * 
	 * @param tss the list of {@link ITransitionSeries} to consider
	 */
	public FittingResultSet subsetDifference(List<ITransitionSeries> tss) {
		
		FittingResultSet subset = new FittingResultSet(totalFit.size());
		subset.totalFit = new ISpectrum(this.totalFit);
		subset.residual = new ISpectrum(this.residual);
		subset.parameters = this.parameters.copy();
		subset.fits = this.fits.stream().filter(f -> !tss.contains(f.getTransitionSeries())).collect(Collectors.toList());
		
		return subset;
	}
	
	public int size() {
		return getFits().size();
	}
	
	public boolean isEmpty( ) {
		return size() == 0;
	}

	@Override
	public Iterator<FittingResult> iterator() {
		return fits.iterator();
	}
	
	public Optional<FittingResult> getFitForTransitionSeries(ITransitionSeries ts) {
		for (FittingResult fit : fits) {
			if (fit.getTransitionSeries().equals(ts)) {
				return Optional.of(fit);
			}
		}
		return Optional.empty();
	}
	
	
	
}
