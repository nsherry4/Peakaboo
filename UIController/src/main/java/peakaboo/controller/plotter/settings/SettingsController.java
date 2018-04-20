package peakaboo.controller.plotter.settings;

import eventful.Eventful;
import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.curvefit.transition.EscapePeakType;
import scidraw.drawing.ViewTransform;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;


public class SettingsController extends Eventful
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
		return settingsModel.session.zoom;
	}

	public void setZoom(float zoom)
	{
		settingsModel.session.zoom = zoom;
		updateListeners();
	}

	public void setShowIndividualSelections(boolean showIndividualSelections)
	{
		settingsModel.persistent.showIndividualFittings = showIndividualSelections;
		setUndoPoint("Individual Fittings");
		plot.fitting().fittingDataInvalidated();
	}

	public boolean getShowIndividualSelections()
	{
		return settingsModel.persistent.showIndividualFittings;
	}


	public void setMaxEnergy(float max) {
		settingsModel.session.maxEnergy = max;
		int dataWidth = plot.data().getDataSet().getAnalysis().channelsPerScan();
		plot.fitting().setFittingParameters(dataWidth, getMinEnergy(), max);

		updateListeners();
	}

	public float getMaxEnergy()
	{
		return settingsModel.session.maxEnergy;
	}

	
	public void setMinEnergy(float min) {
		settingsModel.session.minEnergy = min;
		int dataWidth = plot.data().getDataSet().getAnalysis().channelsPerScan();
		plot.fitting().setFittingParameters(dataWidth, min, getMaxEnergy());
		updateListeners();
	}

	
	public float getMinEnergy()
	{
		return settingsModel.session.minEnergy;
	}
	
	public void setViewLog(boolean log)
	{
		if (log)
		{
			settingsModel.session.viewTransform = ViewTransform.LOG;
		}
		else
		{
			settingsModel.session.viewTransform = ViewTransform.LINEAR;
		}
		setUndoPoint("Log View");
		updateListeners();
	}

	public boolean getViewLog()
	{
		return settingsModel.session.viewTransform == ViewTransform.LOG;
	}

	public void setChannelCompositeMode(ChannelCompositeMode mode)
	{
		settingsModel.session.channelComposite = mode;
		setUndoPoint(mode.show());
		plot.filtering().filteredDataInvalidated();
	}
	

	public ChannelCompositeMode getChannelCompositeMode()
	{
		return settingsModel.session.channelComposite;
	}

	public void setScanNumber(int number)
	{
		//negative is downwards, positive is upwards
		int direction = number - settingsModel.session.scanNumber;

		if (direction > 0)
		{
			number = plot.data().getDataSet().getAnalysis().firstNonNullScanIndex(number);
		}
		else
		{
			number = plot.data().getDataSet().getAnalysis().lastNonNullScanIndex(number);
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
		settingsModel.session.scanNumber = number;
		plot.filtering().filteredDataInvalidated();
	}

	public int getScanNumber()
	{
		return settingsModel.session.scanNumber;
	}

	public void setShowAxes(boolean axes)
	{
		settingsModel.persistent.showAxes = axes;
		plot.setAxisPainters(null);
		setUndoPoint("Axes");
		updateListeners();
	}

	public boolean getShowAxes()
	{
		return settingsModel.persistent.showAxes;
	}

	public boolean getShowTitle()
	{
		return settingsModel.persistent.showPlotTitle;
	}

	public void setShowTitle(boolean show)
	{
		settingsModel.persistent.showPlotTitle = show;
		plot.setAxisPainters(null);
		setUndoPoint("Title");
		updateListeners();
	}

	public void setMonochrome(boolean mono)
	{
		settingsModel.persistent.monochrome = mono;
		setUndoPoint("Monochrome");
		updateListeners();
	}

	public boolean getMonochrome()
	{
		return settingsModel.persistent.monochrome;
	}

	public void setShowElementTitles(boolean show)
	{
		settingsModel.persistent.showElementFitTitles = show;
		setUndoPoint("Fitting Titles");
		updateListeners();
	}

	public void setShowElementMarkers(boolean show)
	{
		settingsModel.persistent.showElementFitMarkers = show;
		setUndoPoint("Fitting Markers");
		updateListeners();
	}

	public void setShowElementIntensities(boolean show)
	{
		settingsModel.persistent.showElementFitIntensities = show;
		setUndoPoint("Fitting Heights");
		updateListeners();
	}

	public boolean getShowElementTitles()
	{
		return settingsModel.persistent.showElementFitTitles;
	}

	public boolean getShowElementMarkers()
	{
		return settingsModel.persistent.showElementFitMarkers;
	}

	public boolean getShowElementIntensities()
	{
		return settingsModel.persistent.showElementFitIntensities;
	}

	public void setShowRawData(boolean show)
	{
		settingsModel.session.backgroundShowOriginal = show;
		setUndoPoint("Raw Data Outline");
		updateListeners();
	}

	public boolean getShowRawData()
	{
		return settingsModel.session.backgroundShowOriginal;
	}

	public float getEnergyForChannel(int channel)
	{
		if (!plot.data().hasDataSet()) return 0.0f;
		EnergyCalibration calibration = new EnergyCalibration(getMinEnergy(), getMaxEnergy(), plot.data().getDataSet().getAnalysis().channelsPerScan());
		return calibration.energyFromChannel(channel);
	}

	public Pair<Float, Float> getValueForChannel(int channel)
	{
		if (channel == -1) return null;
		if (channel >= plot.data().getDataSet().getAnalysis().channelsPerScan()) return null;

		Pair<ReadOnlySpectrum, ReadOnlySpectrum> scans = plot.getDataForPlot();
		if (scans == null) return new Pair<Float, Float>(0.0f, 0.0f);

		return new Pair<Float, Float>(scans.first.get(channel), scans.second.get(channel));
	}


	public EscapePeakType getEscapePeakType()
	{
		return settingsModel.session.escape;
	}
	
	public void setEscapePeakType(EscapePeakType type)
	{
		plot.fitting().setEscapeType(type);
		settingsModel.session.escape = type;
	}
	
	public boolean getLockPlotHeight() {
		return settingsModel.session.lockPlotHeight;
	}
	public void setLockPlotHeight(boolean lock) {
		settingsModel.session.lockPlotHeight = lock;
		updateListeners();
	}
	
	
}
