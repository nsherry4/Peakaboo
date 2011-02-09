package peakaboo.controller.plotter;



import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


import peakaboo.controller.CanvasController;
import peakaboo.controller.mapper.MapController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.filtering.FilteringController;
import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.controller.plotter.settings.ChannelCompositeMode;
import peakaboo.controller.plotter.settings.SettingsController;
import peakaboo.controller.plotter.undo.UndoController;
import peakaboo.controller.settings.Settings;
import peakaboo.curvefit.painters.FittingMarkersPainter;
import peakaboo.curvefit.painters.FittingPainter;
import peakaboo.curvefit.painters.FittingSumPainter;
import peakaboo.curvefit.painters.FittingTitlePainter;
import peakaboo.filter.AbstractFilter;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.workers.PluralSet;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.ViewTransform;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.painters.axis.AxisPainter;
import scidraw.drawing.painters.axis.LineAxisPainter;
import scidraw.drawing.painters.axis.TitleAxisPainter;
import scidraw.drawing.plot.PlotDrawing;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.axis.GridlinePainter;
import scidraw.drawing.plot.painters.axis.TickMarkAxisPainter;
import scidraw.drawing.plot.painters.plot.OriginalDataPainter;
import scidraw.drawing.plot.painters.plot.PrimaryPlotPainter;
import scitypes.Coord;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


import eventful.EventfulListener;
import eventful.EventfulType;
import eventful.EventfulTypeListener;
import fava.datatypes.Bounds;
import fava.datatypes.Pair;




/**
 * This class is the controller for plot displays.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotController extends EventfulType<String>
{
	
	
	private PlotDrawing						plot;
	public List<AxisPainter>				axisPainters;
	public DrawingRequest					dr;

	
	public UndoController					undoController;
	public MapController					mapController;
	public DataController					dataController;
	public FilteringController				filteringController;
	public FittingController				fittingController;
	public SettingsController				settingsController;


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
		mapController = new MapController(this);
		
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

	
	public MapController getMapController()
	{
		return mapController;
	}

	public void setMapController(MapController mapController)
	{
		this.mapController = mapController;
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
	 * Get the scan that should currently be shown.
	 * @return a Spectrum which contains a scan
	 */
	private Spectrum currentScan()
	{
		Spectrum originalData = null;
		
		if (settingsController.getChannelCompositeType() == ChannelCompositeMode.AVERAGE) {
			originalData = dataController.getAveragePlot();
		} else if (settingsController.getChannelCompositeType()  == ChannelCompositeMode.MAXIMUM) {
			originalData = dataController.getMaximumPlot();
		} else {
			originalData = dataController.getScanAtIndex(settingsController.getScanNumber());
		}
		
		return originalData;
		
	}
	
	/**
	 * Returns a pair of spectra. The first one is the filtered data, the second is the original
	 */
	public Pair<Spectrum, Spectrum> getDataForPlot()
	{

		Spectrum originalData = null;

		if (!dataController.hasDataSet() || currentScan() == null) return null;

		
		// get the original data
		originalData = currentScan();

		regenerateCahcedData();
		
		return new Pair<Spectrum, Spectrum>(filteringController.getFilteredPlot(), originalData);
	}

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
	
	public PluralSet<MapResultSet> TASK_getDataForMapFromSelectedRegions(FittingTransform type)
	{
		return dataController.TASK_calculateMap(filteringController.getActiveFilters(), fittingController.getFittingSelections(), type);
	}
		

	
}
