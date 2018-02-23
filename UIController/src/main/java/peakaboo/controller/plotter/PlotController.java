package peakaboo.controller.plotter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import eventful.EventfulListener;
import eventful.EventfulType;
import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.mapview.MapSettings;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.filtering.FilteringController;
import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.controller.plotter.settings.ChannelCompositeMode;
import peakaboo.controller.plotter.settings.SettingsController;
import peakaboo.controller.plotter.undo.IUndoController;
import peakaboo.controller.plotter.undo.UndoController;
import peakaboo.controller.settings.Settings;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;




/**
 * This class is the controller for plot displays.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotController extends EventfulType<String> 
{
	
	public List<AxisPainter>				axisPainters;
	public DrawingRequest					dr;

	
	private IUndoController					undoController;
	private DataController					dataController;
	private FilteringController				filteringController;
	private IFittingController				fittingController;
	private SettingsController				settingsController;

	private MapSettings						savedMapSettings;


	public static enum UpdateType
	{
		DATA, FITTING, FILTER, UNDO, UI
	}
	
	
	public PlotController()
	{
		super();
		initPlotController();
	}

	private void initPlotController()
	{
		
		dr = new DrawingRequest();
		
		
		undoController = new UndoController(this);
		dataController = new DataController(this);
		filteringController = new FilteringController(this);
		fittingController = new FittingController(this);
		settingsController = new SettingsController(this);

		
		undoController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.UNDO.toString());
			}
		});
		
		dataController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.DATA.toString());
			}
		});
		
		filteringController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.FILTER.toString());
			}
		});
		
		fittingController.addListener(new EventfulTypeListener<Boolean>() {
			
			public void change(Boolean b)
			{
				updateListeners(UpdateType.FITTING.toString());
			}
		});
		
		settingsController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.UI.toString());
			}
		});
		
		
		undoController.setUndoPoint("");
	}


	public void setLastMapSettings(MapSettings settings) {
		savedMapSettings = settings;
	}
	
	public MapSettings getLastMapSettings() {
		return savedMapSettings;
	}
	
	
	public InputStream getSerializedPlotSettings()
	{
		//save the current state
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		savePreferences(baos);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		return bais;	
	}
	
	public void savePreferences(OutputStream outStream)
	{
		Settings.savePreferences(
				this, 
				settingsController.getSettingsModel(), 
				fittingController.getFittingModel(),
				filteringController.getFilteringMode(), 
				outStream
			);
	}

	public void loadPreferences(InputStream inStream, boolean isUndoAction)
	{
		Settings.loadPreferences(this, dataController,
				settingsController.getSettingsModel(), 
				fittingController.getFittingModel(),
				filteringController.getFilteringMode(),
				inStream
			);
		
		if (!isUndoAction) undoController.setUndoPoint("Load Session");
		
		filteringController.filteredDataInvalidated();
		fittingController.fittingDataInvalidated();
		fittingController.fittingProposalsInvalidated();
		settingsController.updateListeners();
		
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
	private ReadOnlySpectrum currentScan()
	{
		ReadOnlySpectrum originalData = null;
		
		if (settingsController.getChannelCompositeType() == ChannelCompositeMode.AVERAGE) {
			originalData = dataController.getDataSet().averagePlot();
		} else if (settingsController.getChannelCompositeType()  == ChannelCompositeMode.MAXIMUM) {
			originalData = dataController.getDataSet().maximumPlot();
		} else {
			originalData = dataController.getDataSet().getScanData().get(settingsController.getScanNumber());
		}
		
		return originalData;
		
	}
	

	/**
	 * Returns a pair of spectra. The first one is the filtered data, the second is the original
	 */
	public Pair<ReadOnlySpectrum, ReadOnlySpectrum> getDataForPlot()
	{

		ReadOnlySpectrum originalData = null;
	
		if (!dataController.hasDataSet() || currentScan() == null) return null;

		
		// get the original data
		originalData = currentScan();

		regenerateCahcedData();
		
		return new Pair<ReadOnlySpectrum, ReadOnlySpectrum>(filteringController.getFilteredPlot(), originalData);
	}


	/**
	 * In order to prevent high cpu use every time calculated data such as averaged, filtered, or 
	 * fitted plots are requested, the calculated data is cached. When a setting is changed such 
	 * that the cached data would no longer match freshly calculated data, the cache must be cleared.
	 */
	public void regenerateCahcedData()
	{

		// Regenerate Filtered Data
		if (dataController.hasDataSet() && currentScan() != null)
		{

			
			if (filteringController.getFilteredPlot() == null)
			{
				filteringController.calculateFilteredData(currentScan());
			}

			// Fitting Selections
			if (!fittingController.hasSelectionFitting())
			{
				fittingController.calculateSelectionFittings(filteringController.getFilteredPlot());
			}

			// Fitting Proposals
			if (!fittingController.hasProposalFitting())
			{
				fittingController.calculateProposalFittings();
			}

		}

	}
	
	
	/**
	 * Returns an {@link ExecutorSet} which will generate a map based on the user's current 
	 * selections.
	 * @param type The type of {@link FittingTransform} to use in the calculation
	 * @return
	 */
	public ExecutorSet<MapResultSet> getMapCreationTask(FittingTransform type)
	{
		return dataController.TASK_calculateMap(filteringController.getActiveFilters(), fittingController.getFittingSelections(), type);
	}

	
	
	public DataController data()
	{
		return dataController;
	}

	public FilteringController filtering()
	{
		return filteringController;
	}

	public IFittingController fitting()
	{
		return fittingController;
	}

	public IUndoController history()
	{
		return undoController;
	}

	public SettingsController settings()
	{
		return settingsController;
	}

	public List<AxisPainter> getAxisPainters()
	{
		return axisPainters;
	}

	public void setAxisPainters(List<AxisPainter> axisPainters)
	{
		this.axisPainters = axisPainters;
	}
	
	public DrawingRequest getDR()
	{
		return dr;
	}

	public void setDR(DrawingRequest dr)
	{
		this.dr = dr;
	}

	
}
