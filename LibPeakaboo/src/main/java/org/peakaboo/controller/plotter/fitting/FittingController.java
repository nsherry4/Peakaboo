package org.peakaboo.controller.plotter.fitting;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.session.v2.SavedFittings;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterPlugin;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.search.PeakProposal;
import org.peakaboo.curvefit.peak.search.searcher.DoubleDerivativePeakSearcher;
import org.peakaboo.curvefit.peak.search.searcher.PeakSearcher;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.plot.PlotData;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;
import org.peakaboo.framework.plural.executor.ExecutorSet;


public class FittingController extends EventfulType<Boolean>
{

	FittingModel fittingModel;
	PlotController plot;
	
	
	public FittingController(PlotController plotController)
	{
		this.plot = plotController;
		fittingModel = new FittingModel();
		
		
		fittingModel.selectionResults = new EventfulNullableCache<>(() -> {
			ReadOnlySpectrum data = plot.filtering().getFilteredPlot();
			if (data == null) {
				return null;
			}
			return getFittingSolver().solve(data, fittingModel.selections, getCurveFitter());
		});
		
		fittingModel.proposalResults = new EventfulNullableCache<>(() -> {
			if (plot.currentScan() == null) {
				return null;
			}
			return getFittingSolver().solve(getFittingSelectionResults().getResidual(), fittingModel.proposals, getCurveFitter());
		});
		
		fittingModel.selectionResults.dependsOn(plot.filtering().getFilteredPlotCache());
		fittingModel.proposalResults.dependsOn(fittingModel.selectionResults);
		
		fittingModel.proposalResults.addListener(() -> updateListeners(false));
		
		
	}
	
	public FittingModel getFittingModel()
	{
		return fittingModel;
	}
	
	
	private void setUndoPoint(String change) {
		this.setUndoPoint(change, true);
	}
	
	private void setUndoPoint(String change, boolean distinctChange)
	{
		plot.history().setUndoPoint(change, distinctChange);
	}
	
	
	public void addTransitionSeries(ITransitionSeries e)
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
		
		ITransitionSeries ts = fittingModel.selections.getFittedTransitionSeries().get(from);
		fittingModel.selections.remove(ts);
		fittingModel.selections.insertTransitionSeries(to, ts);
		
