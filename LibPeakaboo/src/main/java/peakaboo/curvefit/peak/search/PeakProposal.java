package peakaboo.curvefit.peak.search;


import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import peakaboo.common.PeakabooLog;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.OptimizingCurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.peak.escape.EscapePeak;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.search.scoring.CompoundFittingScorer;
import peakaboo.curvefit.peak.search.scoring.CurveFittingScorer;
import peakaboo.curvefit.peak.search.scoring.EnergyProximityScorer;
import peakaboo.curvefit.peak.search.scoring.FastFittingScorer;
import peakaboo.curvefit.peak.search.scoring.NoComplexPileupScorer;
import peakaboo.curvefit.peak.search.scoring.PileupSourceScorer;
import peakaboo.curvefit.peak.search.searcher.PeakSearcher;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;


/**
 * This class contains functions to do things like score, suggest, or sort TransitionSeries based on provided data.
 * @author NAS, 2010-2018
 *
 */

public class PeakProposal
{


	
	public static ExecutorSet<List<TransitionSeries>> search(
			final ReadOnlySpectrum data,
			PeakSearcher searcher,
			FittingSet fits,
			CurveFitter fitter,
			FittingSolver solver
		) {
		
		
		DummyExecutor firstStage = new DummyExecutor(true);
		DummyExecutor secondStage = new DummyExecutor();
		
		
		ExecutorSet<List<TransitionSeries>> exec = new ExecutorSet<List<TransitionSeries>>("Automatic Peak Fitting") {

			float firstGuessScore = 0;
			
			@Override
			protected List<TransitionSeries> execute() {
				
				firstStage.advanceState();
				
				//FIRST STAGE
				EnergyCalibration calibration = fits.getFittingParameters().getCalibration();
				
				//Proposals fitting set to store proposals in with same parameters
				FittingSet proposals = new FittingSet(fits.getFittingParameters());
				
				
				//Generate list of peaks
				List<Integer> peaks = searcher.search(data);
				
				//remove any peaks within the FWHM of an existing Transitions in fits
				for (int peak : new ArrayList<>(peaks)) {
					if (peakOverlap(fits.getFittedTransitionSeries(), peak, fits.getFittingParameters())) {
						peaks.remove(new Integer(peak));
					}
				}
				
				if (this.isAbortRequested()) {
					this.aborted();
					return null;
				}
				
				
				firstStage.advanceState();
				
				
				//SECOND STAGE

				//Generate lists of guesses for all peaks
				secondStage.advanceState();
				secondStage.setWorkUnits(peaks.size());
				
								
				/*
				 * Go peak by peak from strongest to weakest.
				 * Take the best guess for that peak.
				 * Find other peaks which also have that guess as part of their list of guesses
				 * Remove those other peaks from future consideration 
				 */
				List<TransitionSeries> newFits = new ArrayList<>();
				for (int channel : peaks) {
					
					//no fitting below 1keV
					if (calibration.energyFromChannel(channel) < 1f) {
						continue;
					}
					
					List<Pair<TransitionSeries, Float>> guesses = fromChannel(data, fits, proposals, fitter, solver, channel, null, 5);
					
					PeakabooLog.get().log(Level.FINE, "Examining Channel " + channel);
					
					
					//if this list of guesses contains a TransitionSeries we've already proposed
					//we assume that this peak is also caused by that TransitionSeries and skip it
					if (peakOverlap(newFits, channel, fits.getFittingParameters())) {
						PeakabooLog.get().log(Level.FINE, "Guesses contains previously proposed TransitionSeries, skipping");
						continue;
					}
					
					if (this.isAbortRequested()) {
						this.aborted();
						return null;
					}
					

					TransitionSeries guess = guesses.get(0).first;
					float guessScore = guesses.get(0).second;
					if (firstGuessScore == 0) {
						firstGuessScore = guessScore;
					}

					if (guessScore < firstGuessScore / 25f) {
						continue;
					}
										
					//If the existing fits doesn't contain this, add it
					if (!fits.getFittedTransitionSeries().contains(guess)) {
						newFits.add(guess);
						proposals.addTransitionSeries(guess);
						PeakabooLog.get().log(Level.FINE, "Channel " + channel + " guess: " + guess);
					}
				
					PeakabooLog.get().log(Level.FINE, "----------------------------");
					
					secondStage.workUnitCompleted();
				}
				
				secondStage.advanceState();
								
				return newFits;
			}
		}; 
		
		
		exec.addExecutor(firstStage, "Finding Peaks");
		exec.addExecutor(secondStage, "Identifying Fittings");

		return exec;
		

		
	}
	

