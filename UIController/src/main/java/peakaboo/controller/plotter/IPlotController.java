package peakaboo.controller.plotter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import eventful.IEventfulType;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.plotter.data.IDataController;
import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.controller.plotter.settings.ISettingsController;
import peakaboo.controller.plotter.undo.IUndoController;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;



public interface IPlotController extends IEventfulType<String>
{

	static enum UpdateType
	{
		DATA, FITTING, FILTER, UNDO, UI
	}
	
	/**
	 * Get the data controller
	 */
	IDataController data();

	/**
	 * Get the filtering controller
	 */
	IFilteringController filtering();

	/**
	 * Get the fitting controller
	 */
	IFittingController fitting();
	
	/**
	 * Get the history/undo controller
	 */
	IUndoController history();
	
	/**
	 * Get the settings controller
	 */
	ISettingsController settings();
	
	/**
	 * Get the mapping controller
	 */
	MappingController mapping();
	
	
	
	
	List<AxisPainter> getAxisPainters();

	void setAxisPainters(List<AxisPainter> axisPainters);
	
	DrawingRequest getDR();
	
	void setDR(DrawingRequest dr);
	
	
	/**
	 * Attempts to retrieve the map controller from the plot controller.
	 * If another mapping window is currently using the map controller,
	 * a new map controller is created. When the mapping windows are closed,
	 * whichever mapping window is closed last will be the last to check-in
	 * their map controller, and that will be the one saved for future use
	 * @return
	 */
	MappingController checkoutMapController();

	
	/**
	 * After using the map controller, return it to the plot controller.
	 * If a new map window is opened after this controller is checked in,
	 * but before any other map controller is checked in, this will be 
	 * the map controller used for that new window
	 */
	void checkinMapController(MappingController controller);

	void setMapController(MappingController mapController);

	InputStream getSerializedPlotSettings();

	void savePreferences(OutputStream outStream);

	void loadPreferences(InputStream inStream, boolean isUndoAction);

	
	/**
	 * Returns a pair of spectra. The first one is the filtered data, the second is the original
	 */
	Pair<ReadOnlySpectrum, ReadOnlySpectrum> getDataForPlot();

	
	/**
	 * In order to prevent high cpu use every time calculated data such as averaged, filtered, or 
	 * fitted plots are requested, the calculated data is cached. When a setting is changed such 
	 * that the cached data would no longer match freshly calculated data, the cache must be cleared.
	 */
	void regenerateCahcedData();

	
	/**
	 * Returns an {@link ExecutorSet} which will generate a map based on the user's current 
	 * selections.
	 * @param type The type of {@link FittingTransform} to use in the calculation
	 * @return
	 */
	ExecutorSet<MapResultSet> getMapCreationTask(FittingTransform type);

	
}