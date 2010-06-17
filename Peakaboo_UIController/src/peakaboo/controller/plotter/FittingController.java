package peakaboo.controller.plotter;

import java.util.List;

import peakaboo.datatypes.eventful.IEventful;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;


public interface FittingController extends IEventful
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
	public List<TransitionSeriesType> getTransitionSeriesTypesForElement(Element e, boolean onlyInEnergyRange);
	public TransitionSeries getTransitionSeriesForElement(Element e, TransitionSeriesType tst);
	public float getTransitionSeriesIntensityForElement(Element e, TransitionSeriesType tst);
	public float getIntensityForElement(Element e);
	public void moveTransitionSeriesUp(TransitionSeries e);
	public void moveTransitionSeriesDown(TransitionSeries e);
	
	public void fittingDataInvalidated();
	
	
	
	public void addProposedTransitionSeries(TransitionSeries e);
	public void removeProposedTransitionSeries(TransitionSeries e);
	public void clearProposedTransitionSeries();
	
	public List<TransitionSeries> getProposedTransitionSeries();
	public void commitProposedTransitionSeries();
	
	public void fittingProposalsInvalidated();	
	
}
