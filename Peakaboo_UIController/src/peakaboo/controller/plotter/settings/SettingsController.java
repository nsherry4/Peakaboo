package peakaboo.controller.plotter.settings;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.PlotController.UpdateType;
import peakaboo.curvefit.fitting.EscapePeakType;
import scidraw.drawing.ViewTransform;
import scitypes.Spectrum;
import eventful.Eventful;
import fava.datatypes.Pair;


public class SettingsController extends Eventful implements ISettingsController
{

	
	private SettingsModel settingsModel;
	private PlotController plotController;
	
	public SettingsController(PlotController plotController)
	{
		this.plotController = plotController;
		settingsModel = new SettingsModel();
	}
	
	public SettingsModel getSettingsModel()
	{
		return settingsModel;
	}
	
	private void setUndoPoint(String change)
	{
		plotController.undoController.setUndoPoint(change);
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
		plotController.fittingController.fittingDataInvalidated();
	}

	public boolean getShowIndividualSelections()
	{
		return settingsModel.showIndividualFittings;
	}

	public void setEnergyPerChannel(float energy)
	{
		//dont call setUndoPoint, as setFittingParameters will do that for us
		plotController.fittingController.setFittingParameters(energy);
		plotController.axisSetInvalidated();
	}

	public float getEnergyPerChannel()
	{
		return plotController.dr.unitSize;
	}

	public void setMaxEnergy(float energy)
	{
		if (!plotController.dataController.hasDataSet() || plotController.dataController.datasetScanSize() == 0)
		{
			return;
		}
		//dont set an undo point here -- setEnergyPerChannel does that already
		setEnergyPerChannel(energy / (plotController.dataController.datasetScanSize()));

	}

	public float getMaxEnergy()
	{
		if (!plotController.dataController.hasDataSet() || plotController.dataController.datasetScanSize() == 0)
		{
			return 20.48f;
		}
		return plotController.dr.unitSize * (plotController.dataController.datasetScanSize());
	}

	public void setViewLog(boolean log)
	{
		if (log)
		{
			plotController.dr.viewTransform = ViewTransform.LOG;
		}
		else
		{
			plotController.dr.viewTransform = ViewTransform.LINEAR;
		}
		plotController.axisSetInvalidated();
		setUndoPoint("Log View");
		updateListeners();
	}

	public boolean getViewLog()
	{
		return (plotController.dr.viewTransform == ViewTransform.LOG);
	}

	public void setShowChannelAverage()
	{
		settingsModel.channelComposite = ChannelCompositeMode.AVERAGE;
		setUndoPoint("Mean Spectrum");
		plotController.filteringController.filteredDataInvalidated();
	}

	public void setShowChannelMaximum()
	{
		settingsModel.channelComposite = ChannelCompositeMode.MAXIMUM;
		setUndoPoint("Max Spectrum");
		plotController.filteringController.filteredDataInvalidated();
	}

	public void setShowChannelSingle()
	{
		settingsModel.channelComposite = ChannelCompositeMode.NONE;
		setUndoPoint("Individual Spectrum");
		plotController.filteringController.filteredDataInvalidated();
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
			number = plotController.dataController.firstNonNullScanIndex(number);
		}
		else
		{
			number = plotController.dataController.lastNonNullScanIndex(number);
		}

		if (number == -1)
		{
			updateListeners();
			return;
		}

		
		if (number > plotController.dataController.datasetScanCount() - 1) number = plotController.dataController.datasetScanCount() - 1;
		if (number < 0) number = 0;
		settingsModel.scanNumber = number;
		plotController.filteringController.filteredDataInvalidated();
	}

	public int getScanNumber()
	{
		return settingsModel.scanNumber;
	}

	public void setShowAxes(boolean axes)
	{
		settingsModel.showAxes = axes;
		plotController.axisPainters = null;
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
		plotController.axisPainters = null;
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
		if (!plotController.dataController.hasDataSet()) return 0.0f;
		return channel * plotController.dr.unitSize;
	}

	public Pair<Float, Float> getValueForChannel(int channel)
	{
		if (channel == -1) return null;
		if (channel >= plotController.dataController.datasetScanSize()) return null;

		Pair<Spectrum, Spectrum> scans = plotController.getDataForPlot();
		if (scans == null) return new Pair<Float, Float>(0.0f, 0.0f);

		return new Pair<Float, Float>(scans.first.get(channel), scans.second.get(channel));
	}

	
	public EscapePeakType getEscapePeakType()
	{
		return settingsModel.escape;
	}
	public void setEscapePeakType(EscapePeakType type)
	{
		plotController.fittingController.setEscapeType(type);
		settingsModel.escape = type;
	}
	
	
}
