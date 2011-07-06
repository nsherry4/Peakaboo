package peakaboo.controller.plotter.settings;

import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.fitting.EscapePeakType;
import scidraw.drawing.ViewTransform;
import scitypes.Spectrum;
import eventful.Eventful;
import fava.datatypes.Pair;


public class SettingsController extends Eventful implements ISettingsController
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
		plot.undoController.setUndoPoint(change);
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
		plot.fittingController.fittingDataInvalidated();
	}

	public boolean getShowIndividualSelections()
	{
		return settingsModel.showIndividualFittings;
	}

	public void setEnergyPerChannel(float energy)
	{
		if (!plot.dataController.hasDataSet() || plot.dataController.datasetScanSize() == 0)
		{
			return;
		}
		
		plot.fittingController.setFittingParameters(energy);
		updateListeners();
	}

	public float getEnergyPerChannel()
	{
		return plot.dr.unitSize;
	}

	public void setMaxEnergy(float energy)
	{
		if (!plot.dataController.hasDataSet() || plot.dataController.datasetScanSize() == 0)
		{
			return;
		}
		//dont set an undo point here -- setEnergyPerChannel does that already
		setEnergyPerChannel(energy / (plot.dataController.datasetScanSize()));

	}

	public float getMaxEnergy()
	{
		if (!plot.dataController.hasDataSet() || plot.dataController.datasetScanSize() == 0)
		{
			return 20.48f;
		}
		return plot.dr.unitSize * (plot.dataController.datasetScanSize());
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

	public void setShowChannelAverage()
	{
		settingsModel.channelComposite = ChannelCompositeMode.AVERAGE;
		setUndoPoint("Mean Spectrum");
		plot.filteringController.filteredDataInvalidated();
	}

	public void setShowChannelMaximum()
	{
		settingsModel.channelComposite = ChannelCompositeMode.MAXIMUM;
		setUndoPoint("Max Spectrum");
		plot.filteringController.filteredDataInvalidated();
	}

	public void setShowChannelSingle()
	{
		settingsModel.channelComposite = ChannelCompositeMode.NONE;
		setUndoPoint("Individual Spectrum");
		plot.filteringController.filteredDataInvalidated();
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
			number = plot.dataController.firstNonNullScanIndex(number);
		}
		else
		{
			number = plot.dataController.lastNonNullScanIndex(number);
		}

		if (number == -1)
		{
			updateListeners();
			return;
		}

		
		if (number > plot.dataController.datasetScanCount() - 1) number = plot.dataController.datasetScanCount() - 1;
		if (number < 0) number = 0;
		settingsModel.scanNumber = number;
		plot.filteringController.filteredDataInvalidated();
	}

	public int getScanNumber()
	{
		return settingsModel.scanNumber;
	}

	public void setShowAxes(boolean axes)
	{
		settingsModel.showAxes = axes;
		plot.axisPainters = null;
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
		plot.axisPainters = null;
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
		if (!plot.dataController.hasDataSet()) return 0.0f;
		return channel * plot.dr.unitSize;
	}

	public Pair<Float, Float> getValueForChannel(int channel)
	{
		if (channel == -1) return null;
		if (channel >= plot.dataController.datasetScanSize()) return null;

		Pair<Spectrum, Spectrum> scans = plot.getDataForPlot();
		if (scans == null) return new Pair<Float, Float>(0.0f, 0.0f);

		return new Pair<Float, Float>(scans.first.get(channel), scans.second.get(channel));
	}

	
	public EscapePeakType getEscapePeakType()
	{
		return settingsModel.escape;
	}
	public void setEscapePeakType(EscapePeakType type)
	{
		plot.fittingController.setEscapeType(type);
		settingsModel.escape = type;
	}
	
	
}
