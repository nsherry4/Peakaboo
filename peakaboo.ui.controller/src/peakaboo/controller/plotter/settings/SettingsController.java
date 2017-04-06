package peakaboo.controller.plotter.settings;

import eventful.Eventful;
import fava.datatypes.Pair;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import scidraw.drawing.ViewTransform;
import scitypes.Spectrum;


public class SettingsController extends Eventful implements ISettingsController
{

	
	private SettingsModel settingsModel;
	private IPlotController plot;
	
	public SettingsController(IPlotController plotController)
	{
		this.plot = plotController;
		settingsModel = new SettingsModel();
	}

	@Override
	public SettingsModel getSettingsModel()
	{
		return settingsModel;
	}

	private void setUndoPoint(String change)
	{
		plot.history().setUndoPoint(change);
	}
	

	@Override
	public float getZoom()
	{
		return settingsModel.zoom;
	}

	@Override
	public void setZoom(float zoom)
	{
		settingsModel.zoom = zoom;
		updateListeners();
	}

	@Override
	public void setShowIndividualSelections(boolean showIndividualSelections)
	{
		settingsModel.showIndividualFittings = showIndividualSelections;
		setUndoPoint("Individual Fittings");
		plot.fitting().fittingDataInvalidated();
	}

	@Override
	public boolean getShowIndividualSelections()
	{
		return settingsModel.showIndividualFittings;
	}

	@Override
	public void setEnergyPerChannel(float energy)
	{
		if (!plot.data().hasDataSet() || plot.data().channelsPerScan() == 0)
		{
			return;
		}
		
		plot.fitting().setFittingParameters(energy);
		updateListeners();
	}

	@Override
	public float getEnergyPerChannel()
	{
		return plot.getDR().unitSize;
	}

	@Override
	public void setMaxEnergy(float energy)
	{
		if (!plot.data().hasDataSet() || plot.data().channelsPerScan() == 0)
		{
			return;
		}
		//dont set an undo point here -- setEnergyPerChannel does that already
		setEnergyPerChannel(energy / (plot.data().channelsPerScan()));

	}

	@Override
	public float getMaxEnergy()
	{
		if (!plot.data().hasDataSet() || plot.data().channelsPerScan() == 0)
		{
			return 20.48f;
		}
		return plot.getDR().unitSize * (plot.data().channelsPerScan());
	}

	@Override
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

	@Override
	public boolean getViewLog()
	{
		return settingsModel.viewTransform == ViewTransform.LOG;
	}

	@Override
	public void setShowChannelMode(ChannelCompositeMode mode)
	{
		settingsModel.channelComposite = mode;
		setUndoPoint(mode.show());
		plot.filtering().filteredDataInvalidated();
	}
	

	@Override
	public ChannelCompositeMode getChannelCompositeType()
	{
		return settingsModel.channelComposite;
	}

	@Override
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

	@Override
	public int getScanNumber()
	{
		return settingsModel.scanNumber;
	}

	@Override
	public void setShowAxes(boolean axes)
	{
		settingsModel.showAxes = axes;
		plot.setAxisPainters(null);
		setUndoPoint("Axes");
		updateListeners();
	}

	@Override
	public boolean getShowAxes()
	{
		return settingsModel.showAxes;
	}

	@Override
	public boolean getShowTitle()
	{
		return settingsModel.showPlotTitle;
	}

	@Override
	public void setShowTitle(boolean show)
	{
		settingsModel.showPlotTitle = show;
		plot.setAxisPainters(null);
		setUndoPoint("Title");
		updateListeners();
	}

	@Override
	public void setMonochrome(boolean mono)
	{
		settingsModel.monochrome = mono;
		setUndoPoint("Monochrome");
		updateListeners();
	}

	@Override
	public boolean getMonochrome()
	{
		return settingsModel.monochrome;
	}

	@Override
	public void setShowElementTitles(boolean show)
	{
		settingsModel.showElementFitTitles = show;
		setUndoPoint("Fitting Titles");
		updateListeners();
	}

	@Override
	public void setShowElementMarkers(boolean show)
	{
		settingsModel.showElementFitMarkers = show;
		setUndoPoint("Fitting Markers");
		updateListeners();
	}

	@Override
	public void setShowElementIntensities(boolean show)
	{
		settingsModel.showElementFitIntensities = show;
		setUndoPoint("Fitting Heights");
		updateListeners();
	}

	@Override
	public boolean getShowElementTitles()
	{
		return settingsModel.showElementFitTitles;
	}

	@Override
	public boolean getShowElementMarkers()
	{
		return settingsModel.showElementFitMarkers;
	}

	@Override
	public boolean getShowElementIntensities()
	{
		return settingsModel.showElementFitIntensities;
	}

	@Override
	public void setShowRawData(boolean show)
	{
		settingsModel.backgroundShowOriginal = show;
		setUndoPoint("Raw Data Outline");
		updateListeners();
	}

	@Override
	public boolean getShowRawData()
	{
		return settingsModel.backgroundShowOriginal;
	}

	@Override
	public float getEnergyForChannel(int channel)
	{
		if (!plot.data().hasDataSet()) return 0.0f;
		return channel * plot.getDR().unitSize;
	}

	@Override
	public Pair<Float, Float> getValueForChannel(int channel)
	{
		if (channel == -1) return null;
		if (channel >= plot.data().channelsPerScan()) return null;

		Pair<Spectrum, Spectrum> scans = plot.getDataForPlot();
		if (scans == null) return new Pair<Float, Float>(0.0f, 0.0f);

		return new Pair<Float, Float>(scans.first.get(channel), scans.second.get(channel));
	}


	@Override
	public EscapePeakType getEscapePeakType()
	{
		return settingsModel.escape;
	}
	
	@Override
	public void setEscapePeakType(EscapePeakType type)
	{
		plot.fitting().setEscapeType(type);
		settingsModel.escape = type;
	}
	
	
}
