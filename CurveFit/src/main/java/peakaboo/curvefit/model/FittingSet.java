package peakaboo.curvefit.model;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peakaboo.curvefit.model.transition.Transition;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesFitting;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * This class acts as a container for a set of {@link TransitionSeries} and maintains a set of {@link TransitionSeriesFitting}s based on various provided parameters. 
 * @author Nathaniel Sherry, 2009-2010
 *
 */

public class FittingSet implements Serializable
{

	private List<TransitionSeriesFitting>	fittings;
	private List<TransitionSeries>			fitTransitionSeries;

	private	EnergyCalibration				calibration;


	private EscapePeakType					escapeType;
	
	

	public FittingSet()
	{
		fittings = new ArrayList<TransitionSeriesFitting>();
		fitTransitionSeries = new ArrayList<TransitionSeries>();

		this.calibration = new EnergyCalibration(0, 0, 0);
		this.escapeType = EscapePeakType.NONE;
	}

	

	public synchronized void setEnergy(float min, float max)
	{
		this.calibration.setMinEnergy(min);
		this.calibration.setMaxEnergy(max);
		regenerateFittings();
	}


	public synchronized void setDataWidth(int dataWidth)
	{
		this.calibration.setDataWidth(dataWidth);
		regenerateFittings();
	}


	public EscapePeakType getEscapeType()
	{
		return escapeType;
	}


	
	public void setEscapeType(EscapePeakType escapeType)
	{
		this.escapeType = escapeType;
		regenerateFittings();
	}
	
	/**
	 * Update several parameters which would require regenerating fittings all in one shot
	 */
	public synchronized void setDataParameters(int dataWidth, float minEnergy, float maxEnergy, EscapePeakType escapeType)
	{
		this.calibration = new EnergyCalibration(minEnergy, maxEnergy, dataWidth);
		this.escapeType = escapeType;
		regenerateFittings();
	}


	
	private synchronized void regenerateFittings()
	{
		fittings.clear();
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
		fittings.add(new TransitionSeriesFitting(ts, calibration, escapeType));
	}


	public synchronized void remove(TransitionSeries ts)
	{
		fitTransitionSeries.remove(ts);

		List<TransitionSeriesFitting> fittingsToRemove = new ArrayList<TransitionSeriesFitting>();
		for (TransitionSeriesFitting f : fittings)
		{

			if (f.transitionSeries.equals(ts))
			{
				fittingsToRemove.add(f);
				break;
			}

		}

		fittings.removeAll(fittingsToRemove);
		
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
		fittings.clear();
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
		
		regenerateFittings();
		
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
		regenerateFittings();
		
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

		regenerateFittings();
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


	/**
	 * Rough method for estimating how intense each {@link TransitionSeries} will be with the 
	 * given data and energy level. This is useful for auto-calibration of energy levels
	 * @param data the spectrum to measure against
	 * @param calibration A custom calibration to allow multithreaded use of this method
	 * @return
	 */
	public Map<TransitionSeries, Float> roughIndivudualHeights(ReadOnlySpectrum data, EnergyCalibration calibration) {
		
		Map<TransitionSeries, Float> heights = new HashMap<>();
		if (calibration.getDataWidth() == 0) {
			return heights;
		}
		
		for (TransitionSeriesFitting f : fittings) {
			if (f.transitionSeries.visible) {
				float height = 0f;
				for (Transition t : f.transitionSeries.getAllTransitions()) {
					int channel = calibration.channelFromEnergy(t.energyValue);
					if (channel >= data.size()) continue;
					if (channel < 0) continue;
					height += data.get(channel);
				}
				heights.put(f.transitionSeries, height);
			}
		}
		
		return heights;
	}
	

	
	public synchronized FittingResultSet calculateFittings(ReadOnlySpectrum data) {
		return calculateFittingsUnsynchronized(data);
	}
	
	public FittingResultSet calculateFittingsUnsynchronized(ReadOnlySpectrum data) {


		if (data.size() != calibration.getDataWidth()) setDataWidth(data.size());
		
		
		FittingResultSet results = new FittingResultSet(data.size());

		Spectrum curve = null;
		float scale, normalization;

		// calculate the fittings
		for (TransitionSeriesFitting f : fittings)
		{
			
			if (f.transitionSeries.visible)
			{

				scale = f.getRatioForCurveUnderData(data);
				curve = f.scaleFitToData(scale);
				normalization = f.getNormalizationScale();
				data = SpectrumCalculations.subtractLists(data, curve, 0.0f);
				
				results.fits.add(new FittingResult(curve, f.transitionSeries, scale, normalization));

				SpectrumCalculations.addLists_inplace(results.totalFit, curve);

			}
			

		}

		results.residual = data;

		return results;

	}
	

	// don't need to synchronize this, since the only interaction
	// it has with fitting data is in the calculateFittings() function
	// which IS synchronized
	public float calculateAreaUnderFit(ReadOnlySpectrum data)
	{

		float result;
		float sum;

		FittingResultSet results = calculateFittings(data);

		sum = 0;
		for (float d : results.totalFit)
		{
			sum += d;
		}
		result = sum /= data.size();

		return result;

	}

}
