package peakaboo.curvefit.fitting;



import java.io.Serializable;
import java.util.List;

import fava.*;
import static fava.Fn.*;

import peakaboo.calculations.SpectrumCalculations;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.peaktable.TransitionSeries;



public class FittingSet implements Serializable
{

	private List<TransitionSeriesFitting>	fittings;
	private List<TransitionSeries>			fitTransitionSeries;

	private float							energyPerChannel;
	private int								dataWidth;

	public static float						escape	= 1.74f;


	public FittingSet(int dataWidth, float energyPerChannel)
	{
		fittings = DataTypeFactory.<TransitionSeriesFitting> list();
		fitTransitionSeries = DataTypeFactory.<TransitionSeries> list();

		this.energyPerChannel = energyPerChannel;
		this.dataWidth = dataWidth;
	}


	public FittingSet()
	{
		fittings = DataTypeFactory.<TransitionSeriesFitting> list();
		fitTransitionSeries = DataTypeFactory.<TransitionSeries> list();

		this.energyPerChannel = 0.0f;
		this.dataWidth = 0;
	}


	public synchronized void setEnergyPerChannel(float energyPerChannel)
	{
		this.energyPerChannel = energyPerChannel;
		regenerateFittings();
	}


	public synchronized void setDataWidth(int dataWidth)
	{
		this.dataWidth = dataWidth;
		regenerateFittings();
	}


	public synchronized void setDataParameters(int dataWidth, float energy)
	{
		this.dataWidth = dataWidth;
		this.energyPerChannel = energy;
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

		if (include(fitTransitionSeries, ts)) return;
		
		addTransitionSeriesToFittings(ts);
		fitTransitionSeries.add(ts);

	}


	private synchronized void addTransitionSeriesToFittings(TransitionSeries ts)
	{
		fittings.add(new TransitionSeriesFitting(ts, dataWidth, energyPerChannel, escape));
	}


	public synchronized void remove(TransitionSeries ts)
	{
		fitTransitionSeries.remove(ts);

		List<TransitionSeriesFitting> fittingsToRemove = DataTypeFactory.<TransitionSeriesFitting> list();
		for (TransitionSeriesFitting f : fittings)
		{

			if (f.transitionSeries.equals(ts))
			{
				fittingsToRemove.add(f);
				break;
			}

		}

		fittings.removeAll(fittingsToRemove);
		
	}


	public synchronized void moveTransitionSeriesUp(TransitionSeries e)
	{
		int insertionPoint;
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
				break;
			}
		}
		regenerateFittings();
	}


	public synchronized void moveTransitionSeriesDown(TransitionSeries e)
	{
		int insertionPoint;
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
				break;
			}
		}
		regenerateFittings();
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
		List<TransitionSeries> fittedElements = DataTypeFactory.<TransitionSeries> list();

		for (TransitionSeries e : fitTransitionSeries)
		{
			fittedElements.add(e);
		}

		return fittedElements;
	}


	public synchronized List<TransitionSeries> getVisibleTransitionSeries()
	{

		List<TransitionSeries> fittedElements = DataTypeFactory.<TransitionSeries> list();

		for (TransitionSeries e : fitTransitionSeries)
		{
			if (e.visible) fittedElements.add(e);
		}

		return fittedElements;

	}


	public synchronized void clear()
	{
		fitTransitionSeries.clear();
		fittings.clear();
	}


	public synchronized boolean isEmpty()
	{
		return fitTransitionSeries.isEmpty();
	}


	// calculates fittings, residual, total curve
	public synchronized FittingResultSet calculateFittings(Spectrum data)
	{

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
	public float calculateAreaUnderFit(Spectrum data)
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
