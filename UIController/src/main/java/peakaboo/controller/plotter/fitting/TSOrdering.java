package peakaboo.controller.plotter.fitting;


import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import peakaboo.common.PeakabooLog;
import peakaboo.curvefit.fitting.Curve;
import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.fitting.FittingResult;
import peakaboo.curvefit.fitting.FittingResultSet;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.peaktable.PeakTable;
import peakaboo.curvefit.scoring.EnergyMatchScorer;
import peakaboo.curvefit.scoring.Scorer;
import peakaboo.curvefit.transition.TransitionSeries;
import plural.streams.StreamExecutor;
import plural.streams.StreamExecutor.State;
import scitypes.ISpectrum;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


/**
 * This class contains functions to do things like score, suggest, or sort TransitionSeries based on provided data.
 * @author Nathaniel Sherry, 2010
 *
 */

public class TSOrdering
{


	/**
	 * Attempts to find an optimal ordering for a given list of {@link TransitionSeries}
	 * @return an ordered list of {@link TransitionSeries}
	 */
	public static List<TransitionSeries> optimizeTSOrdering(
			FittingParameters parameters,
			List<TransitionSeries> unfitted, 
			Spectrum s)
	{
		List<TransitionSeries> ordered = new ArrayList<>(unfitted);

		Collections.sort(ordered, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				return compareTSs(ts1, ts2, parameters, s);
			}
		});
		
		return ordered;
	}
	
	
	/**
	 * Creates an anonymous function to score a {@link TransitionSeries}
	 * @return a score for this {@link TransitionSeries}
	 */
	public static Scorer fScoreTransitionSeries(
			FittingParameters parameters,
			final ReadOnlySpectrum spectrum, 
			boolean useBaseSize)
	{
		return fScoreTransitionSeries(parameters, spectrum, null, useBaseSize);
	}
	
	/**
	 * Creates an anonymous function to score a {@link TransitionSeries}
	 * @return a score for this {@link TransitionSeries}
	 */
	public static Scorer fScoreTransitionSeries(
			final FittingParameters parameters,
			final ReadOnlySpectrum spectrum, 
			final Float energy, 
			final boolean useBaseSize
		)
	{
	
		return new EnergyMatchScorer(spectrum, parameters, energy);
		
//		//scoring function to evaluate each TransitionSeries
//		return new Function<TransitionSeries, Float>() {
//
//			Curve curve = new Curve(null, parameters);
//			Spectrum s = new ISpectrum(spectrum);
//			
//			public Float apply(TransitionSeries ts)
//			{
//				double prox;
//				if (energy == null)
//				{
//					prox = 1.0;
//				} else  {
//					prox = ts.getProximityScore(energy, ((double)(parameters.getCalibration().energyPerChannel()))*2d); //Math.abs(ts.getProximityToEnergy(energy));
//					//if (prox <= energyPerChannel*10) prox = energyPerChannel*10;
//					prox = Math.log1p(prox);
//					
//				}
//				
//				curve.setTransitionSeries(ts);
//				Float remainingArea;
//				
//				//get the fitting ratio, and the fitting spectrum
//				FittingResult result = curve.fit(s);
//				//remove this fitting from the spectrum
//				SpectrumCalculations.subtractLists_inplace(s, result.getFit(), 0.0f);
//				
//				//square the values left in s
//				Spectrum unfit = SpectrumCalculations.multiplyLists(s, s);
//				
//				remainingArea = unfit.sum() / s.size();
//				
//				if (useBaseSize)
//				{
//					return (float)( remainingArea * curve.getSizeOfBase() * prox );
//				} else {
//					return (float)( remainingArea * prox );
//				}
//				
//			}
//		};
		
	}
	
	

	

	//accept two transition series, and return an ordered pair, where the ordering indicates the preferred fitting sequence for best results
	private static Pair<TransitionSeries, TransitionSeries> orderTSPairByScore(
			final TransitionSeries ts1, 
			final TransitionSeries ts2, 
			FittingParameters parameters,
			final Spectrum s)
	{
				
		Pair<TransitionSeries, TransitionSeries> order = new Pair<TransitionSeries, TransitionSeries>();
		
		Float ordering1, ordering2;
		Scorer scorer;
		
		scorer = fScoreTransitionSeries(parameters, s, false);
		scorer.score(ts1);
		ordering1 = scorer.score(ts2);
		
		scorer = fScoreTransitionSeries(parameters, s, false);
		scorer.score(ts2);
		ordering2 = scorer.score(ts1);		
		
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
	
	
	//compare two TransitionSeries -- useful for implementing a Comparator
	private static int compareTSs(
			TransitionSeries ts1, 
			TransitionSeries ts2, 
			FittingParameters parameters,
			final Spectrum s)
	{
		Pair<TransitionSeries, TransitionSeries> orderedPair = orderTSPairByScore(ts1, ts2, parameters, s);
		if (orderedPair.first == ts1) return -1;
		return 1;
	}
	
	
	
	
	
	/**
	 * Generates a list of {@link TransitionSeries} which are good fits for the given data at the given channel index
	 * @return an ordered list of {@link TransitionSeries} which are good fits for the given data at the given channel
	 */
	public static List<TransitionSeries> proposeTransitionSeriesFromChannel(
			final ReadOnlySpectrum data, 
			final FittingSet fits,
			final FittingSet proposed,
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
		FittingResultSet fitResults = fits.fit(data);
		FittingResultSet proposedResults = proposed.fit(fitResults.getResidual());
		
		
		final ReadOnlySpectrum s = proposedResults.getResidual();
		
		if (currentTSisUsed) proposed.addTransitionSeries(currentTS);
		

		final float energy = fits.getFittingParameters().getCalibration().energyFromChannel(channel);	
		

		//get a list of all transition series to start with
		List<TransitionSeries> tss = new ArrayList<>(PeakTable.getAllTransitionSeries());

		
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
		tss = new ArrayList<>(new HashSet<>(tss));
		

		//sort first by how close they are to the channel in quesiton
		tss.sort((ts1, ts2) -> {
			Double prox1, prox2;

			prox1 = Math.abs(ts1.getProximityToEnergy(energy));
			prox2 = Math.abs(ts2.getProximityToEnergy(energy));

			return prox1.compareTo(prox2);
		});
		
		
		//take the top n based on position alone
		tss = tss.subList(0, 15);
		
		
		//TODO: Pulling the calibration from the fits FittingSet seems like a bad plan, and the parameters should somehow live somewhere else?
		
		//now sort by score
		tss = tss.stream()
			.map(ts -> new Pair<>(ts, TSOrdering.fScoreTransitionSeries(fits.getFittingParameters(), s, energy, true).score(ts)))
			.sorted((p1, p2) -> p1.second.compareTo(p2.second))
			.limit(15)
			.map(p -> p.first)
			.collect(Collectors.toList());

		
		//take the best in sorted order based on score
		return tss.subList(0, 6);
	}


	
	
}
