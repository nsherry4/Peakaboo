package org.peakaboo.curvefit.curve.fitting;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public interface FittingResultSetView extends Iterable<FittingResultView> {

	Spectrum getTotalFit();

	ReadOnlySpectrum getResidual();

	List<FittingResultView> getFits();

	FittingParametersView getParameters();

	
	/**
	 * Generates a subset containing the intersection of this FittingResultSet's
	 * fits and the given list of {@link ITransitionSeries}. Methods like
	 * {@link FittingResultSet#getTotalFit()} and
	 * {@link FittingResultSet#getResidual()} will return the result for the entire
	 * set (ie not just the subset).
	 * 
	 * @param tss the list of {@link ITransitionSeries} to consider
	 */
	default FittingResultSetView subsetIntersect(List<ITransitionSeries> tss) {
		
		FittingResultSet subset = new FittingResultSet(this.getTotalFit().size());
		subset.totalFit = new ISpectrum(this.getTotalFit());
		subset.residual = new ISpectrum(this.getResidual());
		subset.parameters = this.getParameters().copy();
		subset.fits = this.getFits().stream().filter(f -> tss.contains(f.getTransitionSeries())).collect(Collectors.toList());
		
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
	default FittingResultSetView subsetDifference(List<ITransitionSeries> tss) {
		
		FittingResultSet subset = new FittingResultSet(this.getTotalFit().size());
		subset.totalFit = new ISpectrum(this.getTotalFit());
		subset.residual = new ISpectrum(this.getResidual());
		subset.parameters = this.getParameters().copy();
		subset.fits = this.getFits().stream().filter(f -> !tss.contains(f.getTransitionSeries())).collect(Collectors.toList());
		
		return subset;
	}

	
	default int size() {
		return getFits().size();
	}
	
	default boolean isEmpty( ) {
		return size() == 0;
	}

	default Iterator<FittingResultView> iterator() {
		return getFits().iterator();
	}
	
	default Optional<FittingResultView> getFitForTransitionSeries(ITransitionSeries ts) {
		for (FittingResultView fit : getFits()) {
			if (fit.getTransitionSeries().equals(ts)) {
				return Optional.of(fit);
			}
		}
		return Optional.empty();
	}
	
	
}