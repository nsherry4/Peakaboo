package peakaboo.controller.plotter;

import java.util.List;

import eventful.IEventfulType;

import peakaboo.curvefit.fitting.EscapePeakType;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;


public interface FittingController extends IEventfulType<String>
{

	//For Committed Fittings
	public void addTransitionSeries(TransitionSeries e);
	public void addAllTransitionSeries(List<TransitionSeries> e);
	public void removeTransitionSeries(TransitionSeries e);
	public void clearTransitionSeries();
	
	public List<TransitionSeries> getFittedTransitionSeries();
	public List<TransitionSeries> getUnfittedTransitionSeries(TransitionSeriesType tst);
	
	public void setTransitionSeriesVisibility(TransitionSeries e, boolean show);
	public boolean getTransitionSeriesVisibility(TransitionSeries e);
	public List<TransitionSeries> getVisibleTransitionSeries();
	
	public float getTransitionSeriesIntensity(TransitionSeries ts);
	public void moveTransitionSeriesUp(TransitionSeries e);
	public void moveTransitionSeriesDown(TransitionSeries e);
	
	public void fittingDataInvalidated();
	
	
	
	//For Proposed Fittings
	public void addProposedTransitionSeries(TransitionSeries e);
	public void removeProposedTransitionSeries(TransitionSeries e);
	public void clearProposedTransitionSeries();
	
	public List<TransitionSeries> getProposedTransitionSeries();
	public void commitProposedTransitionSeries();
	
	public void fittingProposalsInvalidated();	
	
	
	
	//Escape Peaks
	public void setEscapeType(EscapePeakType type);
	public EscapePeakType getEscapeType();
	
	
	
	//Magic
	public List<TransitionSeries> proposeTransitionSeriesFromChannel(final int channel, TransitionSeries currentTransition);
	public void optimizeTransitionSeriesOrdering();
}
