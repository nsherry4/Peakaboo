package org.peakaboo.controller.plotter.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.PlotController.PlotSpectra;
import org.peakaboo.controller.settings.SavedPersistence;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.display.plot.PlotSettings;

import cyclops.Pair;
import eventful.Eventful;


public class ViewController extends Eventful
{

	
	private ViewModel viewModel;
	private PlotController plot;
	
	public ViewController(PlotController plotController)
	{
		this.plot = plotController;
		viewModel = new ViewModel();
	}

	public ViewModel getViewModel()
	{
		return viewModel;
	}

	private void setUndoPoint(String change)
	{
		plot.history().setUndoPoint(change);
	}
	

	public float getZoom()
	{
		return viewModel.session.zoom;
	}

	public void setZoom(float zoom)
	{
		viewModel.session.zoom = zoom;
		updateListeners();
	}

	public void setShowIndividualSelections(boolean showIndividualSelections)
	{
		viewModel.persistent.showIndividualFittings = showIndividualSelections;
		savePersistentSettings();
		setUndoPoint("Individual Fittings");
		updateListeners();
	}

	public boolean getShowIndividualSelections()
	{
		return viewModel.persistent.showIndividualFittings;
	}

	
	public void setViewLog(boolean log)
	{
		viewModel.session.logTransform = log;
		setUndoPoint("Log View");
		updateListeners();
	}

	public boolean getViewLog()
	{
		return viewModel.session.logTransform;
	}

	public void setChannelCompositeMode(ChannelCompositeMode mode)
	{
		viewModel.session.channelComposite = mode;
		setUndoPoint(mode.show());
		plot.filtering().filteredDataInvalidated();
	}
	

	public ChannelCompositeMode getChannelCompositeMode()
	{
		return viewModel.session.channelComposite;
	}

	public void setScanNumber(int number)
	{
		//negative is downwards, positive is upwards
		int direction = number - viewModel.session.scanNumber;

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
		viewModel.session.scanNumber = number;
		plot.filtering().filteredDataInvalidated();
	}

	public int getScanNumber()
	{
		return viewModel.session.scanNumber;
	}

	public void setMonochrome(boolean mono)
	{
		viewModel.persistent.monochrome = mono;
		savePersistentSettings();
		setUndoPoint("Monochrome");
		updateListeners();
	}

	public boolean getMonochrome()
	{
		return viewModel.persistent.monochrome;
	}
	
	public void setConsistentScale(Boolean consistent) {
		viewModel.persistent.consistentScale = consistent;
		savePersistentSettings();
		setUndoPoint("Consistent Scale");
		updateListeners();
	}
	
	public boolean getConsistentScale() {
		return viewModel.persistent.consistentScale;
	}

	public void setShowElementMarkers(boolean show)
	{
		viewModel.persistent.showElementFitMarkers = show;
		savePersistentSettings();
		setUndoPoint("Fitting Markers");
		updateListeners();
	}

	public void setShowElementIntensities(boolean show)
	{
		viewModel.persistent.showElementFitIntensities = show;
		savePersistentSettings();
		setUndoPoint("Fitting Heights");
		updateListeners();
	}

	public boolean getShowElementMarkers()
	{
		return viewModel.persistent.showElementFitMarkers;
	}

	public boolean getShowElementIntensities()
	{
		return viewModel.persistent.showElementFitIntensities;
	}

	public void setShowRawData(boolean show)
	{
		viewModel.session.backgroundShowOriginal = show;
		setUndoPoint("Raw Data Outline");
		updateListeners();
	}

	public boolean getShowRawData()
	{
		return viewModel.session.backgroundShowOriginal;
	}
	
	public float getEnergyForChannel(int channel)
	{
		if (!plot.data().hasDataSet()) return 0.0f;
		EnergyCalibration calibration = new EnergyCalibration(
				plot.fitting().getMinEnergy(), 
				plot.fitting().getMaxEnergy(), 
				plot.data().getDataSet().getAnalysis().channelsPerScan()
			);
		return calibration.energyFromChannel(channel);
	}

	public Pair<Float, Float> getValueForChannel(int channel)
	{
		if (channel == -1) return null;
		if (channel >= plot.data().getDataSet().getAnalysis().channelsPerScan()) return null;

		PlotSpectra scans = plot.getDataForPlot();
		if (scans == null) return new Pair<Float, Float>(0.0f, 0.0f);

		return new Pair<Float, Float>(scans.filtered.get(channel), scans.raw.get(channel));
	}

	
	public boolean getLockPlotHeight() {
		return viewModel.session.lockPlotHeight;
	}
	public void setLockPlotHeight(boolean lock) {
		viewModel.session.lockPlotHeight = lock;
		updateListeners();
	}
	
	

	
	/**
	 * This should really only be called at creation time, since it loads settings 
	 * from disk and does not create an undo point.
	 */
	public void loadPersistentSettings() {
		File file = new File(plot.getConfigDir() + "/settings.yaml");
		if (!file.exists()) {
			savePersistentSettings();
		}
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
		File file = new File(plot.getConfigDir() + "/settings.yaml");
		try {
			
			SavedPersistence saved = SavedPersistence.storeFrom(plot);
			String yaml = saved.serialize();
			byte[] bytes = yaml.getBytes();
			Files.write(file.toPath(), bytes);
			
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Could not save persistent settings", e);
		}
	}

	public PlotSettings getPlotSettings() {
		
		PlotSettings settings = new PlotSettings();
		
		settings.backgroundShowOriginal = getShowRawData();
		settings.monochrome = getMonochrome();
		settings.showElementFitIntensities = getShowElementIntensities();
		settings.showElementFitMarkers = getShowElementMarkers();
		settings.showIndividualFittings = getShowIndividualSelections();
		settings.logTransform = getViewLog();
		
		return settings;
	}





	
}
