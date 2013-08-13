package peakaboo.controller.plotter.fitting;

import static fava.Fn.filter;

import java.util.Comparator;
import java.util.List;

import peakaboo.controller.plotter.IPlotController;
import peakaboo.curvefit.controller.IFittingController;
import peakaboo.curvefit.controller.TSOrdering;
import peakaboo.curvefit.model.FittingModel;
import peakaboo.curvefit.model.FittingResult;
import peakaboo.curvefit.model.FittingResultSet;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesType;
import peakaboo.curvefit.peaktable.PeakTable;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;
import eventful.EventfulType;
import fava.Fn;
import fava.Functions;
import fava.datatypes.Pair;
import fava.functionable.FList;
import fava.signatures.FnCondition;
import fava.signatures.FnFold;
import fava.signatures.FnMap;


public class FittingController extends EventfulType<Boolean> implements IFittingController
{

	FittingModel fittingModel;
	IPlotController plot;
	
	public FittingController(IPlotController plotController)
	{
		this.plot = plotController;
		fittingModel = new FittingModel();
	}
	
	@Override
	public FittingModel getFittingModel()
	{
		return fittingModel;
	}
	
	private void setUndoPoint(String change)
	{
		plot.history().setUndoPoint(change);
	}
	
	
	@Override
	public void addTransitionSeries(TransitionSeries e)
	{
		if (e == null) return;
		fittingModel.selections.addTransitionSeries(e);
		setUndoPoint("Add Fitting");
		fittingDataInvalidated();
	}

	@Override
	public void addAllTransitionSeries(List<TransitionSeries> tss)
	{
		for (TransitionSeries ts : tss)
		{
			fittingModel.selections.addTransitionSeries(ts);
		}
		setUndoPoint("Add Fittings");
		fittingDataInvalidated();
	}

	@Override
	public void clearTransitionSeries()
	{
		
		fittingModel.selections.clear();
		setUndoPoint("Clear Fittings");
		fittingDataInvalidated();
	}

	@Override
	public void removeTransitionSeries(TransitionSeries e)
	{
		
		fittingModel.selections.remove(e);
		setUndoPoint("Remove Fitting");
		fittingDataInvalidated();
	}

	@Override
	public FList<TransitionSeries> getFittedTransitionSeries()
	{
		return FList.wrap(fittingModel.selections.getFittedTransitionSeries());
	}

	@Override
	public FList<TransitionSeries> getUnfittedTransitionSeries(final TransitionSeriesType tst)
	{

		final List<TransitionSeries> fitted = getFittedTransitionSeries();


		return filter(PeakTable.getAllTransitionSeries(), new FnCondition<TransitionSeries>() {


			public Boolean f(TransitionSeries ts)
			{
				return (!fitted.contains(ts)) && tst.equals(ts.type);
			}
		});

	}

	@Override
	public void setTransitionSeriesVisibility(TransitionSeries e, boolean show)
	{
		fittingModel.selections.setTransitionSeriesVisibility(e, show);
		setUndoPoint("Fitting Visiblitiy");
		fittingDataInvalidated();
	}

	@Override
	public boolean getTransitionSeriesVisibility(TransitionSeries e)
	{
		return e.visible;
	}

	@Override
	public FList<TransitionSeries> getVisibleTransitionSeries()
	{

		return filter(getFittedTransitionSeries(), new FnCondition<TransitionSeries>() {


			public Boolean f(TransitionSeries ts)
			{
				return ts.visible;
			}
		});

	}

	@Override
	public float getTransitionSeriesIntensity(TransitionSeries ts)
	{
		plot.regenerateCahcedData();

		if (fittingModel.selectionResults == null) return 0.0f;

		for (FittingResult result : fittingModel.selectionResults.fits)
		{
			if (result.transitionSeries == ts) {
				float max = SpectrumCalculations.max(result.fit);
				if (Float.isNaN(max)) max = 0f;
				return max;
			}
		}
		return 0.0f;

	}

	@Override
	public void moveTransitionSeriesUp(TransitionSeries e)
	{
		fittingModel.selections.moveTransitionSeriesUp(e);
		setUndoPoint("Move Fitting Up");
		fittingDataInvalidated();
	}

	@Override
	public void moveTransitionSeriesUp(List<TransitionSeries> tss)
	{
		fittingModel.selections.moveTransitionSeriesUp(tss);
		setUndoPoint("Move Fitting Up");
		fittingDataInvalidated();
	}
	
	@Override
	public void moveTransitionSeriesDown(TransitionSeries e)
	{
		fittingModel.selections.moveTransitionSeriesDown(e);
		setUndoPoint("Move Fitting Down");
		fittingDataInvalidated();
	}

