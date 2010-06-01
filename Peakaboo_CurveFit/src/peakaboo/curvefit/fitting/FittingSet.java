package peakaboo.curvefit.fitting;

import java.io.Serializable;
import java.util.List;

import peakaboo.calculations.ListCalculations;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.PeakTable;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;

public class FittingSet implements Serializable{

	private List<TransitionSeriesFitting> fittings;
	private List<Pair<Element, Boolean>> elements;
	private PeakTable peakTable;
	
	private double energyPerChannel;
	private int dataWidth;
	
	public FittingSet(PeakTable peakTable, int dataWidth, double energyPerChannel){
		fittings = DataTypeFactory.<TransitionSeriesFitting>list();
		elements = DataTypeFactory.<Pair<Element, Boolean>>list();
		
		this.peakTable = peakTable;
		this.energyPerChannel = energyPerChannel;
		this.dataWidth = dataWidth;
	}
	public FittingSet(PeakTable peakTable){
		fittings = DataTypeFactory.<TransitionSeriesFitting>list();
		elements = DataTypeFactory.<Pair<Element, Boolean>>list();
		
		this.peakTable = peakTable;
		this.energyPerChannel = 0.0;
		this.dataWidth = 0;
	}
	
	
	public synchronized void setEnergyPerChannel(double energyPerChannel){
		this.energyPerChannel = energyPerChannel;
		regenerateFittings();
	}
	public synchronized void setDataWidth(int dataWidth){
		this.dataWidth = dataWidth;
		regenerateFittings();
	}
	public synchronized void setDataParameters(int dataWidth, double energy){
		this.dataWidth = dataWidth;
		this.energyPerChannel = energy;
		regenerateFittings();
	}
	private synchronized void regenerateFittings(){
		fittings.clear();
		for (Pair<Element, Boolean> e : elements){
			createTransitionsForElement(e.first);
		}
	}
	

	public synchronized void addElement(Element e){
		
		if (elements.indexOf(e) != -1) return;
		
		createTransitionsForElement(e);
		elements.add(new Pair<Element, Boolean>(e, true));

	}
	private synchronized void createTransitionsForElement(Element e){
		List<TransitionSeries> ts = peakTable.getTransitionSeriesForElement(e);
		
		TransitionSeriesFitting f;
		
		for (TransitionSeries t : ts){
			f = new TransitionSeriesFitting(t, dataWidth, energyPerChannel);
			fittings.add(f);
		}
	}
	
	public synchronized void remove(Element e){
		deleteElementFromList(e);
		List<TransitionSeries> ts = peakTable.getTransitionSeriesForElement(e);
		
		List<TransitionSeriesFitting> fittingsToRemove = DataTypeFactory.<TransitionSeriesFitting>list();
		for (TransitionSeriesFitting f : fittings){
			for (TransitionSeries t : ts) {
				
				if (f.transitionSeries == t) {
					fittingsToRemove.add(f);
					break;
				}
				
			}
		}
		
		fittings.removeAll(fittingsToRemove);

	}
	
	public synchronized void moveElementUp(Element e){
		int insertionPoint;
		Pair<Element, Boolean> data;
		
		for (int i = 0; i < elements.size(); i++){
			if (elements.get(i).first == e){
				data = elements.get(i);
				elements.remove(data);
				insertionPoint = i-1;
				if (insertionPoint == -1) insertionPoint = 0;
				elements.add(insertionPoint, data);
				break;
			}
		}
		regenerateFittings();
	}
	public synchronized void moveElementDown(Element e){
		int insertionPoint;
		Pair<Element, Boolean> data;
		
		for (int i = 0; i < elements.size(); i++){
			if (elements.get(i).first == e){
				data = elements.get(i);
				elements.remove(data);
				insertionPoint = i+1;
				if (insertionPoint == elements.size()+1) insertionPoint = elements.size();
				elements.add(insertionPoint, data);
				break;
			}
		}
		regenerateFittings();
	}
	
	
	public synchronized boolean hasElement(Element e){
		if (getElementData(e) != null) return true;
		return false;
	}
	private synchronized Pair<Element, Boolean> getElementData(Element e){
		for (Pair<Element, Boolean> pair : elements){
			if (pair.first == e) return pair;
		}
		return null;
	}

	private synchronized void deleteElementFromList(Element e){
		elements.remove(getElementData(e));
	}
	
	public synchronized void setElementVisibility(Element element, boolean show){
		for (Pair<Element, Boolean> e : elements){
			if (e.first == element) {
				e.second = show;
				break;
			}
		}
	}
	
	
	public synchronized boolean getElementVisibilty(Element element){
		
		for (Pair<Element, Boolean> e : elements){
			if (e.first == element) {
				return e.second.booleanValue();
			}
		}
		return false;
	}
	
	public synchronized List<Element> getFittedElements(){
		List<Element> fittedElements = DataTypeFactory.<Element>list();
		
		for (Pair<Element, Boolean> e : elements){
			fittedElements.add(e.first);
		}
		
		return fittedElements;
	}
	
	public synchronized List<TransitionSeries> getTransitionSeries(){
		
		List<TransitionSeries> result = DataTypeFactory.<TransitionSeries>list();
		
		for (TransitionSeriesFitting f : fittings){
			result.add(f.transitionSeries);
		}
		
		return result;
		
	}
	
	public synchronized List<TransitionSeries> getVisibleTransitionSeries(){
		
		List<TransitionSeries> result = DataTypeFactory.<TransitionSeries>list();
		
		for (TransitionSeriesFitting f : fittings){
			if ( f.transitionSeries.visible && getElementVisibilty(f.transitionSeries.element) ) result.add(f.transitionSeries);
		}
		
		return result;
		
	}
		
	public synchronized void clear(){
		elements.clear();
		fittings.clear();
	}
	public synchronized boolean isEmpty(){
		return elements.isEmpty();
	}
	
	//calculates fittings, residual, total curve
	public synchronized FittingResultSet calculateFittings(List<Double> data){
		
		FittingResultSet results = new FittingResultSet();
		results.fits = DataTypeFactory.<FittingResult>list();
		
		
		List<Double> curve = null;
		double scale, normalization;
		
		//calculate the fittings
		for (TransitionSeriesFitting f : fittings){
			
			if (getElementVisibilty(f.transitionSeries.element) && f.transitionSeries.visible){
						
				scale = f.getRatioForCurveUnderData(data);
				curve = f.scaleFitToData(scale);
				normalization = f.getNormalizationScale();
				data = ListCalculations.subtractLists(data, curve, 0.0);
				
				results.fits.add(new FittingResult(curve, f.transitionSeries, scale, normalization) );
				
				if (results.totalFit == null){
					results.totalFit = DataTypeFactory.<Double>listInit(curve);
				} else {
					ListCalculations.addLists_inplace(results.totalFit, curve);
				}
			}
			
		}
		
		results.residual = data;
		
		return results;
		
	}

	
	//don't need to synchronize this, since the only interaction
	//it has with fitting data is in the calculateFittings() function
	//which IS synchronized
	public double calculateAreaUnderFit(List<Double> data){
			
		double result;
		double sum;
					
		FittingResultSet results = calculateFittings(data);
		
		sum = 0;
		for (double d : results.totalFit)
		{
			sum += d;
		}
		result = sum /= data.size();

		
		return result;
		
	}
	
	

}
