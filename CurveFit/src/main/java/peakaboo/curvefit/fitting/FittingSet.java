package peakaboo.curvefit.fitting;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transitionseries.EscapePeakType;
import peakaboo.curvefit.transitionseries.TransitionSeries;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * This class acts as a container for a set of {@link TransitionSeries} and maintains a set of {@link CurveFitter}s based on various provided parameters. 
 * @author Nathaniel Sherry, 2009-2010
 *
 */

public class FittingSet implements Serializable
{

	private List<CurveFitter>				fitters;
	private List<TransitionSeries>			fitTransitionSeries;
	private	EnergyCalibration				calibration;
	private EscapePeakType					escapeType;
	
	

	public FittingSet()
	{
		fitters = new ArrayList<CurveFitter>();
		fitTransitionSeries = new ArrayList<TransitionSeries>();

		this.calibration = new EnergyCalibration(0, 0, 0);
		this.escapeType = EscapePeakType.NONE;
	}

	

	public synchronized void setEnergy(float min, float max)
	{
		if (max < min) {
			throw new RuntimeException("Minimum energy cannot be greater than maximum energy");
		}
		this.calibration.setMinEnergy(min);
		this.calibration.setMaxEnergy(max);
		regenerateFitters();
	}


	public synchronized void setDataWidth(int dataWidth)
	{
		this.calibration.setDataWidth(dataWidth);
		regenerateFitters();
	}


	public EscapePeakType getEscapeType()
	{
		return escapeType;
	}


	
	public void setEscapeType(EscapePeakType escapeType)
	{
		this.escapeType = escapeType;
		regenerateFitters();
	}
	
	/**
	 * Update several parameters which would require regenerating fitters all in one shot
	 */
	public synchronized void setDataParameters(int dataWidth, float minEnergy, float maxEnergy, EscapePeakType escapeType)
	{
		if (maxEnergy < minEnergy) {
			throw new RuntimeException("Minimum energy cannot be greater than maximum energy");
		}
		this.calibration = new EnergyCalibration(minEnergy, maxEnergy, dataWidth);
		this.escapeType = escapeType;
		regenerateFitters();
	}


	
	private synchronized void regenerateFitters()
	{
		fitters.clear();
		for (TransitionSeries ts : fitTransitionSeries)
		{
			addTransitionSeriesToFittings(ts);
		}
	}


	public synchronized void addTransitionSeries(TransitionSeries ts)
	{

		if (fitTransitionSeries.contains(ts)) return;
		
		addTransitionSeriesToFittings(ts);
		fitTransitionSeries.add(ts);

	}


	private synchronized void addTransitionSeriesToFittings(TransitionSeries ts)
	{
		fitters.add(new CurveFitter(ts, calibration, escapeType));
	}


	public synchronized void remove(TransitionSeries ts)
	{
		fitTransitionSeries.remove(ts);

		List<CurveFitter> fittingsToRemove = new ArrayList<CurveFitter>();
		for (CurveFitter f : fitters)
		{

			if (f.getTransitionSeries().equals(ts))
			{
				fittingsToRemove.add(f);
				break;
			}

		}

		fitters.removeAll(fittingsToRemove);
		
		ts.setVisible(true);
		
	}
	
	//if this has been set to false, and it is a primary TS, we may see it again, so we don't want this
	//setting hanging around
	public synchronized void clear()
	{
		
		for (TransitionSeries t : fitTransitionSeries)
		{
			t.setVisible(true);
		}
		
		fitTransitionSeries.clear();
		fitters.clear();
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
		
		regenerateFitters();
		
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
		regenerateFitters();
		
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

		regenerateFitters();
	}


	public synchronized List<TransitionSeries> getFittedTransitionSeries()
	{
		List<TransitionSeries> fittedElements = new ArrayList<TransitionSeries>();

		for (TransitionSeries e : fitTransitionSeries)
		{
			fittedElements.add(e);
		}

		return fittedElements;
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



	
	public synchronized FittingResultSet fit(ReadOnlySpectrum data) {
		return fitUnsynchronized(data);
	}
	
	public FittingResultSet fitUnsynchronized(ReadOnlySpectrum data) {


		if (data.size() != calibration.getDataWidth()) setDataWidth(data.size());
		
		
		FittingResultSet results = new FittingResultSet(data.size());


		// calculate the fitters
		for (CurveFitter fitter : fitters)
		{
			
			if (fitter.getTransitionSeries().visible)
			{

				FittingResult result = fitter.fit(data);
				data = SpectrumCalculations.subtractLists(data, result.getFit(), 0.0f);
				
				//should this be done through a method addFit?
				results.fits.add(result);
				SpectrumCalculations.addLists_inplace(results.totalFit, result.getFit());

			}
			

		}

		results.residual = data;

		return results;

	}
	

	// don't need to synchronize this, since the only interaction
	// it has with fitting data is in the fit() function
	// which IS synchronized
	public float calculateAreaUnderFit(ReadOnlySpectrum data)
	{

		float result;
		float sum;

		FittingResultSet results = fit(data);

		sum = 0;
		for (float d : results.totalFit)
		{
			sum += d;
		}
		result = sum /= data.size();

		return result;

	}

}
