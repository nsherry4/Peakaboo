package peakaboo.controller.plotter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import eventful.IEventfulType;
import peakaboo.controller.mapper.mapview.MapSettings;
import peakaboo.controller.plotter.data.IDataController;
import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.controller.plotter.settings.SettingsController;
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
	SettingsController settings();
	

	
	
	
	
	List<AxisPainter> getAxisPainters();

	void setAxisPainters(List<AxisPainter> axisPainters);
	
	DrawingRequest getDR();
	
	void setDR(DrawingRequest dr);
	
	
	public void setLastMapSettings(MapSettings settings);
	public MapSettings getLastMapSettings();
	

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