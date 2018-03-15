package peakaboo.curvefit.controller;


import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import peakaboo.common.PeakabooLog;
import peakaboo.curvefit.model.EnergyCalibration;
import peakaboo.curvefit.model.FittingResult;
import peakaboo.curvefit.model.FittingResultSet;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesFitting;
import peakaboo.curvefit.peaktable.PeakTable;
import plural.streams.StreamExecutor;
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
	 * @param energyPerChannel the range of energy covered by one data point in a {@link Spectrum}
	 * @param unfitted the list of {@link TransitionSeries} to attempt to order
	 * @param s the data to use to score orderings
	 * @param escape the kind of {@link EscapePeakType} these fittings should use
	 * @return an ordered list of {@link TransitionSeries}
	 */
	public static List<TransitionSeries> optimizeTSOrdering(
			EnergyCalibration calibration,
			List<TransitionSeries> unfitted, 
			Spectrum s, 
			EscapePeakType escape)
	{
		List<TransitionSeries> ordered = new ArrayList<>(unfitted);

		Collections.sort(ordered, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				return compareTSs(ts1, ts2, calibration, s, escape);
			}
		});
		
		return ordered;
	}
	
	
	/**
	 * Creates an anonymous function to score a {@link TransitionSeries}
	 * @param escape the kind of {@link EscapePeakType} the fitting should use
	 * @param energyPerChannel the range of energy covered by one data point in a {@link Spectrum}
	 * @param spectrum the data to use to score this {@link TransitionSeries}
	 * @return a score for this {@link TransitionSeries}
	 */
	public static Function<TransitionSeries, Float> fScoreTransitionSeries(
			EscapePeakType escape, 
			EnergyCalibration calibration,
			final ReadOnlySpectrum spectrum)
	{
		return fScoreTransitionSeries(escape, calibration, spectrum, null, true);
	}
	
	/**
	 * Creates an anonymous function to score a {@link TransitionSeries}
	 * @param escape the kind of {@link EscapePeakType} the fitting should use
	 * @param energyPerChannel the range of energy covered by one data point in a {@link Spectrum}
	 * @param spectrum the data to use to score this {@link TransitionSeries}
	 * @param useBaseSize should {@link TransitionSeries} with larger base sizes (wider) be scored worse
	 * @return a score for this {@link TransitionSeries}
	 */
	public static Function<TransitionSeries, Float> fScoreTransitionSeries(
			EscapePeakType escape, 
			EnergyCalibration calibration,
			final ReadOnlySpectrum spectrum, 
			boolean useBaseSize)
	{
		return fScoreTransitionSeries(escape, calibration, spectrum, null, useBaseSize);
	}
	
	/**
	 * Creates an anonymous function to score a {@link TransitionSeries}
	 * @param escape the kind of {@link EscapePeakType} the fitting should use
	 * @param energyPerChannel the range of energy covered by one data point in a {@link Spectrum}
	 * @param spectrum the data to use to score this {@link TransitionSeries}
	 * @param energy score {@link TransitionSeries} better the closer they are to the given energy value
	 * @param useBaseSize should {@link TransitionSeries} with larger base sizes (wider) be scored worse
	 * @return a score for this {@link TransitionSeries}
	 */
	public static Function<TransitionSeries, Float> fScoreTransitionSeries(
			final EscapePeakType escape, 
			EnergyCalibration calibration,
			final ReadOnlySpectrum spectrum, 
			final Float energy, 
			final boolean useBaseSize
		)
	{
	
		//scoring function to evaluate each TransitionSeries
		return new Function<TransitionSeries, Float>() {

			TransitionSeriesFitting tsf = new TransitionSeriesFitting(null, calibration, escape);
			Spectrum s = new ISpectrum(spectrum);
			
			public Float apply(TransitionSeries ts)
			{
				double prox;
				if (energy == null)
				{
					prox = 1.0;
				} else  {
					prox = ts.getProximityScore(energy, ((double)(calibration.energyPerChannel()))*2d); //Math.abs(ts.getProximityToEnergy(energy));
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
				
				remainingArea = unfit.sum() / s.size();
				
				if (useBaseSize)
				{
					return (float)( remainingArea * tsf.getSizeOfBase() * prox );
				} else {
					return (float)( remainingArea * prox );
				}
				
			}
		};
		
	}
	
	
	
	/**
	 * Return a list of all {@link TransitionSeries} which overlap with the given {@link TransitionSeries}
	 * @param ts the {@link TransitionSeries} with which to check for overlaps
	 * @param tss the other {@link TransitionSeries}, which should be checked for overlaps with ts
	 * @param energyPerChannel the range of energy covered by one data point in a {@link Spectrum}
	 * @param spectrumSize the size of the data to be fitted
	 * @param escape the kind of {@link EscapePeakType} that should
	 * @return a list of all {@link TransitionSeries} which overlap with the given one
	 */
	public static List<TransitionSeries> getTSsOverlappingTS(final TransitionSeries ts, final List<TransitionSeries> tss, EnergyCalibration calibration, final EscapePeakType escape)
	{
		final TransitionSeriesFitting tsf1 = new TransitionSeriesFitting(null, calibration, escape);
		final TransitionSeriesFitting tsf2 = new TransitionSeriesFitting(null, calibration, escape);
		
		//we want the true flag so that we make sure that elements which overlap an escape peak are still considered overlapping
		tsf1.setTransitionSeries(ts, true);
		
		//map all other TSs to booleans to check if this overlaps
		return tss.stream().filter((TransitionSeries otherts) -> {
			if (otherts.equals(ts)) return false;	//its not overlapping if its the same TS
			tsf2.setTransitionSeries(otherts, true);						
			return (tsf1.isOverlapping(tsf2));
		}).collect(Collectors.toList());
	}
	
	

	//accept two transition series, and return an ordered pair, where the ordering indicates the preferred fitting sequence for best results
	private static Pair<TransitionSeries, TransitionSeries> orderTSPairByScore(
			final TransitionSeries ts1, 
			final TransitionSeries ts2, 
			EnergyCalibration calibration,
			final Spectrum s, 
			final EscapePeakType escape)
	{
				
		Pair<TransitionSeries, TransitionSeries> order = new Pair<TransitionSeries, TransitionSeries>();
		
		Float ordering1, ordering2;
		Function<TransitionSeries, Float> scorer;
		
		scorer = fScoreTransitionSeries(escape, calibration, s, false);
		scorer.apply(ts1);
		ordering1 = scorer.apply(ts2);
		
		scorer = fScoreTransitionSeries(escape, calibration, s, false);
		scorer.apply(ts2);
		ordering2 = scorer.apply(ts1);		
		
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
			EnergyCalibration calibration,
			final Spectrum s, 
			final EscapePeakType escape)
	{
		Pair<TransitionSeries, TransitionSeries> orderedPair = orderTSPairByScore(ts1, ts2, calibration, s, escape);
		if (orderedPair.first == ts1) return -1;
		return 1;
	}
	
	
	
	
	
	/**
	 * Generates a list of {@link TransitionSeries} which are good fits for the given data at the given channel index
	 * @param escape the kind of {@link EscapePeakType} to use when finding good matches
	 * @param energyPerChannel the range of energy covered by one data point in a {@link Spectrum}
	 * @param data the data against which {@link TransitionSeries} should be scored
	 * @param fits the current set of fitted {@link TransitionSeries}
	 * @param proposed the current set of proposed {@link TransitionSeries}
	 * @param channel the channel for which the recommendations have been requested
	 * @param currentTS the currently suggested {@link TransitionSeries}. If a previous suggestion was made, it should not be included in the fittings subtracted from the given data, as it will prevent good fittigs from being propsed.
	 * @return an ordered list of {@link TransitionSeries} which are good fits for the given data at the given channel
	 */
	public static List<TransitionSeries> proposeTransitionSeriesFromChannel(
			final EscapePeakType escape,
			EnergyCalibration calibration,
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
		FittingResultSet fitResults = fits.calculateFittings(data);
		FittingResultSet proposedResults = proposed.calculateFittings(fitResults.residual);
		
		
		final ReadOnlySpectrum s = proposedResults.residual;
		
		if (currentTSisUsed) proposed.addTransitionSeries(currentTS);
		

		final float energy = calibration.energyFromChannel(channel);	
		

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
		
		//now sort by score
		tss = tss.stream()
			.map(ts -> new Pair<TransitionSeries, Float>(ts, TSOrdering.fScoreTransitionSeries(escape, calibration, s, energy, true).apply(ts)))
			.sorted((p1, p2) -> p1.second.compareTo(p2.second))
			.limit(15)
			.map(p -> p.first)
			.collect(Collectors.toList());

		
		//take the best in sorted order based on score
		return tss.subList(0, 6);
	}


	
	public static StreamExecutor<Pair<Float, Float>> proposeEnergyLevel(ReadOnlySpectrum spectrum, List<TransitionSeries> tsList, int dataWidth) {
		
		List<Pair<Float, Float>> energies = new ArrayList<>();
		for (float max = 0.05f; max <= 100f; max += 0.05f) {
			for (float min = -10.0f; min < 10.0f; min += 0.05) {
				energies.add(new Pair<Float, Float>(min, max));
			}
		}
		
		StreamExecutor<Pair<Float, Float>> executor = new StreamExecutor<>(energies.size() / 100);
		executor.setTask(energies, stream -> {

			//build a new model for experimenting with
			FittingSet fits = new FittingSet();
			fits.setDataWidth(dataWidth);
			for (TransitionSeries ts : tsList) {
				fits.addTransitionSeries(ts);
			}
			
			//Score each energy value using our observed stream
			List<Float> scores = stream.map(energyPair -> {
				
				EnergyCalibration calibration = new EnergyCalibration(energyPair.first, energyPair.second, dataWidth);
				
				Map<TransitionSeries, Float> heights = fits.roughIndivudualHeights(spectrum, calibration);
				float score = 0;
				for (Float f : heights.values()) {
					score += Math.sqrt(f);
				}
				return score;
				
			}).collect(Collectors.toList());
		
			if (executor.getState() == StreamExecutor.State.ABORTED) {
				return null;
			}
			
			//Find the best score, and its energy
			float bestScore = 0f;
			float bestMax = 0f;
			float bestMin = 0f;
			
			for (int i = 0; i < energies.size(); i++) {
				Pair<Float, Float> energy = energies.get(i);
				float score = scores.get(i);
				if (score > bestScore) {
					bestScore = score;
					bestMin = energy.first;
					bestMax = energy.second;
				}
			}
			
			PeakabooLog.get().log(Level.INFO, "Proposing Energy Level Stage 1: bestMin=" + bestMin + ", bestMax=" + bestMax);
			
			float window = 0.1f;
			Pair<Float, Float> fineTuned = proposeEnergyFineTuning(spectrum, tsList, dataWidth, bestMin-window, bestMin+window, bestMax-window, bestMax+window);
			
			PeakabooLog.get().log(Level.INFO, "Proposing Energy Level Stage 2: bestMin=" + fineTuned.first+ ", bestMax=" + fineTuned.second);
			
			return fineTuned;
			
			
		});


		return executor;
	}
	
	public static Pair<Float, Float> proposeEnergyFineTuning(ReadOnlySpectrum spectrum, List<TransitionSeries> tsList, int dataWidth, float lomin, float himin, float lomax, float himax) {
		
		//build a new model for experimenting with
		FittingSet fits = new FittingSet();
		fits.setDataWidth(dataWidth);
		for (TransitionSeries ts : tsList) {
			fits.addTransitionSeries(ts);
		}
		
		//Find the best score, and its energy
		float bestScore = 0f;
		float bestMin = lomin;
		float bestMax = himax;
		
		for (float min = lomin; min <= himin; min += 0.01f) {
			for (float max = lomax; max <= himax; max += 0.01f) {
				if (max <= min) continue;
				
				fits.setEnergy(min, max);
				FittingResultSet results = fits.calculateFittingsUnsynchronized(spectrum);
				
				float score = 0f;
				for (FittingResult fit : results.fits) {
					score += Math.sqrt(fit.fit.sum());
				}
				
				if (score > bestScore) {
					bestScore = score;
					bestMin = min;
					bestMax = max;
				}
			}
		}
		
		return new Pair<Float, Float>(bestMin, bestMax);
	}
	
}
