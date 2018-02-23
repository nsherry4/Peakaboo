package peakaboo.controller.plotter.fitting;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import eventful.EventfulType;
import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.controller.TSOrdering;
import peakaboo.curvefit.model.FittingModel;
import peakaboo.curvefit.model.FittingResult;
import peakaboo.curvefit.model.FittingResultSet;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesType;
import peakaboo.curvefit.peaktable.PeakTable;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;
import scitypes.SpectrumCalculations;


public class FittingController extends EventfulType<Boolean>
{

	FittingModel fittingModel;
	PlotController plot;
	
	public FittingController(PlotController plotController)
	{
		this.plot = plotController;
		fittingModel = new FittingModel();
	}
	
	public FittingModel getFittingModel()
	{
		return fittingModel;
	}
	
	private void setUndoPoint(String change)
	{
		plot.history().setUndoPoint(change);
	}
	
	
	public void addTransitionSeries(TransitionSeries e)
	{
		if (e == null) return;
		fittingModel.selections.addTransitionSeries(e);
		setUndoPoint("Add Fitting");
		fittingDataInvalidated();
	}

	public void addAllTransitionSeries(List<TransitionSeries> tss)
	{
		for (TransitionSeries ts : tss)
		{
			fittingModel.selections.addTransitionSeries(ts);
		}
		setUndoPoint("Add Fittings");
		fittingDataInvalidated();
	}

	public void clearTransitionSeries()
	{
		
		fittingModel.selections.clear();
		setUndoPoint("Clear Fittings");
		fittingDataInvalidated();
	}

	public void removeTransitionSeries(TransitionSeries e)
	{
		
		fittingModel.selections.remove(e);
		setUndoPoint("Remove Fitting");
		fittingDataInvalidated();
	}

	public List<TransitionSeries> getFittedTransitionSeries()
	{
		return fittingModel.selections.getFittedTransitionSeries();
	}

	public List<TransitionSeries> getUnfittedTransitionSeries(final TransitionSeriesType tst)
	{
		final List<TransitionSeries> fitted = getFittedTransitionSeries();
		return PeakTable.getAllTransitionSeries().stream().filter(ts -> (!fitted.contains(ts)) && tst.equals(ts.type)).collect(toList());
	}

	public void setTransitionSeriesVisibility(TransitionSeries e, boolean show)
	{
		fittingModel.selections.setTransitionSeriesVisibility(e, show);
		setUndoPoint("Fitting Visiblitiy");
		fittingDataInvalidated();
	}

	public boolean getTransitionSeriesVisibility(TransitionSeries e)
	{
		return e.visible;
	}

	public List<TransitionSeries> getVisibleTransitionSeries()
	{
		return getFittedTransitionSeries().stream().filter(ts -> ts.visible).collect(toList());
	}

	public float getTransitionSeriesIntensity(TransitionSeries ts)
	{
		plot.regenerateCahcedData();

		if (fittingModel.selectionResults == null) return 0.0f;

		for (FittingResult result : fittingModel.selectionResults.fits)
		{
			if (result.transitionSeries == ts) {
				float max = result.fit.max();
				if (Float.isNaN(max)) max = 0f;
				return max;
			}
		}
		return 0.0f;

	}

	public void moveTransitionSeriesUp(TransitionSeries e)
	{
		fittingModel.selections.moveTransitionSeriesUp(e);
		setUndoPoint("Move Fitting Up");
		fittingDataInvalidated();
	}

	public void moveTransitionSeriesUp(List<TransitionSeries> tss)
	{
		fittingModel.selections.moveTransitionSeriesUp(tss);
		setUndoPoint("Move Fitting Up");
		fittingDataInvalidated();
	}
	
	public void moveTransitionSeriesDown(TransitionSeries e)
	{
		fittingModel.selections.moveTransitionSeriesDown(e);
		setUndoPoint("Move Fitting Down");
		fittingDataInvalidated();
	}

	public void moveTransitionSeriesDown(List<TransitionSeries> tss)
	{
		fittingModel.selections.moveTransitionSeriesDown(tss);
		setUndoPoint("Move Fitting Down");
		fittingDataInvalidated();
	}

	public void fittingDataInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		fittingModel.selectionResults = null;

		// this will call update listener for us
		fittingProposalsInvalidated();

	}

	
	public void addProposedTransitionSeries(TransitionSeries e)
	{
		fittingModel.proposals.addTransitionSeries(e);
		fittingProposalsInvalidated();
	}

	public void removeProposedTransitionSeries(TransitionSeries e)
	{
		fittingModel.proposals.remove(e);
		fittingProposalsInvalidated();
	}

	public void clearProposedTransitionSeries()
	{
		fittingModel.proposals.clear();
		fittingProposalsInvalidated();
	}

	public List<TransitionSeries> getProposedTransitionSeries()
	{
		return fittingModel.proposals.getFittedTransitionSeries();
	}

	public void commitProposedTransitionSeries()
	{
		addAllTransitionSeries(fittingModel.proposals.getFittedTransitionSeries());
		fittingModel.proposals.clear();
		fittingDataInvalidated();
	}

	public void fittingProposalsInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		fittingModel.proposalResults = null;
		updateListeners(false);
	}

	public void setEscapeType(EscapePeakType type)
	{
		fittingModel.selections.setEscapeType(type);
		fittingModel.proposals.setEscapeType(type);
		
		fittingDataInvalidated();
		
		setUndoPoint("Escape Peaks");
		updateListeners(false);
	}

	public EscapePeakType getEscapeType()
	{
		return plot.settings().getEscapePeakType();
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

	public boolean canMap()
	{
		return ! (getFittedTransitionSeries().size() == 0 || plot.data().getDataSet().getScanData().scanCount() == 0);
	}


	public void setFittingParameters(float energyPerChannel)
	{

		int scanSize = 0;
		
		plot.getDR().unitSize = energyPerChannel;
		fittingModel.selections.setDataParameters(scanSize, energyPerChannel, plot.settings().getEscapePeakType());
		fittingModel.proposals.setDataParameters(scanSize, energyPerChannel, plot.settings().getEscapePeakType());

		setUndoPoint("Calibration");
		plot.filtering().filteredDataInvalidated();
	}

	

	public void calculateProposalFittings()
	{
		fittingModel.proposalResults = fittingModel.proposals.calculateFittings(fittingModel.selectionResults.residual);
	}

	public void calculateSelectionFittings(ReadOnlySpectrum data)
	{
		fittingModel.selectionResults = fittingModel.selections.calculateFittings(data);
	}

	public boolean hasProposalFitting()
	{
		return fittingModel.proposalResults != null;
	}

	public boolean hasSelectionFitting()
	{
		return fittingModel.selectionResults != null;
	}

	public FittingSet getFittingSelections()
	{
		return fittingModel.selections;
	}

	public FittingResultSet getFittingProposalResults()
	{
		return fittingModel.proposalResults;
	}

	public FittingResultSet getFittingSelectionResults()
	{
		return fittingModel.selectionResults;
	}
	
	
}