	@Override
	public void moveTransitionSeriesDown(List<TransitionSeries> tss)
	{
		fittingModel.selections.moveTransitionSeriesDown(tss);
		setUndoPoint("Move Fitting Down");
		fittingDataInvalidated();
	}

	@Override
	public void fittingDataInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		fittingModel.selectionResults = null;

		// this will call update listener for us
		fittingProposalsInvalidated();

	}

	
	@Override
	public void addProposedTransitionSeries(TransitionSeries e)
	{
		fittingModel.proposals.addTransitionSeries(e);
		fittingProposalsInvalidated();
	}

	@Override
	public void removeProposedTransitionSeries(TransitionSeries e)
	{
		fittingModel.proposals.remove(e);
		fittingProposalsInvalidated();
	}

	@Override
	public void clearProposedTransitionSeries()
	{
		fittingModel.proposals.clear();
		fittingProposalsInvalidated();
	}

	@Override
	public List<TransitionSeries> getProposedTransitionSeries()
	{
		return fittingModel.proposals.getFittedTransitionSeries();
	}

	@Override
	public void commitProposedTransitionSeries()
	{
		addAllTransitionSeries(fittingModel.proposals.getFittedTransitionSeries());
		fittingModel.proposals.clear();
		fittingDataInvalidated();
	}

	@Override
	public void fittingProposalsInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		fittingModel.proposalResults = null;
		updateListeners(false);
	}

	@Override
	public void setEscapeType(EscapePeakType type)
	{
		fittingModel.selections.setEscapeType(type);
		fittingModel.proposals.setEscapeType(type);
		
		fittingDataInvalidated();
		
		setUndoPoint("Escape Peaks");
		updateListeners(false);
	}

	@Override
	public EscapePeakType getEscapeType()
	{
		return plot.settings().getEscapePeakType();
	}

	@Override
	public void optimizeTransitionSeriesOrdering()
	{
		
		
		
		//all visible TSs
		final FList<TransitionSeries> tss = Fn.map(getVisibleTransitionSeries(), Functions.<TransitionSeries>id());
				
		//all invisible TSs
		FList<TransitionSeries> invisibles = Fn.filter(getFittedTransitionSeries(), new FnCondition<TransitionSeries>() {

			public Boolean f(TransitionSeries element)
			{
				return ! tss.include(element);
			}
		});
				
		
		//find all the TSs which overlap with other TSs
		final FList<TransitionSeries> overlappers = tss.filter(new FnCondition<TransitionSeries>() {

			public Boolean f(final TransitionSeries ts)
			{
				
				return TSOrdering.getTSsOverlappingTS(
						ts, 
						tss, 
						plot.settings().getEnergyPerChannel(), 
						plot.data().getDataWidth(),
						plot.settings().getEscapePeakType()
					).size() != 0;			
				
			}
		});
		
		
		//then get all the TSs which don't overlap
		FList<TransitionSeries> nonOverlappers = tss.filter(new FnCondition<TransitionSeries>() {

			public Boolean f(TransitionSeries element)
			{
				return ! overlappers.include(element);
			}
		});
	
		

		//score each of the overlappers w/o competition
		FList<Pair<TransitionSeries, Float>> scoredOverlappers = overlappers.map(new FnMap<TransitionSeries, Pair<TransitionSeries, Float>>() {

			public Pair<TransitionSeries, Float> f(TransitionSeries ts)
			{
				return new Pair<TransitionSeries, Float>(
						ts, 
						TSOrdering.fScoreTransitionSeries(
								plot.settings().getEscapePeakType(), 
								plot.settings().getEnergyPerChannel(), 
								plot.filtering().getFilteredPlot()
							).f(ts)
					);
			}
		});
		
		//sort all the overlappig visible elements according to how strongly they would fit on their own (ie no competition)
		Fn.sortBy(scoredOverlappers, new Comparator<Float>() {
			
			public int compare(Float f1, Float f2)
			{
				return (f2.compareTo(f1));
				
			}
		}, Functions.<TransitionSeries, Float>second());
		
		
		
		//find the optimal ordering of the visible overlapping TSs based on how they fit with competition
		FList<TransitionSeries> bestfit = optimizeTSOrderingHelper(scoredOverlappers.map(Functions.<TransitionSeries, Float>first()), new FList<TransitionSeries>());
		

		
		//FList<TransitionSeries> bestfit = TSOrdering.optimizeTSOrdering(getEnergyPerChannel(), tss, filteringController.getFilteredPlot());

		//re-add all of the overlappers
		bestfit.addAll(nonOverlappers);
		
		//re-add all of the invisible TSs
		bestfit.addAll(invisibles);
		
		
		//set the TS selection for the model to be the ordering we have just calculated
		clearTransitionSeries();
		addAllTransitionSeries(bestfit);
		setUndoPoint("Fitting Ordering");
		updateListeners(false);
		
	}
		
	public List<TransitionSeries> proposeTransitionSeriesFromChannel(final int channel, TransitionSeries currentTS)
	{
		
		if (! plot.data().hasDataSet() ) return null;
		
		return TSOrdering.proposeTransitionSeriesFromChannel(
				plot.settings().getEscapePeakType(),
				plot.settings().getEnergyPerChannel(),
				plot.filtering().getFilteredPlot(),
				fittingModel.selections,
				fittingModel.proposals,
				channel,
				currentTS	
		);
	}

	@Override
	public boolean canMap()
	{
		return ! (getFittedTransitionSeries().size() == 0 || plot.data().size() == 0);
	}

	// =============================================
	// Helper Functions for IFittingController
	// =============================================
	private FList<TransitionSeries> optimizeTSOrderingHelper(FList<TransitionSeries> unfitted, FList<TransitionSeries> fitted)
	{
		
		//assumption: unfitted will be in sorted order based on how well each TS fits independently
		if (unfitted.size() == 0) return fitted;
		
		int n = 4;
		
		FList<TransitionSeries> topn = unfitted.take(n);
		unfitted.removeAll(topn);
		FList<List<TransitionSeries>> perms = Fn.permutations(topn);
				
		//function to score an ordering of Transition Series
		final FnMap<List<TransitionSeries>, Float> scoreTSs = new FnMap<List<TransitionSeries>, Float>() {

			public Float f(List<TransitionSeries> tss)
			{
				
				final FnMap<TransitionSeries, Float> scoreTS = TSOrdering.fScoreTransitionSeries(
						plot.settings().getEscapePeakType(), 
						plot.settings().getEnergyPerChannel(), 
						plot.filtering().getFilteredPlot()
					);
				
				Float score = 0f;
				for (TransitionSeries ts : tss)
				{
					score = scoreTS.f(ts);
				}
				return score;
				

			}
		};
	
		
		//find the best fitting for the currently selected fittings
		FList<TransitionSeries> bestfit = FList.<TransitionSeries>wrap(perms.fold(new FnFold<List<TransitionSeries>, List<TransitionSeries>>() {
			
			public List<TransitionSeries> f(List<TransitionSeries> l1, List<TransitionSeries> l2)
			{
				Float s1, s2; //scores
				s1 = scoreTSs.f(l1);
				s2 = scoreTSs.f(l2);				
				
				if (s1 < s2) return l1;
				return l2;
				
			}
		}));

		
		//add the best half of the fitted elements to the fititngs list
		//and the rest back into the start of the unfitted elements list
		fitted.addAll(bestfit.take(n/2));
		bestfit.removeAll(fitted);
		unfitted.addAll(0, bestfit);
		
		
		//recurse
		return optimizeTSOrderingHelper(unfitted, fitted);

				
	}

	@Override
	public void setFittingParameters(float energyPerChannel)
	{

		int scanSize = 0;
		
		plot.getDR().unitSize = energyPerChannel;
		fittingModel.selections.setDataParameters(scanSize, energyPerChannel, plot.settings().getEscapePeakType());
		fittingModel.proposals.setDataParameters(scanSize, energyPerChannel, plot.settings().getEscapePeakType());

		setUndoPoint("Calibration");
		plot.filtering().filteredDataInvalidated();
	}

	

	@Override
	public void calculateProposalFittings()
	{
		fittingModel.proposalResults = fittingModel.proposals.calculateFittings(fittingModel.selectionResults.residual);
	}

	@Override
	public void calculateSelectionFittings(Spectrum data)
	{
		fittingModel.selectionResults = fittingModel.selections.calculateFittings(data);
	}

	@Override
	public boolean hasProposalFitting()
	{
		return fittingModel.proposalResults != null;
	}

	@Override
	public boolean hasSelectionFitting()
	{
		return fittingModel.selectionResults != null;
	}

	@Override
	public FittingSet getFittingSelections()
	{
		return fittingModel.selections;
	}

	@Override
	public FittingResultSet getFittingProposalResults()
	{
		return fittingModel.proposalResults;
	}

	@Override
	public FittingResultSet getFittingSelectionResults()
	{
		return fittingModel.selectionResults;
	}
	
	
}
