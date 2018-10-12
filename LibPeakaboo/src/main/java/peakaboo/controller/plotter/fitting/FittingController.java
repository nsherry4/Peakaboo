package peakaboo.controller.plotter.fitting;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import cyclops.Pair;
import cyclops.ReadOnlySpectrum;
import cyclops.util.Mutable;
import eventful.EventfulType;
import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.OptimizingCurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.curve.fitting.solver.OptimizingFittingSolver;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.fitting.FittingFunction;
import peakaboo.curvefit.peak.search.PeakProposal;
import peakaboo.curvefit.peak.search.searcher.DerivativePeakSearcher;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.mapping.calibration.CalibrationProfile;
import peakaboo.mapping.calibration.CalibrationReference;
import plural.executor.ExecutorSet;


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
	
	
	public void moveTransitionSeries(int from, int to) {
		//we'll be removing the item from the list, so if the 
		//destination is greater than the source, decrement it 
		//to make up the difference
		if (to > from) { to--; }
		
		TransitionSeries ts = fittingModel.selections.getFittedTransitionSeries().get(from);
		fittingModel.selections.remove(ts);
		fittingModel.selections.insertTransitionSeries(to, ts);
		
		setUndoPoint("Move Fitting");
		fittingDataInvalidated();
		
	}
	
	public void addAllTransitionSeries(Collection<TransitionSeries> tss)
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

	public List<TransitionSeries> getUnfittedTransitionSeries()
	{
		final List<TransitionSeries> fitted = getFittedTransitionSeries();
		return PeakTable.SYSTEM.getAll().stream().filter(ts -> (!fitted.contains(ts))).collect(toList());
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
		plot.regenerateCachedData();

		if (fittingModel.selectionResults == null) return 0.0f;

		for (FittingResult result : fittingModel.selectionResults.getFits())
		{
			if (result.getTransitionSeries() == ts) {
				float max = result.getFit().max();
				if (Float.isNaN(max)) max = 0f;
				return max;
			}
		}
		return 0.0f;

	}



	public void moveTransitionSeriesUp(List<TransitionSeries> tss)
	{
		fittingModel.selections.moveTransitionSeriesUp(tss);
		setUndoPoint("Move Fitting Up");
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
		fittingModel.selections.getFittingParameters().setEscapeType(type);
		fittingModel.proposals.getFittingParameters().setEscapeType(type);
		
		fittingDataInvalidated();
		
		setUndoPoint("Escape Peaks");
		updateListeners(false);
	}

	public EscapePeakType getEscapeType()
	{
		return fittingModel.selections.getFittingParameters().getEscapeType();
	}
	
	public List<TransitionSeries> proposeTransitionSeriesFromChannel(final int channel, TransitionSeries currentTS)
	{
		
		if (! plot.data().hasDataSet() ) return null;
				
		return PeakProposal.fromChannel(
				plot.filtering().getFilteredPlot(),
				this.getFittingSelections(),
				this.getFittingProposals(),
				this.getCurveFitter(),
				this.getFittingSolver(),
				channel,
				currentTS,
				6
		).stream().map(p -> p.first).collect(Collectors.toList());
	}

	/**
	 * Given a channel, return the existing FittingResult which makes most sense to
	 * 'select' for that channel, or null if there are no good fits.
	 */
	public TransitionSeries selectTransitionSeriesAtChannel(int channel) {      
        float bestValue = 1f;
        FittingResult bestFit = null;

        if (getFittingSelectionResults() == null) {
            return null;
        }
        
		for (FittingResult fit : getFittingSelectionResults()) {
			if (!fit.getFit().inBounds(channel)) {
				continue;
			}
            float value = fit.getFit().get(channel);
            if (value > bestValue) {
                bestValue = value;
                bestFit = fit;
            }
        }
		if (bestFit == null) {
			return null;
		}
		return bestFit.getTransitionSeries();
	}
	
	public boolean canMap()
	{
		return ! (getVisibleTransitionSeries().size() == 0 || plot.data().getDataSet().getScanData().scanCount() == 0);
	}


	public void setFittingParameters(int scanSize, float min, float max)
	{
		fittingModel.selections.getFittingParameters().setCalibration(min, max, scanSize);
		fittingModel.proposals.getFittingParameters().setCalibration(min, max, scanSize);

		//TODO: Why is this here? Are we just resetting it to be sure they stay in sync?
		fittingModel.selections.getFittingParameters().setEscapeType(getEscapeType());
		fittingModel.proposals.getFittingParameters().setEscapeType(getEscapeType());

		
		setUndoPoint("Calibration");
		plot.filtering().filteredDataInvalidated();
	}

	public void setMaxEnergy(float max) {
		int dataWidth = plot.data().getDataSet().getAnalysis().channelsPerScan();
		setFittingParameters(dataWidth, getMinEnergy(), max);

		updateListeners(false);
	}

	public float getMaxEnergy()
	{
		return fittingModel.selections.getFittingParameters().getCalibration().getMaxEnergy();
	}

	public void setMinEnergy(float min) {
		int dataWidth = plot.data().getDataSet().getAnalysis().channelsPerScan();
		setFittingParameters(dataWidth, min, getMaxEnergy());
		updateListeners(false);
	}

	
	public float getMinEnergy()
	{
		return fittingModel.selections.getFittingParameters().getCalibration().getMinEnergy();
	}
	
	public EnergyCalibration getEnergyCalibration() {
		return fittingModel.selections.getFittingParameters().getCalibration();
	}
	
	
	
	

	public void calculateProposalFittings()
	{
		fittingModel.proposalResults = getFittingSolver().solve(fittingModel.selectionResults.getResidual(), fittingModel.proposals, getCurveFitter());
	}

	public void calculateSelectionFittings(ReadOnlySpectrum data)
	{
		fittingModel.selectionResults = getFittingSolver().solve(data, fittingModel.selections, getCurveFitter());
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

	public FittingSet getFittingProposals()
	{
		return fittingModel.proposals;
	}
	
	public FittingResultSet getFittingProposalResults()
	{
		return fittingModel.proposalResults;
	}

	public FittingResultSet getFittingSelectionResults()
	{
		return fittingModel.selectionResults;
	}

	public List<TransitionSeries> getHighlightedTransitionSeries() {
		return fittingModel.highlighted;
	}
	
	public void setHighlightedTransitionSeries(List<TransitionSeries> highlighted) {
		//If the highlight already matches, don't bother
		if (fittingModel.highlighted != null && fittingModel.highlighted.equals(highlighted)) {
			return;
		}
		fittingModel.highlighted = highlighted;
		updateListeners(false);
	}
	
	public float getFWHMBase() {
		return fittingModel.selections.getFittingParameters().getFWHMBase();
	}
	
	public void setFWHMBase(float base) {
		fittingModel.selections.getFittingParameters().setFWMHBase(base);
		fittingModel.proposals.getFittingParameters().setFWMHBase(base);
		fittingDataInvalidated();
		setUndoPoint("Change Peak Shape");
	}

	public void setFittingFunction(Class<? extends FittingFunction> cls) {
		fittingModel.selections.getFittingParameters().setFittingFunction(cls);
		fittingModel.proposals.getFittingParameters().setFittingFunction(cls);
		fittingDataInvalidated();
		setUndoPoint("Change Peak Shape");
	}
	
	public Class<? extends FittingFunction> getFittingFunction() {
		return fittingModel.selections.getFittingParameters().getFittingFunction();
	}
	
	public CurveFitter getCurveFitter() {
		return fittingModel.curveFitter;
	}
	
	
	public void setCurveFitter(CurveFitter curveFitter) {
		this.fittingModel.curveFitter = curveFitter;
		fittingDataInvalidated();
	}



	public FittingSolver getFittingSolver() {
		return this.fittingModel.fittingSolver;
	}
	
	public void setFittingSolver(FittingSolver fittingSolver) {
		this.fittingModel.fittingSolver = fittingSolver;
		fittingDataInvalidated();
	}

	public ExecutorSet<List<TransitionSeries>> autodetectPeaks() {
		DerivativePeakSearcher searcher = new DerivativePeakSearcher();
		ReadOnlySpectrum data = plot.filtering().getFilteredPlot();
		ExecutorSet<List<TransitionSeries>> exec = PeakProposal.search(
				data, 
				searcher, 
				getFittingSelections(), 
				getCurveFitter(), 
				getFittingSolver()
			);
		

		Mutable<Boolean> ran = new Mutable<>(false);
		exec.addListener(() -> {
			if (!exec.getCompleted()) return;
			if (ran.get()) return;
			ran.set(true);
			for (TransitionSeries ts : exec.getResult()) {
				getFittingSelections().addTransitionSeries(ts);
			}
			fittingDataInvalidated();
		});
		
		
		return exec;

		
	}

	public boolean hasAnnotation(TransitionSeries ts) {
		if (!fittingModel.annotations.containsKey(ts)) {
			return false;
		}
		String annotation = getAnnotation(ts);
		if (annotation == null || annotation.trim().length() == 0) {
			return false;
		}
		return true;
	}
	
	public String getAnnotation(TransitionSeries ts) {
		return fittingModel.annotations.get(ts);
	}
	
	public void setAnnotation(TransitionSeries ts, String annotation) {
		if (annotation.trim().length() == 0) {
			fittingModel.annotations.remove(ts);
		} else {
			fittingModel.annotations.put(ts, annotation);
		}
		updateListeners(false);
	}

	public Map<TransitionSeries, String> getAnnotations() {
		return new HashMap<>(fittingModel.annotations);
	}

	public void clearAnnotations() {
		fittingModel.annotations.clear();
		updateListeners(false);
	}

	public void loadCalibrationReference(CalibrationReference ref) {
		fittingModel.calibrationReference = ref;
		clearTransitionSeries();
		List<TransitionSeries> tss = new ArrayList<>(ref.getConcentrations().keySet());
		tss.sort((a, b) -> a.element.compareTo(b.element));
		
		//CalibrationReferences use blank TransitionSeries so it's not limited by the peaktable data
		//we have to convert here
		//TODO: Should the controller convert all added transitionseries to ones from the PeakTable?
		tss = tss.stream().map(ts -> PeakTable.SYSTEM.get(ts)).filter(ts -> ts != null).collect(Collectors.toList());
		addAllTransitionSeries(tss);
		//TODO: Should we be doing this, or should the user be doing it?
		setFittingSolver(new OptimizingFittingSolver());
		setCurveFitter(new OptimizingCurveFitter());
		updateListeners(false);
	}
	
	public CalibrationReference getCalibrationReference() {
		return fittingModel.calibrationReference;
	}

	public CalibrationProfile generateCalibrationProfile() {
		CalibrationReference reference = getCalibrationReference();
		if (reference == null) {
			return null;
		}
		FittingResultSet sample = getFittingSelectionResults();
		CalibrationProfile profile = new CalibrationProfile(reference, sample);
		return profile;
	}
	
	
	
	
	
}
