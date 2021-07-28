package org.peakaboo.curvefit.curve.fitting;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;

/**
 * This class acts as a container for a set of {@link ITransitionSeries} and maintains a set of {@link Curve}s based on various provided parameters. 
 * @author Nathaniel Sherry, 2009-2010
 *
 */

public class FittingSet implements ROFittingSet {

	private EventfulCache<List<Curve>>		curves;
	private List<ITransitionSeries>			fitTransitionSeries;
	
	private FittingParameters				parameters;

	
	public FittingSet() {
		curves = new EventfulNullableCache<>(this::generateCurves);
		fitTransitionSeries = new ArrayList<>();
		this.parameters = new FittingParameters(this);
	}

	/**
	 * Create a new FittingSet by copying a {@link ROFittingParameters}
	 * object.
	 */
	public FittingSet(ROFittingParameters parameters) {
		this();
		this.parameters = new FittingParameters(parameters, this);
	}
	
	
	synchronized void invalidateCurves() {
		curves.invalidate();
	}


	@Override
	public List<Curve> getCurves() {
		return curves.getValue();
	}
	
	@Override
	public List<Curve> getVisibleCurves() {
		return getCurves().stream().filter(c -> c.getTransitionSeries().isVisible()).collect(Collectors.toList());
	}
	
	public synchronized void addTransitionSeries(ITransitionSeries ts) {
		if (fitTransitionSeries.contains(ts)) return;
		fitTransitionSeries.add(ts);
		invalidateCurves();
	}
	
	public synchronized void insertTransitionSeries(int index, ITransitionSeries ts) {
		if (fitTransitionSeries.contains(ts)) return;
		fitTransitionSeries.add(index, ts);
		invalidateCurves();
	}

	
	private synchronized List<Curve> generateCurves() {
		List<Curve> curvelist = new ArrayList<Curve>();
		for (ITransitionSeries ts : fitTransitionSeries) {
			curvelist.add(new Curve(ts, parameters));
		}
		return curvelist;
	}


	public synchronized void remove(ITransitionSeries ts) {
		fitTransitionSeries.remove(ts);
		invalidateCurves();
	}
	
	//if this has been set to false, and it is a primary TS, we may see it again, so we don't want this
	//setting hanging around
	public synchronized void clear() {
		fitTransitionSeries.clear();
		invalidateCurves();
	}


	public synchronized boolean isEmpty() {
		return fitTransitionSeries.isEmpty();
	}

	
	public synchronized boolean moveTransitionSeriesUp(ITransitionSeries e) {
		int insertionPoint;
		boolean movedTS = false;
		ITransitionSeries ts;

		for (int i = 0; i < fitTransitionSeries.size(); i++) {
			if (fitTransitionSeries.get(i).equals(e)) {
				ts = fitTransitionSeries.get(i);
				fitTransitionSeries.remove(ts);
				insertionPoint = i - 1;
				if (insertionPoint == -1) insertionPoint = 0;
				fitTransitionSeries.add(insertionPoint, ts);
				movedTS = insertionPoint != i;
				break;
				
			}
		}
		
		invalidateCurves();
		
		return movedTS;
		
	}
	public synchronized void moveTransitionSeriesUp(List<ITransitionSeries> tss) {
		for (int i = 0; i < tss.size(); i++) {
			//method returns true if it was able to move the TS.
			//if we weren't able to move it, we don't try to move any of them
			if (  ! moveTransitionSeriesUp(tss.get(i))  ) break;
		}
	}


	public synchronized boolean moveTransitionSeriesDown(ITransitionSeries e) {
		int insertionPoint;
		boolean movedTS = false;
		ITransitionSeries ts;

		for (int i = 0; i < fitTransitionSeries.size(); i++) {
			
			if (fitTransitionSeries.get(i).equals(e)) {
								
				ts = fitTransitionSeries.get(i);
				fitTransitionSeries.remove(ts);
				insertionPoint = i + 1;
				if (insertionPoint == fitTransitionSeries.size() + 1) insertionPoint = fitTransitionSeries.size();
				fitTransitionSeries.add(insertionPoint, ts);
				movedTS = insertionPoint != i;
				break;
			}
		}
		invalidateCurves();
		
		return movedTS;
		
	}

	public synchronized void moveTransitionSeriesDown(List<ITransitionSeries> tss) {
		for (int i = tss.size()-1; i >= 0; i--) {
			//method returns true if it was able to move the TS.
			//if we weren't able to move it, we don't try to move any of them
			if (  ! moveTransitionSeriesDown(tss.get(i))  ) break;
		}
	}

	public synchronized boolean hasTransitionSeries(ITransitionSeries ts) {
		if (fitTransitionSeries.contains(ts)) return true;
		return false;
	}


	public synchronized void setTransitionSeriesVisibility(ITransitionSeries ts, boolean show) {
		for (ITransitionSeries e : fitTransitionSeries) {
			if (ts.equals(e)) {
				e.setVisible(show);
			}
		}
		invalidateCurves();
	}

	public void setAllTransitionSeriesVisibility(boolean show) {
		for (ITransitionSeries e : fitTransitionSeries) {
			e.setVisible(show);
		}
		invalidateCurves();
	}
	

	public synchronized List<ITransitionSeries> getFittedTransitionSeries() {
		return new ArrayList<>(fitTransitionSeries);
	}


	public synchronized List<ITransitionSeries> getVisibleTransitionSeries() {
		List<ITransitionSeries> fittedElements = new ArrayList<>();
		for (ITransitionSeries e : fitTransitionSeries) {
			if (e.isVisible()) fittedElements.add(e);
		}
		return fittedElements;
	}

	@Override
	public FittingParameters getFittingParameters() {
		return parameters;
	}


	

}
