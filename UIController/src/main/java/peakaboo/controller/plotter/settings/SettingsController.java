package peakaboo.controller.plotter.settings;

import java.io.Serializable;

import eventful.Eventful;
import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import scidraw.drawing.ViewTransform;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;


public class SettingsController extends Eventful implements Serializable
{

	
	private SettingsModel settingsModel;
	private PlotController plot;
	
	public SettingsController(PlotController plotController)
	{
		this.plot = plotController;
		settingsModel = new SettingsModel();
	}

	public SettingsModel getSettingsModel()
	{
		return settingsModel;
	}

	private void setUndoPoint(String change)
	{
		plot.history().setUndoPoint(change);
	}
	

	public float getZoom()
	{
		return settingsModel.zoom;
	}

	public void setZoom(float zoom)
	{
		settingsModel.zoom = zoom;
		updateListeners();
	}

	public void setShowIndividualSelections(boolean showIndividualSelections)
	{
		settingsModel.showIndividualFittings = showIndividualSelections;
		setUndoPoint("Individual Fittings");
		plot.fitting().fittingDataInvalidated();
	}

	public boolean getShowIndividualSelections()
	{
		return settingsModel.showIndividualFittings;
	}


	public void setMaxEnergy(float max) {
		settingsModel.maxEnergy = max;
		//if (plot.data().hasDataSet() && plot.data().getDataSet().channelsPerScan() > 0) {
			plot.fitting().setFittingParameters(getMinEnergy(), max);
		//}
		updateListeners();
	}

	public float getMaxEnergy()
	{
		return settingsModel.maxEnergy;
	}

	
	public void setMinEnergy(float min) {
		settingsModel.minEnergy = min;
		//if (plot.data().hasDataSet() && plot.data().getDataSet().channelsPerScan() > 0) {
			plot.fitting().setFittingParameters(min, getMaxEnergy());
		//}
		updateListeners();
	}

	
	public float getMinEnergy()
	{
		return settingsModel.minEnergy;
	}
	
	public void setViewLog(boolean log)
	{
		if (log)
		{
			settingsModel.viewTransform = ViewTransform.LOG;
		}
		else
		{
			settingsModel.viewTransform = ViewTransform.LINEAR;
		}
		setUndoPoint("Log View");
		updateListeners();
	}

	public boolean getViewLog()
	{
		return settingsModel.viewTransform == ViewTransform.LOG;
	}

	public void setShowChannelMode(ChannelCompositeMode mode)
	{
		settingsModel.channelComposite = mode;
		setUndoPoint(mode.show());
		plot.filtering().filteredDataInvalidated();
	}
	

	public ChannelCompositeMode getChannelCompositeType()
	{
		return settingsModel.channelComposite;
	}

	public void setScanNumber(int number)
	{
		//negative is downwards, positive is upwards
		int direction = number - settingsModel.scanNumber;

		if (direction > 0)
		{
			number = plot.data().getDataSet().firstNonNullScanIndex(number);
		}
		else
		{
			number = plot.data().getDataSet().lastNonNullScanIndex(number);
		}

		if (number == -1)
		{
			updateListeners();
			return;
		}

		
		if (number > plot.data().getDataSet().getScanData().scanCount() - 1) {
			number = plot.data().getDataSet().getScanData().scanCount() - 1;
		}
		if (number < 0) number = 0;
		settingsModel.scanNumber = number;
		plot.filtering().filteredDataInvalidated();
	}

	public int getScanNumber()
	{
		return settingsModel.scanNumber;
	}

	public void setShowAxes(boolean axes)
	{
		settingsModel.showAxes = axes;
		plot.setAxisPainters(null);
		setUndoPoint("Axes");
		updateListeners();
	}

	public boolean getShowAxes()
	{
		return settingsModel.showAxes;
	}

	public boolean getShowTitle()
	{
		return settingsModel.showPlotTitle;
	}

	public void setShowTitle(boolean show)
	{
		settingsModel.showPlotTitle = show;
		plot.setAxisPainters(null);
		setUndoPoint("Title");
		updateListeners();
	}

	public void setMonochrome(boolean mono)
	{
		settingsModel.monochrome = mono;
		setUndoPoint("Monochrome");
		updateListeners();
	}

	public boolean getMonochrome()
	{
		return settingsModel.monochrome;
	}

	public void setShowElementTitles(boolean show)
	{
		settingsModel.showElementFitTitles = show;
		setUndoPoint("Fitting Titles");
		updateListeners();
	}

	public void setShowElementMarkers(boolean show)
	{
		settingsModel.showElementFitMarkers = show;
		setUndoPoint("Fitting Markers");
		updateListeners();
	}

	public void setShowElementIntensities(boolean show)
	{
		settingsModel.showElementFitIntensities = show;
		setUndoPoint("Fitting Heights");
		updateListeners();
	}

	public boolean getShowElementTitles()
	{
		return settingsModel.showElementFitTitles;
	}

	public boolean getShowElementMarkers()
	{
		return settingsModel.showElementFitMarkers;
	}

	public boolean getShowElementIntensities()
	{
		return settingsModel.showElementFitIntensities;
	}

	public void setShowRawData(boolean show)
	{
		settingsModel.backgroundShowOriginal = show;
		setUndoPoint("Raw Data Outline");
		updateListeners();
	}

	public boolean getShowRawData()
	{
		return settingsModel.backgroundShowOriginal;
	}

	public float getEnergyForChannel(int channel)
	{
		if (!plot.data().hasDataSet()) return 0.0f;
		return FittingSet.energyForChannel(channel, getMinEnergy(), getMaxEnergy(), plot.data().getDataSet().channelsPerScan());
	}

	public Pair<Float, Float> getValueForChannel(int channel)
	{
		if (channel == -1) return null;
		if (channel >= plot.data().getDataSet().channelsPerScan()) return null;

		Pair<ReadOnlySpectrum, ReadOnlySpectrum> scans = plot.getDataForPlot();
		if (scans == null) return new Pair<Float, Float>(0.0f, 0.0f);

		return new Pair<Float, Float>(scans.first.get(channel), scans.second.get(channel));
	}


	public EscapePeakType getEscapePeakType()
	{
		return settingsModel.escape;
	}
	
	public void setEscapePeakType(EscapePeakType type)
	{
		plot.fitting().setEscapeType(type);
		settingsModel.escape = type;
	}
	
	public boolean getLockPlotHeight() {
		return settingsModel.lockPlotHeight;
	}
	public void setLockPlotHeight(boolean lock) {
		settingsModel.lockPlotHeight = lock;
		updateListeners();
	}
	
	
}
