package peakaboo.curvefit.automation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;
import fava.Fn;
import fava.Functions;
import fava.datatypes.Pair;
import fava.lists.FList;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;


public class TSOrdering
{


	public static FList<TransitionSeries> optimizeTSOrdering(final float energyPerChannel, final FList<TransitionSeries> unfitted, final Spectrum s)
	{
		FList<TransitionSeries> ordered = unfitted.toSink();

		Collections.sort(ordered, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				return compareTSs(ts1, ts2, energyPerChannel, s);
			}
		});
		
		return ordered;
	}
	
	

	public static FunctionMap<TransitionSeries, Float> fScoreTransitionSeries(final float energyPerChannel, final Spectrum spectrum)
	{
		return fScoreTransitionSeries(energyPerChannel, spectrum, null, true);
	}
	
	public static FunctionMap<TransitionSeries, Float> fScoreTransitionSeries(final float energyPerChannel, final Spectrum spectrum, boolean useBaseSize)
	{
		return fScoreTransitionSeries(energyPerChannel, spectrum, null, useBaseSize);
	}
	
	public static FunctionMap<TransitionSeries, Float> fScoreTransitionSeries(final float energyPerChannel, final Spectrum spectrum, final Float energy, final boolean useBaseSize)
	{
	
		//scoring function to evaluate each TransitionSeries
		return new FunctionMap<TransitionSeries, Float>() {

			TransitionSeriesFitting tsf = new TransitionSeriesFitting(null, spectrum.size(), energyPerChannel, FittingSet.escape);
			Spectrum s = new Spectrum(spectrum);
			
			public Float f(TransitionSeries ts)
			{
				Double prox;
				if (energy == null)
				{
					prox = 1.0;
				} else  {
					prox = Math.abs(ts.getProximityToEnergy(energy));
					if (prox <= 0.001) prox = 0.001;
					prox = Math.log1p(prox);
					
				}

				tsf.setTransitionSeries(ts);
				Float ratio, remainingArea;
				
				//get the fitting ratio, and the fitting spectrum
				ratio = tsf.getRatioForCurveUnderData(s);
				Spectrum fitting = tsf.scaleFitToData(ratio);
				//remove this fitting from the spectrum
				SpectrumCalculations.subtractLists_inplace(s, fitting, 0.0f);
				
				//square the values left in s
				Spectrum unfit = SpectrumCalculations.multiplyLists(s, s);
				
				remainingArea = SpectrumCalculations.sumValuesInList(unfit) / s.size();

				if (useBaseSize)
				{
					return (float)( remainingArea * tsf.getSizeOfBase() * prox );
				} else {
					return (float)( remainingArea * prox );
				}
				
			}
		};
		
	}
	
	
	
	
	//get a list of all TSs which overlap with the given TS from the list of TSs 
	public static FList<TransitionSeries> getTSsOverlappingTS(final TransitionSeries ts, final FList<TransitionSeries> tss, float energyPerChannel, int spectrumSize)
	{
		final TransitionSeriesFitting tsf1 = new TransitionSeriesFitting(null, spectrumSize, energyPerChannel, FittingSet.escape);
		final TransitionSeriesFitting tsf2 = new TransitionSeriesFitting(null, spectrumSize, energyPerChannel, FittingSet.escape);
		
		//we want the true flag so that we make sure that elements which overlap an escape peak are still considered overlapping
		tsf1.setTransitionSeries(ts, true);
		
		//map all other TSs to booleans to check if this overlaps
		return tss.filter(new FunctionMap<TransitionSeries, Boolean>() {

			public Boolean f(TransitionSeries otherts)
			{
										
				if (otherts.equals(ts)) return false;	//its not overlapping if its the same TS
				
				tsf2.setTransitionSeries(otherts, true);						
				return (tsf1.isOverlapping(tsf2));
				
			}
		});
	}
	
	
	//get a list of ordered pairs, each pair containing ts and one element from tss. ordering determines which TS ordering results in a better fit
	private static FList<Pair<TransitionSeries, TransitionSeries>> getOrderedTSPairs(final TransitionSeries ts, final FList<TransitionSeries> tss, final float energyPerChannel, final Spectrum s)
	{
		return tss.map(new FunctionMap<TransitionSeries, Pair<TransitionSeries, TransitionSeries>>() {

			public Pair<TransitionSeries, TransitionSeries> f(TransitionSeries ts2)
			{
				return orderTSPairByScore(ts, ts2, energyPerChannel, s);
			}
		});
	}
	
	
	
	//generate a map between each TS in tss, and a list of ordered pairs each containing the TS and one other TS, where the ordering indicates the preferred fitting sequence for best results
	public static Map<TransitionSeries, FList<Pair<TransitionSeries, TransitionSeries>>> getOrderedTSPairsMap(final FList<TransitionSeries> tss, final float energyPerChannel, final Spectrum s)
	{
		
		final Map<TransitionSeries, FList<Pair<TransitionSeries, TransitionSeries>>> pairsForTS = DataTypeFactory.map();
		
		
		tss.each(new FunctionEach<TransitionSeries>() {

			public void f(TransitionSeries ts)
			{
				//get a list of all TSs which overlap with this one
				FList<TransitionSeries> otherTSs = getTSsOverlappingTS(ts, tss, energyPerChannel, s.size());
				
				//generate the ordered pairs from the list of overlapping TSs, and add it to the map
				pairsForTS.put(ts, getOrderedTSPairs(ts, otherTSs, energyPerChannel, s));
				
			}
		});
		
		return pairsForTS;		
		
	}
	
	

	//accept two transition series, and return an ordered pair, where the ordering indicates the preferred fitting sequence for best results
	private static Pair<TransitionSeries, TransitionSeries> orderTSPairByScore(final TransitionSeries ts1, final TransitionSeries ts2, final float energyPerChannel, final Spectrum s)
	{
				
		Pair<TransitionSeries, TransitionSeries> order = new Pair<TransitionSeries, TransitionSeries>();
		
		Float ordering1, ordering2;
		FunctionMap<TransitionSeries, Float> scorer;
		
		scorer = fScoreTransitionSeries(energyPerChannel, s, false);
		scorer.f(ts1);
		ordering1 = scorer.f(ts2);
		
		scorer = fScoreTransitionSeries(energyPerChannel, s, false);
		scorer.f(ts2);
		ordering2 = scorer.f(ts1);		
		
		if (ordering1 < ordering2)
		{
			order.first = ts1;
			order.second = ts2;
		} else {
			order.first = ts2;
			order.second = ts1;
		}
		
		return order;
	}
	
	
	private static int compareTSs(TransitionSeries ts1, TransitionSeries ts2, final float energyPerChannel, final Spectrum s)
	{
		Pair<TransitionSeries, TransitionSeries> orderedPair = orderTSPairByScore(ts1, ts2, energyPerChannel, s);
		if (orderedPair.first == ts1) return -1;
		return 1;
	}
	

	
}
