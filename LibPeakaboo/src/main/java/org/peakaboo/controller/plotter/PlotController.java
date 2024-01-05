package org.peakaboo.controller.plotter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.controller.plotter.calibration.CalibrationController;
import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.controller.plotter.io.IOController;
import org.peakaboo.controller.plotter.notification.NotificationController;
import org.peakaboo.controller.plotter.notification.NotificationController.Notice;
import org.peakaboo.controller.plotter.undo.UndoController;
import org.peakaboo.controller.plotter.view.ViewController;
import org.peakaboo.controller.session.v2.SavedAppData;
import org.peakaboo.controller.session.v2.SavedSession;
import org.peakaboo.curvefit.curve.fitting.DelegatingFittingSetView;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.display.plot.PlotData;
import org.peakaboo.display.plot.PlotData.PlotDataSpectra;
import org.peakaboo.filter.model.Filter.FilterContext;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.mapping.Mapping;
import org.peakaboo.mapping.rawmap.RawMapSet;
import org.peakaboo.tier.Tier;




/**
 * This class is the controller for plot displays.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotController extends EventfulType<PlotUpdateType> 
{
		
	private UndoController					undoController;
	private DataController					dataController;
	private FilteringController				filteringController;
	private FittingController				fittingController;
	private ViewController					viewController;
	private CalibrationController			calibrationController;
	private IOController					ioController;
	private NotificationController			notificationController;
	
	private File configDir;

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
		calibrationController = Tier.provider().createPlotCalibrationController(this);
		viewController = new ViewController(this);
		ioController = new IOController();
		notificationController = new NotificationController();
		
		undoController.addListener(() -> updateListeners(PlotUpdateType.UNDO));
		dataController.addListener(() -> updateListeners(PlotUpdateType.DATA));
		filteringController.addListener(() -> updateListeners(PlotUpdateType.FILTER));
		fittingController.addListener(b -> updateListeners(PlotUpdateType.FITTING));
		viewController.addListener(() -> updateListeners(PlotUpdateType.UI));
		calibrationController.addListener(() -> updateListeners(PlotUpdateType.CALIBRATION));
		ioController.addListener(() -> updateListeners(PlotUpdateType.UI));
		
		undoController.setUndoPoint("", /*distinctChange =*/ true);
	}

	
	@Deprecated(since = "6", forRemoval = true)
	public SavedSessionV1 getSavedSettings() {
		return SavedSessionV1.storeFrom(this);
	}
	
	public SavedSession save() {
		
		var extended = new LinkedHashMap<String, Object>();
		var calibration = calibrationController.save();
		if (!calibration.isEmpty()) {
			extended.putAll(calibration);
		}
		
		return new SavedSession(
			dataController.save(), 
			filteringController.save(), 
			fittingController.save(), 
			viewController.getViewModel(), 
			SavedAppData.current(), 
			extended
		);
	}
	
	/**
	 * Given a {@link SavedSession} in yaml form, read the saved session and load it's values
	 */
	public void load(String yaml, boolean isUndoAction) throws DruthersLoadException {
		DruthersSerializer.deserialize(yaml, false,
				new DruthersSerializer.FormatLoader<>(
						SavedSession.FORMAT, 
						SavedSession.class, 
						saved -> load(saved, isUndoAction)
					)
			);
	}
	
	/**
	 * Load the values from this {@link SavedSession} into this controller's model
	 */
	public void load(SavedSession saved, boolean isUndoAction) {
		if (!isUndoAction) undoController.setUndoPoint("Load Session", /*distinctChange =*/ true);
		data().load(saved.data);
		filtering().load(saved.filters);
		fitting().load(saved.fittings);
		view().getViewModel().copy(saved.view);
		calibration().load(saved.extended);
	}
	
	
	@Deprecated(since = "6", forRemoval = true)
	public static Optional<SavedSessionV1> readSavedSettings(String yaml) {
		try {
			return Optional.of(DruthersSerializer.deserialize(yaml, SavedSessionV1.class));
		} catch (Exception e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to load saved session", e);
			return Optional.empty();
		}
	}

	
	@Deprecated(since = "6", forRemoval = true)
	public void loadSessionSettingsV1(SavedSessionV1 saved, boolean isUndoAction) {
		if (!isUndoAction) undoController.setUndoPoint("Load Session", /*distinctChange =*/ true);
		
		List<String> errors = saved.loadInto(this);
		
		filteringController.filteredDataInvalidated();
		fittingController.fittingDataInvalidated();
		fittingController.fittingProposalsInvalidated();
		viewController.updateListeners();
		calibrationController.updateListeners();
		
		//fire an update message from the fittingcontroller with a boolean flag
		//indicating that the change is not comming from inside the fitting controller
		fittingController.updateListeners(true);

		for (String error : errors) {
			this.notifications().updateListeners(new Notice(error, null));
		}
		
	}

	/**
	 * Get the scan that should currently be shown. Looks up appropriate information
	 * in the settings controller, and uses it to calculate the current scan from the
	 * raw data supplied by the data controller.
	 * @return a Spectrum which contains a scan
	 */
	public SpectrumView currentScan()
	{
		if (!dataController.hasDataSet()) {
			return null;
		}

		return switch(viewController.getChannelCompositeMode()) {
			case AVERAGE -> dataController.getDataSet().getAnalysis().averagePlot();
			case MAXIMUM -> dataController.getDataSet().getAnalysis().maximumPlot();
			case NONE -> dataController.getDataSet().getScanData().get(viewController.getScanNumber());
		};

	}
	
	/**
	 * Returns a string to spectrum mapping of all additional scans which should be
	 * processed along with the current scan from
	 * {@link PlotController#currentScan()}
	 */
	public Map<String, SpectrumView> currentOtherScans() {
		//TODO find a way to get these from the channel composite mode instead of using this example for testing
		return Map.of("asdf", currentScan());
		//return new HashMap<>();
	}
	
	

	/**
	 * Returns a PlotData object, which contains settins for the plot to be displayed with
	 */
	public PlotData getPlotData() {
		
		PlotData data = new PlotData();
		PlotDataSpectra spectra = getPlotDataSpectra();
		
		fitting().populatePlotData(data);
		data.consistentScale = view().getConsistentScale();
		data.dataset = data().getDataSet();
		data.filters = filtering().getActiveFilters();
		data.spectra = spectra;

		return data;
	}

	public PlotDataSpectra getPlotDataSpectra()	{
		if (!dataController.hasDataSet() || currentScan() == null) return null;

		return new PlotDataSpectra(
				currentScan(), 
				filteringController.getFilteredPlot(),
				filteringController.getFilterDeltas(),
				filteringController.getFilteredOtherPlots()
			);
	}
	

	public FilterContext getFilterContext() {
		FilterContext ctx = new FilterContext(
				data().getDataSet(), 
				new DelegatingFittingSetView(fitting().getFittingSelections())
			);
		return ctx;
	}
	
	/**
	 * Returns an {@link StreamExecutor} which will generate a map based on the user's current 
	 * selections.
	 * @param type The type of {@link FittingTransform} to use in the calculation
	 * @return
	 */
	public StreamExecutor<RawMapSet> getMapTask() {
		return Mapping.mapTask(
				filteringController.getActiveFilters(), 
				fittingController.getCurveFitter(), 
				fittingController.getFittingSolver(),
				getFilterContext()
			);
	}
	
	
	public void writeFitleredSpectrumToCSV(File saveFile) {
		SpectrumView spectrum = currentScan();
		FilterSet filters = filtering().getActiveFilters();
		spectrum = filters.applyFiltersUnsynchronized(spectrum, getFilterContext());
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(saveFile))) {
			writer.write(spectrum.toString(", ") + "\n");
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to save fitted data", e);
		}
	}
	
	public ExecutorSet<Object> writeFitleredDataSetToCSV(File saveFile) {
		
		return Plural.build("Exporting Data", "Writing", (execset, exec) -> {
			FilterSet filters = filtering().getActiveFilters();
			ScanData data = data().getDataSet().getScanData();
			
			exec.setWorkUnits(data.scanCount());

			try (Writer writer = new OutputStreamWriter(new FileOutputStream(saveFile))) {
				int count = 0;
				for (SpectrumView spectrum : data) {
					spectrum = filters.applyFiltersUnsynchronized(spectrum, getFilterContext());
					writer.write(spectrum.toString(", ") + "\n");

					//abort test
					if (execset.isAbortRequested()) {
						break;
					}
					
					//progress counter
					count++;
					if (count >= 100) {
						exec.workUnitCompleted(count);
						count = 0;
					}
				}
			} catch (Exception e) { 
				PeakabooLog.get().log(Level.SEVERE, "Failed to save fitted data", e);
				execset.aborted();
			}
			
			return null;
		});
		
	}
	
	

	public void writeFittingInformation(OutputStream os) {
		
		List<ITransitionSeries> tss = fitting().getFittedTransitionSeries();
		
		try {
			// get an output stream to write the data to
			OutputStreamWriter osw = new OutputStreamWriter(os);
			DetectorProfile profile = calibration().getDetectorProfile();
			
			//header
			osw.write("Fitting, Intensity (Raw), Area (Raw)");
			if (calibration().hasDetectorProfile()) {
				osw.write(", Intensity (Calibrated with " + profile.getName() + "), Area (Calibrated with " + profile.getName() + ")");
			}
			osw.write("\n");
			
			// write out the data
			float intensity, area;
			for (ITransitionSeries ts : tss) {

				if (ts.isVisible()) {
					intensity = fitting().getTransitionSeriesIntensity(ts);
					area = fitting().getFittingResultForTransitionSeries(ts).getFitSum();
					
					//write uncalibrated data
					osw.write(
							ts.toString() +
							", " + SigDigits.roundFloatTo(intensity, 2) + 
							", " + SigDigits.roundFloatTo(area, 2)
						);
					
					//write calibrated data if we have it in this profile for this TS
					if (profile.contains(ts)) {
						osw.write(
								", " + SigDigits.roundFloatTo(profile.calibrate(intensity, ts), 2) +
								", " + SigDigits.roundFloatTo(profile.calibrate(area, ts), 2)
							);
					}
					osw.write("\n");
				}
			}
			osw.flush();
		}
		catch (IOException e)
		{
			PeakabooLog.get().log(Level.SEVERE, "Failed to save fitting information", e);
		}
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
	
	public IOController io() {
		return ioController;
	}

	public File getConfigDir() {
		return configDir;
	}

	public CalibrationController calibration() {
		return calibrationController;
	}	

	public NotificationController notifications() {
		return notificationController;
	}	
	
}
