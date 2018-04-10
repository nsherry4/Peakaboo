package peakaboo.controller.plotter.fitting;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.curvefit.fitting.FittingResultSet;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.peaktable.PeakTable;
import peakaboo.curvefit.scoring.EnergyProximityScorer;
import peakaboo.curvefit.scoring.FastFittingScorer;
import peakaboo.curvefit.scoring.Scorer;
import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;


/**
 * This class contains functions to do things like score, suggest, or sort TransitionSeries based on provided data.
 * @author NAS, 2010-2018
 *
 */

public class TSOrdering
{




	
	
	/**
	 * Generates a list of {@link TransitionSeries} which are good fits for the given data at the given channel index
	 * @return an ordered list of {@link TransitionSeries} which are good fits for the given data at the given channel
	 */
	public static List<TransitionSeries> proposeTransitionSeriesFromChannel(
			final ReadOnlySpectrum data, 
			FittingController controller,
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
	
		
		FittingSet fits = controller.getFittingSelections();
		FittingSet proposed = controller.getFittingProposals();
		
		
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
		
	
		Scorer proximityScorer = new EnergyProximityScorer(energy, fits.getFittingParameters().getCalibration());
		Scorer fastfitScorer = new FastFittingScorer(s, fits.getFittingParameters().getCalibration());
		Scorer fastScorer = ts -> {
			//the closer the better, so we accent this
			float p = (float)Math.log1p(proximityScorer.score(ts));
			//Don't reward a better fit too much as the signal fitted grows
			float f = 1+fastfitScorer.score(ts);
			
			float score = p * f;			
			return score;
		};
		
		
		//now sort by score
		tss = tss.stream()
			.map(ts -> new Pair<>(ts, -fastScorer.score(ts)))
			.sorted((p1, p2) -> p1.second.compareTo(p2.second))
			.limit(15)
			.map(p -> p.first)
			.collect(Collectors.toList());

		
		//take the best in sorted order based on score
		return tss;
	}


	
	
}