	//given the energy level of a peak and a list of existing new fits, check to 
	//see if the given peak can be explained by an existing fit
	private static boolean peakOverlap(List<TransitionSeries> newfits, int channel, FittingParameters parameters) {
		float energy = parameters.getCalibration().energyFromChannel(channel);
		for (TransitionSeries ts : newfits) {
			for (Transition t : ts) {
				if (transitionOverlap(t, energy, 0.1f, parameters)) return true;
			}
			for (Transition t : ts.escape(parameters.getEscapeType())) {
				if (transitionOverlap(t, energy, 0.1f * EscapePeak.intensity(ts.element), parameters)) return true;
			}
		}
		return false;
	}
	private static boolean transitionOverlap(Transition t, float energy, float cutoff, FittingParameters parameters) {
		if (t.relativeIntensity < cutoff) return false; 
		float hwhm = parameters.getFWHM(t)/2f;
		float min = t.energyValue - hwhm;
		float max = t.energyValue + hwhm;
		if (min < energy && energy < max) {
			return true;
		}
		return false;
	}

	
	/**
	 * Generates a list of {@link TransitionSeries} which are good fits for the given data at the given channel index
	 * @return an ordered list of {@link TransitionSeries} which are good fits for the given data at the given channel
	 */
	public static List<Pair<TransitionSeries, Float>> fromChannel(
			final ReadOnlySpectrum data, 
			FittingSet fits,
			FittingSet proposed,
			CurveFitter fitter,
			FittingSolver solver,
			final int channel, 
			TransitionSeries currentTS,
			int guessCount
		) {
		
		
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
	
		EnergyCalibration calibration = fits.getFittingParameters().getCalibration();
		
		//remove the current transitionseries from the list of proposed trantision series so we can re-suggest it.
		//otherwise, the copy getting fitted eats all the signal from the one we would suggest during scoring
		boolean currentTSisUsed = currentTS != null && proposed.getFittedTransitionSeries().contains(currentTS);
		if (currentTSisUsed) proposed.remove(currentTS);
		
		//recalculate
		FittingResultSet fitResults = solver.solve(data, fits, fitter);
		FittingResultSet proposedResults = solver.solve(fitResults.getResidual(), proposed, fitter);
		
		
		final ReadOnlySpectrum residualSpectrum = proposedResults.getResidual();
		
		if (currentTSisUsed) proposed.addTransitionSeries(currentTS);
		

		final float energy = calibration.energyFromChannel(channel);	
		

		//get a list of all transition series to start with
		List<TransitionSeries> tss = new ArrayList<>(PeakTable.SYSTEM.getAll());

		
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
		
	
		
		CompoundFittingScorer fastCompoundScorer = new CompoundFittingScorer();
		fastCompoundScorer.add(new EnergyProximityScorer(energy, fits.getFittingParameters()), 10f);
		fastCompoundScorer.add(new FastFittingScorer(energy, residualSpectrum, fits.getFittingParameters()), 10f);
		fastCompoundScorer.add(new NoComplexPileupScorer(), 2f);
		fastCompoundScorer.add(new PileupSourceScorer(data, calibration), 1f);

		
		//Good scorer also adds a very slow curve fitting scorer to make sure that we actually evaluate the curve at some point
		CompoundFittingScorer goodCompoundScorer = new CompoundFittingScorer();	
		goodCompoundScorer.add(fastCompoundScorer, 23f);
		goodCompoundScorer.add(new CurveFittingScorer(residualSpectrum, fits.getFittingParameters(), fitter), 10f);

		
		
		//now sort by score
		List<Pair<TransitionSeries, Float>> scoredGuesses = tss.stream()
			//fast scorer to shrink downthe list
			.map(ts -> new Pair<>(ts, fastCompoundScorer.score(ts)))
			.sorted((p1, p2) -> p2.second.compareTo(p1.second))
			.limit(guessCount)
			.map(p -> p.first)
			
			//good scorer to put them in the best order
			.map(ts -> new Pair<>(ts, goodCompoundScorer.score(ts)))
			.sorted((p1, p2) -> p2.second.compareTo(p1.second))
			//.map(p -> p.first)
			
			.collect(Collectors.toList());

		
		//take the best in sorted order based on score
		return scoredGuesses;
	}


	
	
}
