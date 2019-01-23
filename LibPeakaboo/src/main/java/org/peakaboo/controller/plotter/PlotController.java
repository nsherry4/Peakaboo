package peakaboo.controller.plotter;

import java.io.File;
import java.util.Map;

import cyclops.ReadOnlySpectrum;
import eventful.EventfulType;
import peakaboo.controller.plotter.calibration.CalibrationController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.filtering.FilteringController;
import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.controller.plotter.undo.UndoController;
import peakaboo.controller.plotter.view.ChannelCompositeMode;
import peakaboo.controller.plotter.view.ViewController;
import peakaboo.controller.settings.SavedSession;
import peakaboo.display.plot.PlotData;
import peakaboo.filter.model.Filter;
import peakaboo.mapping.rawmap.RawMapSet;
import plural.streams.StreamExecutor;




/**
 * This class is the controller for plot displays.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotController extends EventfulType<String> 
{
		
	private UndoController					undoController;
	private DataController					dataController;
	private FilteringController				filteringController;
	private FittingController				fittingController;
	private ViewController					viewController;
	private CalibrationController			calibrationController;

	private File configDir;

	public static enum UpdateType
	{
		DATA, FITTING, FILTER, UNDO, UI, CALIBRATION
	}
	
	
	public PlotController(File configDir)
	{
		super();
		this.configDir = configDir;
		initPlotController();
	}

	private void initPlotController()
	{
				
		undoController = new UndoController(this);
		dataController = new DataController(this);
		filteringController = new FilteringController(this);
		fittingController = new FittingController(this);
		calibrationController = new CalibrationController(this);
		viewController = new ViewController(this);
		viewController.loadPersistentSettings();
		
		
		undoController.addListener(() -> updateListeners(UpdateType.UNDO.toString()));
		dataController.addListener(() -> updateListeners(UpdateType.DATA.toString()));
		filteringController.addListener(() -> updateListeners(UpdateType.FILTER.toString()));		
		fittingController.addListener(b -> updateListeners(UpdateType.FITTING.toString()));
		viewController.addListener(() -> updateListeners(UpdateType.UI.toString()));
		calibrationController.addListener(() -> updateListeners(UpdateType.CALIBRATION.toString()));
		
		undoController.setUndoPoint("");
	}

	
	public SavedSession getSavedSettings() {
		return SavedSession.storeFrom(this);
	}
	
	
	public SavedSession readSavedSettings(String yaml) {
		return SavedSession.deserialize(yaml);
	}

	
	public void loadSettings(String data, boolean isUndoAction) {
		SavedSession saved = SavedSession.deserialize(data);
		loadSessionSettings(saved, isUndoAction);		
	}
	
	public void loadSessionSettings(SavedSession saved, boolean isUndoAction) {
		if (!isUndoAction) undoController.setUndoPoint("Load Session");
		saved.loadInto(this);
		
		filteringController.filteredDataInvalidated();
		fittingController.fittingDataInvalidated();
		fittingController.fittingProposalsInvalidated();
		viewController.updateListeners();
		calibrationController.updateListeners();
		
		//fire an update message from the fittingcontroller with a boolean flag
		//indicating that the change is not comming from inside the fitting controller
		fittingController.updateListeners(true);

	}

	/**
	 * Get the scan that should currently be shown. Looks up appropriate information
	 * in the settings controller, and uses it to calculate the current scan from the
	 * raw data supplied by the data controller.
	 * @return a Spectrum which contains a scan
	 */
	public ReadOnlySpectrum currentScan()
	{
		if (!dataController.hasDataSet()) {
			return null;
		}
		
		ReadOnlySpectrum originalData = null;
		
		if (viewController.getChannelCompositeMode() == ChannelCompositeMode.AVERAGE) {
			originalData = dataController.getDataSet().getAnalysis().averagePlot();
		} else if (viewController.getChannelCompositeMode()  == ChannelCompositeMode.MAXIMUM) {
			originalData = dataController.getDataSet().getAnalysis().maximumPlot();
		} else {
			originalData = dataController.getDataSet().getScanData().get(viewController.getScanNumber());
		}
		
		return originalData;
		
	}
	

	/**
	 * Returns a PlotData object, which contains settins for the plot to be displayed with
	 */
	public PlotData getPlotData() {
		
		PlotData data = new PlotData();
		PlotSpectra dataForPlot = getDataForPlot();
		
		//TODO: Can this whole block be moved to the controller, since it just calls into controller a bunch?
		data.selectionResults = fitting().getFittingSelectionResults();
		data.proposedResults = fitting().getFittingProposalResults();
		data.calibration = fitting().getEnergyCalibration();
		data.escape = fitting().getEscapeType();
		data.highlightedTransitionSeries = fitting().getHighlightedTransitionSeries();
		data.proposedTransitionSeries = fitting().getProposedTransitionSeries();
		data.annotations = fitting().getAnnotations();
		
		data.consistentScale = view().getConsistentScale();
		
		data.dataset = data().getDataSet();
		
		data.filters = filtering().getActiveFilters();
		
		if (dataForPlot != null) {
			data.filtered = dataForPlot.filtered;
			data.raw = dataForPlot.raw;
			data.deltas = dataForPlot.deltas;
		}

		
		
		return data;
	}
	
	
	public static class PlotSpectra {
		public ReadOnlySpectrum raw;
		public ReadOnlySpectrum filtered;
		public Map<Filter, ReadOnlySpectrum> deltas;
	}
	
	public PlotSpectra getDataForPlot()
	{

		ReadOnlySpectrum originalData = null;
	
		if (!dataController.hasDataSet() || currentScan() == null) return null;

		
		// get the original data
		originalData = currentScan();
		
		PlotSpectra spectra = new PlotSpectra();
		spectra.raw = originalData;
		spectra.filtered = filteringController.getFilteredPlot();
		spectra.deltas = filteringController.getFilterDeltas();
		
		return spectra;
	}
	

	
	/**
	 * Returns an {@link StreamExecutor} which will generate a map based on the user's current 
	 * selections.
	 * @param type The type of {@link FittingTransform} to use in the calculation
	 * @return
	 */
	public StreamExecutor<RawMapSet> getMapTask() {
		return dataController.getMapTask(
				filteringController.getActiveFilters(), 
				fittingController.getFittingSelections(), 
				fittingController.getCurveFitter(), 
				fittingController.getFittingSolver()
			);
	}
	
	
	public DataController data()
	{
		return dataController;
	}

	public FilteringController filtering()
	{
		return filteringController;
	}

	public FittingController fitting()
	{
		return fittingController;
	}

	public UndoController history()
	{
		return undoController;
	}

	public ViewController view()
	{
		return viewController;
	}

	public File getConfigDir() {
		return configDir;
	}

	public CalibrationController calibration() {
		return calibrationController;
	}	
	
}
