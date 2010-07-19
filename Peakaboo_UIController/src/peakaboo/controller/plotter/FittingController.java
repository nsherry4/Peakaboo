package peakaboo.controller.plotter;

import java.util.List;

import eventful.IEventfulType;

import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;


public interface FittingController extends IEventfulType<String>
{

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
	
	
	
	public void addProposedTransitionSeries(TransitionSeries e);
	public void removeProposedTransitionSeries(TransitionSeries e);
	public void clearProposedTransitionSeries();
	
	public List<TransitionSeries> getProposedTransitionSeries();
	public void commitProposedTransitionSeries();
	
	public void fittingProposalsInvalidated();	
	
	public List<TransitionSeries> proposeTransitionSeriesFromChannel(final int channel, TransitionSeries currentTransition);
	public void optimizeTransitionSeriesOrdering();
}
