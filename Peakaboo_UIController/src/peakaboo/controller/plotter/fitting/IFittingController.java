package peakaboo.controller.plotter.fitting;

import java.util.List;

import eventful.IEventfulType;
import fava.functionable.FList;

import peakaboo.curvefit.fitting.EscapePeakType;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.peaktable.TransitionSeriesType;
import peakaboo.curvefit.results.FittingResultSet;
import scitypes.Spectrum;


public interface IFittingController extends IEventfulType<Boolean>
{

	//For Committed Fittings
	public void addTransitionSeries(TransitionSeries e);
	public void addAllTransitionSeries(List<TransitionSeries> e);
	public void removeTransitionSeries(TransitionSeries e);
	public void clearTransitionSeries();
	
	public FList<TransitionSeries> getFittedTransitionSeries();
	public FList<TransitionSeries> getUnfittedTransitionSeries(TransitionSeriesType tst);
	
	public void setTransitionSeriesVisibility(TransitionSeries e, boolean show);
	public boolean getTransitionSeriesVisibility(TransitionSeries e);
	public FList<TransitionSeries> getVisibleTransitionSeries();
	
	public float getTransitionSeriesIntensity(TransitionSeries ts);
	public void moveTransitionSeriesUp(TransitionSeries e);
	public void moveTransitionSeriesDown(TransitionSeries e);
	public void moveTransitionSeriesUp(List<TransitionSeries> e);
	public void moveTransitionSeriesDown(List<TransitionSeries> e);
	
	public FittingSet getFittingSelections();
	public void calculateSelectionFittings(Spectrum data);
	public void fittingDataInvalidated();
	public boolean hasSelectionFitting();
	public FittingResultSet	getFittingSelectionResults();
	
	public boolean canMap();
	
	
	
	//For Proposed Fittings
	public void addProposedTransitionSeries(TransitionSeries e);
	public void removeProposedTransitionSeries(TransitionSeries e);
	public void clearProposedTransitionSeries();
	
	public List<TransitionSeries> getProposedTransitionSeries();
	public void commitProposedTransitionSeries();
	
	public void calculateProposalFittings();
	public void fittingProposalsInvalidated();
	public boolean hasProposalFitting();
	public FittingResultSet	getFittingProposalResults();
	
	
	//Escape Peaks
	public void setEscapeType(EscapePeakType type);
	public EscapePeakType getEscapeType();
	
	
	
	//Magic
	public List<TransitionSeries> proposeTransitionSeriesFromChannel(final int channel, TransitionSeries currentTransition);
	public void optimizeTransitionSeriesOrdering();
}
