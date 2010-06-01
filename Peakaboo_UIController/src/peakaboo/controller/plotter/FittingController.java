package peakaboo.controller.plotter;

import java.util.List;

import peakaboo.datatypes.eventful.IEventful;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;


public interface FittingController extends IEventful
{

	public void addElement(Element e);
	public void addAllElements(List<Element> e);
	public void removeElement(Element e);
	public void clearElements();
	
	public List<Element> getFittedElements();
	public List<Element> getUnfittedElements();
	
	public void setElementVisibility(Element e, boolean show);
	public boolean getElementVisibility(Element e);
	public List<Element> getVisibleElements();
	
	public List<TransitionSeriesType> getTransitionSeriesTypesForElement(Element e, boolean onlyInEnergyRange);
	public TransitionSeries getTransitionSeriesForElement(Element e, TransitionSeriesType tst);
	public double getTransitionSeriesIntensityForElement(Element e, TransitionSeriesType tst);
	public double getIntensityForElement(Element e);
	public void moveElementUp(Element e);
	public void moveElementDown(Element e);
	
	public void fittingDataInvalidated();
	
	
	
	public void addProposedElement(Element e);
	public void removeProposedElement(Element e);
	public void clearProposedElements();
	
	public List<Element> getProposedElements();
	public void commitProposedElements();
	
	public void fittingProposalsInvalidated();	
	
}
