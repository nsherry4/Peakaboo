package peakaboo.curvefit.fitting;



import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;
import scitypes.SpectrumCalculations;

/**
 * This class acts as a container for a set of {@link TransitionSeries} and maintains a set of {@link Curve}s based on various provided parameters. 
 * @author Nathaniel Sherry, 2009-2010
 *
 */

public class FittingSet
{

	private List<Curve>						curves;
	private boolean							curvesValid = false;
	private List<TransitionSeries>			fitTransitionSeries;
	
	private FittingParameters				parameters;

	
	public FittingSet() {
		curves = new ArrayList<Curve>();
		fitTransitionSeries = new ArrayList<TransitionSeries>();
		this.parameters = new FittingParameters(this);
	}

	
	synchronized void invalidateCurves() {
		curves.clear();
		curvesValid = false;
	}


	public List<Curve> getCurves() {
		if (!curvesValid) {
			synchronized (this) {
				if (!curvesValid) {
					for (TransitionSeries ts : fitTransitionSeries) {
						generateCurve(ts);
					}
					curvesValid = true;
				}
			}
		}
		return new ArrayList<>(curves);
	}
	
	public synchronized void addTransitionSeries(TransitionSeries ts)
	{

		if (fitTransitionSeries.contains(ts)) return;
		fitTransitionSeries.add(ts);
		invalidateCurves();

	}

	private synchronized void generateCurve(TransitionSeries ts)
	{
		curves.add(new Curve(ts, parameters));
	}


	public synchronized void remove(TransitionSeries ts)
	{
		fitTransitionSeries.remove(ts);
		invalidateCurves();
	}
	
	//if this has been set to false, and it is a primary TS, we may see it again, so we don't want this
	//setting hanging around
	public synchronized void clear()
	{
		fitTransitionSeries.clear();
		invalidateCurves();
	}


	public synchronized boolean isEmpty()
	{
		return fitTransitionSeries.isEmpty();
	}

	
	public synchronized boolean moveTransitionSeriesUp(TransitionSeries e)
	{
		int insertionPoint;
		boolean movedTS = false;
		TransitionSeries ts;

		for (int i = 0; i < fitTransitionSeries.size(); i++)
		{
			if (fitTransitionSeries.get(i).equals(e))
			{
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
	public synchronized void moveTransitionSeriesUp(List<TransitionSeries> tss)
	{
		for (int i = 0; i < tss.size(); i++)
		{
			//method returns true if it was able to move the TS.
			//if we weren't able to move it, we don't try to move any of them
			if (  ! moveTransitionSeriesUp(tss.get(i))  ) break;
		}
	}


	public synchronized boolean moveTransitionSeriesDown(TransitionSeries e)
	{
		int insertionPoint;
		boolean movedTS = false;
		TransitionSeries ts;

		for (int i = 0; i < fitTransitionSeries.size(); i++)
		{
			
			if (fitTransitionSeries.get(i).equals(e))
			{
								
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

	public synchronized void moveTransitionSeriesDown(List<TransitionSeries> tss)
	{
		for (int i = tss.size()-1; i >= 0; i--)
		{
			//method returns true if it was able to move the TS.
			//if we weren't able to move it, we don't try to move any of them
			if (  ! moveTransitionSeriesDown(tss.get(i))  ) break;
		}
	}

	public synchronized boolean hasTransitionSeries(TransitionSeries ts)
	{
		if (fitTransitionSeries.contains(ts)) return true;
		return false;
	}


	public synchronized void setTransitionSeriesVisibility(TransitionSeries ts, boolean show)
	{
		for (TransitionSeries e : fitTransitionSeries)
		{
			if (ts.equals(e))
			{
				e.visible = show;
			}
		}

		invalidateCurves();
	}


	public synchronized List<TransitionSeries> getFittedTransitionSeries()
	{
		return new ArrayList<>(fitTransitionSeries);
	}


	public synchronized List<TransitionSeries> getVisibleTransitionSeries()
	{

		List<TransitionSeries> fittedElements = new ArrayList<TransitionSeries>();

		for (TransitionSeries e : fitTransitionSeries)
		{
			if (e.visible) fittedElements.add(e);
		}

		return fittedElements;

	}

	public FittingParameters getFittingParameters() {
		return parameters;
	}
	

}
