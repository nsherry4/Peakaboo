package peakaboo.curvefit.automation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import peakaboo.curvefit.fitting.EscapePeakType;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;
import fava.Fn;
import fava.Functions;
import fava.datatypes.Pair;
import fava.lists.FList;
import fava.signatures.FunctionCall;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;


public class TSOrdering
{


	public static FList<TransitionSeries> optimizeTSOrdering(final float energyPerChannel, final FList<TransitionSeries> unfitted, final Spectrum s, final EscapePeakType escape)
	{
		FList<TransitionSeries> ordered = unfitted.toSink();

		Collections.sort(ordered, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				return compareTSs(ts1, ts2, energyPerChannel, s, escape);
			}
		});
		
		return ordered;
	}
	
	

	public static FunctionMap<TransitionSeries, Float> fScoreTransitionSeries(EscapePeakType escape, final float energyPerChannel, final Spectrum spectrum)
	{
		return fScoreTransitionSeries(escape, energyPerChannel, spectrum, null, true);
	}
	
	public static FunctionMap<TransitionSeries, Float> fScoreTransitionSeries(EscapePeakType escape, final float energyPerChannel, final Spectrum spectrum, boolean useBaseSize)
	{
		return fScoreTransitionSeries(escape, energyPerChannel, spectrum, null, useBaseSize);
	}
	
	public static FunctionMap<TransitionSeries, Float> fScoreTransitionSeries(EscapePeakType escape, final float energyPerChannel, final Spectrum spectrum, final Float energy, final boolean useBaseSize)
	{
		return fScoreTransitionSeries(escape, energyPerChannel, spectrum, energy, useBaseSize, TransitionSeriesFitting.defaultStandardDeviations);
	}
	
	public static FunctionMap<TransitionSeries, Float> fScoreTransitionSeries(final EscapePeakType escape, final float energyPerChannel, final Spectrum spectrum, final Float energy, final boolean useBaseSize, final float stddevs)
	{
	
		//scoring function to evaluate each TransitionSeries
		return new FunctionMap<TransitionSeries, Float>() {

			TransitionSeriesFitting tsf = new TransitionSeriesFitting(null, spectrum.size(), energyPerChannel, escape, stddevs);
			Spectrum s = new Spectrum(spectrum);
			
			public Float f(TransitionSeries ts)
			{
				double prox;
				if (energy == null)
				{
					prox = 1.0;
				} else  {
					prox = ts.getProximityScore(energy, (double)energyPerChannel*2); //Math.abs(ts.getProximityToEnergy(energy));
					//if (prox <= energyPerChannel*10) prox = energyPerChannel*10;
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
	public static FList<TransitionSeries> getTSsOverlappingTS(final TransitionSeries ts, final FList<TransitionSeries> tss, float energyPerChannel, int spectrumSize, final EscapePeakType escape)
	{
		final TransitionSeriesFitting tsf1 = new TransitionSeriesFitting(null, spectrumSize, energyPerChannel, escape);
		final TransitionSeriesFitting tsf2 = new TransitionSeriesFitting(null, spectrumSize, energyPerChannel, escape);
		
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
	private static FList<Pair<TransitionSeries, TransitionSeries>> getOrderedTSPairs(final TransitionSeries ts, final FList<TransitionSeries> tss, final float energyPerChannel, final Spectrum s, final EscapePeakType escape)
	{
		return tss.map(new FunctionMap<TransitionSeries, Pair<TransitionSeries, TransitionSeries>>() {

			public Pair<TransitionSeries, TransitionSeries> f(TransitionSeries ts2)
			{
				return orderTSPairByScore(ts, ts2, energyPerChannel, s, escape);
			}
		});
	}
	
	
	
	//generate a map between each TS in tss, and a list of ordered pairs each containing the TS and one other TS, where the ordering indicates the preferred fitting sequence for best results
	public static Map<TransitionSeries, FList<Pair<TransitionSeries, TransitionSeries>>> getOrderedTSPairsMap(final FList<TransitionSeries> tss, final float energyPerChannel, final Spectrum s, final EscapePeakType escape)
	{
		
		final Map<TransitionSeries, FList<Pair<TransitionSeries, TransitionSeries>>> pairsForTS = DataTypeFactory.map();
		
		
		tss.each(new FunctionEach<TransitionSeries>() {

			public void f(TransitionSeries ts)
			{
				//get a list of all TSs which overlap with this one
				FList<TransitionSeries> otherTSs = getTSsOverlappingTS(ts, tss, energyPerChannel, s.size(), escape);
				
				//generate the ordered pairs from the list of overlapping TSs, and add it to the map
				pairsForTS.put(ts, getOrderedTSPairs(ts, otherTSs, energyPerChannel, s, escape));
				
			}
		});
		
		return pairsForTS;		
		
	}
	
	

	//accept two transition series, and return an ordered pair, where the ordering indicates the preferred fitting sequence for best results
	private static Pair<TransitionSeries, TransitionSeries> orderTSPairByScore(final TransitionSeries ts1, final TransitionSeries ts2, final float energyPerChannel, final Spectrum s, final EscapePeakType escape)
	{
				
		Pair<TransitionSeries, TransitionSeries> order = new Pair<TransitionSeries, TransitionSeries>();
		
		Float ordering1, ordering2;
		FunctionMap<TransitionSeries, Float> scorer;
		
		scorer = fScoreTransitionSeries(escape, energyPerChannel, s, false);
		scorer.f(ts1);
		ordering1 = scorer.f(ts2);
		
		scorer = fScoreTransitionSeries(escape, energyPerChannel, s, false);
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
	
	
	private static int compareTSs(TransitionSeries ts1, TransitionSeries ts2, final float energyPerChannel, final Spectrum s, final EscapePeakType escape)
	{
		Pair<TransitionSeries, TransitionSeries> orderedPair = orderTSPairByScore(ts1, ts2, energyPerChannel, s, escape);
		if (orderedPair.first == ts1) return -1;
		return 1;
	}
	
	
	
	
	

	public static List<TransitionSeries> proposeTransitionSeriesFromChannel(
			final EscapePeakType escape,
			final float energyPerChannel, 
			final Spectrum data, 
			final FittingSet fits,
			final FittingSet proposed,
			final List<TransitionSeries> allTransitionSeries,
			final int channel, 
			TransitionSeries currentTS
	)
	{
		
		
		
		/*
		 * 
		 * Method description
		 * ------------------
		 * 
		 * We try to figure out which Transition Series are the best fit for the given channel.
		 * This is done in the following steps
		 * 
		 * 1. If we have suggested a TS previously, it should be passed in in currentTS
		 * 2. If currentTS isn't null, we remove it from the proposals, refit, and then readd it
		 * 		* we do this so that we can still suggest that same TS this time, otherwise, there would be no signal for it to fit
		 * 3. We get all TSs from the peak table, and add all summations of all fitted & proposed TSs
		 * 4. We remove all TSs which are already fitted or proposed.
		 * 5. We add currentTS to the list, since the last step will have removed it
		 * 6. We unique the list, don't want duplicates showing up
		 * 7. We sort by proximity, and take the top 15
		 * 8. We sort by a more detailed scoring function which involves fitting each TS and seeing how well it fits
		 * 9. We return the top 5 from the list in the last step
		 * 
		 */
	
		
		//remove the current transitionseries from the list of proposed trantision series so we can re-suggest it.
		//otherwise, the copy getting fitted eats all the signal from the one we would suggest during scoring
		boolean currentTSisUsed = currentTS != null && proposed.getFittedTransitionSeries().contains(currentTS);
		if (currentTSisUsed) proposed.remove(currentTS);
		
		//recalculate
		FittingResultSet fitResults = fits.calculateFittings(data);
		FittingResultSet proposedResults = proposed.calculateFittings(fitResults.residual);
		
		
		final Spectrum s = proposedResults.residual;
		
		if (currentTSisUsed) proposed.addTransitionSeries(currentTS);
		

		final float energy = channel * energyPerChannel;	


		//get a list of all transition series to start with
		FList<TransitionSeries> tss = Fn.map(allTransitionSeries, Functions.<TransitionSeries>id());

		
		//add in any 2x summations from the list of previously fitted AND proposed peaks.
		//we exclude any that the caller requests so that if a UI component is *replacing* a TS with
		//these suggestions, it doesn't get summations for the now-removed TS
		List<TransitionSeries> summationCandidates = fits.getFittedTransitionSeries();
		summationCandidates.addAll(proposed.getFittedTransitionSeries());
		if (currentTSisUsed) summationCandidates.remove(currentTS);
		
		for (TransitionSeries ts1 : summationCandidates)
		{
			for (TransitionSeries ts2 : summationCandidates)
			{
				tss.add(ts1.summation(ts2));
			}
		}
		

		//remove the transition series we have already fit, including any summations
		tss.removeAll(fits.getFittedTransitionSeries());
		tss.removeAll(proposed.getFittedTransitionSeries());
		
		
		//We then re-add the TS passed to us so that we can still suggest the 
		//TS that is currently selected, if it fits
		if (currentTSisUsed) {
			tss.add(currentTS);
		}
		
		
		//remove any duplicates we might have created while adding the summations
		tss = Fn.unique(tss);
		

		//sort first by how close they are to the channel in quesiton
		Fn.sortBy(tss, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				Double prox1, prox2;

				prox1 = Math.abs(ts1.getProximityToEnergy(energy));
				prox2 = Math.abs(ts2.getProximityToEnergy(energy));

				return prox1.compareTo(prox2);

			}
		}, Functions.<TransitionSeries> id());
		
		//take the top n based on position alone
		tss = tss.take(15);
		
		//now sort by score
		Collections.sort(tss, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				Float prox1, prox2;

				prox1 = TSOrdering.fScoreTransitionSeries(escape, energyPerChannel, s, energy, true).f(ts1);
				prox2 = TSOrdering.fScoreTransitionSeries(escape, energyPerChannel, s, energy, true).f(ts2);
				
				return prox1.compareTo(prox2);

			}
		});
			
		//take the 5 best in sorted order based on score
		return tss.take(5);
	}

	

	
}