		setUndoPoint("Move Fitting");
		fittingDataInvalidated();
		
	}
	
	public void addAllTransitionSeries(Collection<ITransitionSeries> tss)
	{
		for (ITransitionSeries ts : tss)
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

	public void removeTransitionSeries(ITransitionSeries e)
	{
		
		fittingModel.selections.remove(e);
		setUndoPoint("Remove Fitting");
		fittingDataInvalidated();
	}

	public List<ITransitionSeries> getFittedTransitionSeries()
	{
		return fittingModel.selections.getFittedTransitionSeries();
	}

	public List<ITransitionSeries> getUnfittedTransitionSeries()
	{
		final List<ITransitionSeries> fitted = getFittedTransitionSeries();
		return PeakTable.SYSTEM.getAll().stream().filter(ts -> (!fitted.contains(ts))).collect(toList());
	}
	
	public void setTransitionSeriesVisibility(ITransitionSeries e, boolean show)
	{
		fittingModel.selections.setTransitionSeriesVisibility(e, show);
		setUndoPoint("Fitting Visiblitiy");
		fittingDataInvalidated();
	}
	
	public void setAllTransitionSeriesVisibility(boolean show) {
		fittingModel.selections.setAllTransitionSeriesVisibility(show);
		setUndoPoint("Fittings Visiblitiy");
		fittingDataInvalidated();
	}

	public boolean getTransitionSeriesVisibility(ITransitionSeries e)
	{
		return e.isVisible();
	}

	public List<ITransitionSeries> getVisibleTransitionSeries()
	{
		return getFittedTransitionSeries().stream().filter(ITransitionSeries::isVisible).collect(toList());
	}

	public float getTransitionSeriesIntensity(ITransitionSeries ts)
	{
		FittingResultView result = getFittingResultForTransitionSeries(ts);
		if (result == null) {
			return 0f;
		}

		float max = result.getFitMax();
		if (Float.isNaN(max)) max = 0f;
		return max;
		
	}


	public void fittingDataInvalidated()
	{
		
		PeakabooLog.get().log(Level.FINE, "Fitting Data Invalidated");
		
		// Clear cached values, since they now have to be recalculated
		fittingModel.selectionResults.invalidate();

		// this will call update listener for us
		fittingProposalsInvalidated();

	}

	
	public void addProposedTransitionSeries(ITransitionSeries e)
	{
		fittingModel.proposals.addTransitionSeries(e);
		fittingProposalsInvalidated();
	}

	public void removeProposedTransitionSeries(ITransitionSeries e)
	{
		fittingModel.proposals.remove(e);
		fittingProposalsInvalidated();
	}

	public void clearProposedTransitionSeries()
	{
		fittingModel.proposals.clear();
		fittingProposalsInvalidated();
	}

	public List<ITransitionSeries> getProposedTransitionSeries()
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
		fittingModel.proposalResults.invalidate();
	}

	public void setDetectorMaterial(DetectorMaterialType type)
	{
		fittingModel.selections.getFittingParameters().setDetectorMaterial(type);
		fittingModel.proposals.getFittingParameters().setDetectorMaterial(type);
		
		fittingDataInvalidated();
		
		setUndoPoint("Set Detector Material");
		updateListeners(false);
	}

	public DetectorMaterialType getDetectorMaterial()
	{
		return fittingModel.selections.getFittingParameters().getDetectorMaterial();
	}
	
	public List<ITransitionSeries> proposeTransitionSeriesFromChannel(final int channel, ITransitionSeries currentTS)
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
	public ITransitionSeries selectTransitionSeriesAtChannel(int channel) {      
        float bestValue = 1f;
        FittingResultView bestFit = null;

        if (getFittingSelectionResults() == null) {
            return null;
        }
        
		for (FittingResultView fit : getFittingSelectionResults()) {
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
		return ! (getVisibleTransitionSeries().isEmpty() || plot.data().getDataSet().getScanData().isEmpty());
	}


	public void setFittingParameters(int scanSize, float min, float max)
	{
		fittingModel.selections.getFittingParameters().setCalibration(min, max, scanSize);
		fittingModel.proposals.getFittingParameters().setCalibration(min, max, scanSize);

		//TODO: Why is this here? Are we just resetting it to be sure they stay in sync?
		fittingModel.selections.getFittingParameters().setDetectorMaterial(getDetectorMaterial());
		fittingModel.proposals.getFittingParameters().setDetectorMaterial(getDetectorMaterial());

		
		setUndoPoint("Energy Calibration");
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
	
	public void setMinMaxEnergy(float min, float max) {
		int dataWidth = plot.data().getDataSet().getAnalysis().channelsPerScan();
		setFittingParameters(dataWidth, min, max);
		updateListeners(false);
	}

	
	public float getMinEnergy()
	{
		return fittingModel.selections.getFittingParameters().getCalibration().getMinEnergy();
	}
	
	public EnergyCalibration getEnergyCalibration() {
		return fittingModel.selections.getFittingParameters().getCalibration();
	}

	
	public boolean hasProposalFitting()
	{
		return fittingModel.proposalResults.getValue() != null;
	}

	public boolean hasSelectionFitting()
	{
		return fittingModel.selectionResults.getValue() != null;
	}

	public FittingSet getFittingSelections()
	{
		return fittingModel.selections;
	}

	public FittingSet getFittingProposals()
	{
		return fittingModel.proposals;
	}
	
	public FittingResultSetView getFittingProposalResults()
	{
		return fittingModel.proposalResults.getValue();
	}

	public FittingResultSetView getFittingSelectionResults()
	{
		return fittingModel.selectionResults.getValue();
	}

	public FittingResultView getFittingResultForTransitionSeries(ITransitionSeries ts) {
		if (getFittingSelectionResults() == null) return null;
		return getFittingSelectionResults().getFitForTransitionSeries(ts).orElse(null);
	}
	
	public List<ITransitionSeries> getHighlightedTransitionSeries() {
		return fittingModel.highlighted;
	}
	
	public void setHighlightedTransitionSeries(List<ITransitionSeries> highlighted) {
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
		setUndoPoint("Change Peak Fitter");
	}
	
	public Class<? extends FittingFunction> getFittingFunction() {
		return fittingModel.selections.getFittingParameters().getFittingFunction();
	}
	
	public CurveFitterPlugin getCurveFitter() {
		return fittingModel.curveFitter;
	}
	
	
	public void setCurveFitter(CurveFitterPlugin curveFitter) {
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

	public ExecutorSet<List<ITransitionSeries>> autodetectPeaks() {
		PeakSearcher searcher = new DoubleDerivativePeakSearcher();
		ReadOnlySpectrum data = plot.filtering().getFilteredPlot();
		ExecutorSet<List<ITransitionSeries>> exec = PeakProposal.search(
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
			for (ITransitionSeries ts : exec.getResult()) {
				getFittingSelections().addTransitionSeries(ts);
			}
			fittingDataInvalidated();
		});
		
		
		return exec;

		
	}

	public boolean hasAnnotation(ITransitionSeries ts) {
		if (!fittingModel.annotations.containsKey(ts)) {
			return false;
		}
		String annotation = getAnnotation(ts);
		return annotation != null && annotation.trim().length() > 0;
	}
	
	public String getAnnotation(ITransitionSeries ts) {
		return fittingModel.annotations.get(ts);
	}
	
	public void setAnnotation(ITransitionSeries ts, String annotation) {
		if (annotation.trim().length() == 0) {
			fittingModel.annotations.remove(ts);
		} else {
			fittingModel.annotations.put(ts, annotation);
		}
		updateListeners(false);
	}

	public Map<ITransitionSeries, String> getAnnotations() {
		return new HashMap<>(fittingModel.annotations);
	}

	public void clearAnnotations() {
		fittingModel.annotations.clear();
		updateListeners(false);
	}

	public boolean getShowEscapePeaks() {
		return fittingModel.selections.getFittingParameters().getShowEscapePeaks();
	}

	public void setShowEscapePeaks(boolean showEscapePeaks) {
		fittingModel.selections.getFittingParameters().setShowEscapePeaks(showEscapePeaks);
		fittingModel.proposals.getFittingParameters().setShowEscapePeaks(showEscapePeaks);
		
		fittingDataInvalidated();
		setUndoPoint("Toggle Escape Peaks");
		updateListeners(false);
	}

	
	public void populatePlotData(PlotData data) {
		data.selectionResults = getFittingSelectionResults();
		data.proposedResults = getFittingProposalResults();
		data.calibration = getEnergyCalibration();
		data.detectorMaterial = getDetectorMaterial();
		data.highlightedTransitionSeries = getHighlightedTransitionSeries();
		data.proposedTransitionSeries = getProposedTransitionSeries();
		data.annotations = getAnnotations();
	}

	public SavedFittings save() {
		
		var fittings = fittingModel.selections.getFittedTransitionSeries().stream().map(ITransitionSeries::save).toList();
		
		Map<String, String> savedannos = this.fittingModel.annotations.entrySet().stream().collect(Collectors.toMap(
				e -> e.getKey().getShellement(), 
				e -> e.getValue()
		));
		
		
		this.fittingModel.annotations.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getShellement(), e-> e.getValue()));
		
		return new SavedFittings(
			fittings, 
			savedannos, 
			getFittingSolver().getClass().getName(), //TODO: replace this 
			getCurveFitter().getClass().getName(), //TODO: replace this
			getFittingFunction().getName(), //TODO: replace this
			fittingModel.selections.getFittingParameters().save()
		);
	}

	public List<String> load(SavedFittings saved) {
		
		fittingModel.selections.clear();
		clearAnnotations();
		for (var fitting : saved.fittings) {
			var ts = PeakTable.SYSTEM.get(fitting.shellement);
			if (ts == null) { 
				continue; 
			}
			//Add this ts to our list
			fittingModel.selections.addTransitionSeries(ts);
			//Check for an annotation. If it exists, add it.
			if (saved.annotations.containsKey(fitting.shellement)) {
				setAnnotation(ts, saved.annotations.get(fitting.shellement));
			}
		}
		
		
		Class<? extends FittingSolver> fittingSolverClass;
		try {
			fittingSolverClass = (Class<? extends FittingSolver>) Class.forName(saved.solver);
			fittingModel.fittingSolver = fittingSolverClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException | RuntimeException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to find Fitting Solver " + saved.solver, e);
		}

		
		List<String> errors = new ArrayList<>();
		
		
		//Restore CurveFitter
		Class<? extends CurveFitterPlugin> curveFitterClass;
		try {
			curveFitterClass = (Class<? extends CurveFitterPlugin>) Class.forName(saved.fitter);
			fittingModel.curveFitter = curveFitterClass.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			String[] parts = saved.fitter.split("\\.");
			String shortname = parts[parts.length-1];
			errors.add("Failed to find Curve Fitter " + shortname);
		} catch (ReflectiveOperationException | RuntimeException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to find Curve Fitter " + saved.fitter, e);
		}
		
		//Restore the fitting function
		Class<? extends FittingFunction> fittingFunctionClass ;
		try {
			fittingFunctionClass = (Class<? extends FittingFunction>) Class.forName(saved.model);
			fittingModel.selections.getFittingParameters().setFittingFunction(fittingFunctionClass);
			fittingModel.proposals.getFittingParameters().setFittingFunction(fittingFunctionClass);
		} catch (ClassNotFoundException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to find Fitting Function " + saved.model, e);
		}
		
		
		//We load all this here instead of a load method in FittingParameters because we track two
		//of them, one for selections and one for proposed selections.
		setMinMaxEnergy(saved.calibration.min, saved.calibration.max);
		setDetectorMaterial(DetectorMaterialType.valueOf(saved.calibration.detectorMaterial));
		setFWHMBase(saved.calibration.fwhmbase);
		setShowEscapePeaks(saved.calibration.escapes);

		return errors;
		
	}
	
	
	
}

