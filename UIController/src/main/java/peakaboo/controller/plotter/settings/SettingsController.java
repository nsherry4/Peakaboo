package peakaboo.controller.plotter.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;

import commonenvironment.Env;
import eventful.Eventful;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.settings.SavedPersistence;
import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.curvefit.fitting.functions.FittingFunction;
import peakaboo.curvefit.fitting.functions.LorentzFittingFunction;
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
		savePersistentSettings();
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
		savePersistentSettings();
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
		savePersistentSettings();
		plot.setAxisPainters(null);
		setUndoPoint("Title");
		updateListeners();
	}

	public void setMonochrome(boolean mono)
	{
		settingsModel.persistent.monochrome = mono;
		savePersistentSettings();
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
		savePersistentSettings();
		setUndoPoint("Fitting Titles");
		updateListeners();
	}

	public void setShowElementMarkers(boolean show)
	{
		settingsModel.persistent.showElementFitMarkers = show;
		savePersistentSettings();
		setUndoPoint("Fitting Markers");
		updateListeners();
	}

	public void setShowElementIntensities(boolean show)
	{
		settingsModel.persistent.showElementFitIntensities = show;
		savePersistentSettings();
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
	
	public void setFWHMBase(float base) {
		plot.fitting().setFWHMBase(base);	
		settingsModel.session.fwhmBase = base;
	}
	
	public void setFWHMMult(float mult) {
		plot.fitting().setFWHMMult(mult);	
		settingsModel.session.fwhmMult = mult;
	}

	public void setFittingFunction(Class<? extends FittingFunction> cls) {
		plot.fitting().setFittingFunction(cls);
		settingsModel.session.fittingFunctionName = cls.getName();
	}
	
	
	/**
	 * This should really only be called at creation time, since it loads settings 
	 * from disk and does not create an undo point.
	 */
	public void loadPersistentSettings() {
		File file = new File(Configuration.appDir() + "/settings.yaml");
		try {
			
			byte[] bytes = Files.readAllBytes(file.toPath());
			String yaml = new String(bytes);
			SavedPersistence saved = SavedPersistence.deserialize(yaml);
			saved.loadInto(plot);
					
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Could not load persistent settings", e);
		}
		
		plot.filtering().filteredDataInvalidated();
		plot.fitting().fittingDataInvalidated();
		updateListeners();
		
	}
	
	
	private void savePersistentSettings() {
		File file = new File(Configuration.appDir() + "/settings.yaml");
		try {
			
			SavedPersistence saved = SavedPersistence.storeFrom(plot);
			String yaml = saved.serialize();
			byte[] bytes = yaml.getBytes();
			Files.write(file.toPath(), bytes);
			
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Could not save persistent settings", e);
		}
	}

	
}
