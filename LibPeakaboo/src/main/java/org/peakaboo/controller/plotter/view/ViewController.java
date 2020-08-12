package org.peakaboo.controller.plotter.view;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.PlotController.PlotSpectra;
import org.peakaboo.controller.settings.Settings;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.display.plot.PlotSettings;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.eventful.Eventful;


public class ViewController extends Eventful
{

	
	private SessionViewModel viewModel;
	private PlotController plot;
	
	private static final String SETTING_MONOCHROME = "org.peakaboo.controller.plotter.view.monochrome";
	private static final String SETTING_CONSTSCALE = "org.peakaboo.controller.plotter.view.constantscale";
	private static final String SETTING_FITINTENSITY = "org.peakaboo.controller.plotter.view.fit.intensity";
	private static final String SETTING_FITMARKERS = "org.peakaboo.controller.plotter.view.fit.markers";
	private static final String SETTING_FITINDIVIDUAL = "org.peakaboo.controller.plotter.view.fit.individual";
	
	public ViewController(PlotController plotController)
	{
		this.plot = plotController;
		viewModel = new SessionViewModel();
	}

	public SessionViewModel getViewModel() {
		return viewModel;
	}

	private void setUndoPoint(String change)
	{
		plot.history().setUndoPoint(change);
	}
	

	public float getZoom()
	{
		return viewModel.zoom;
	}

	public void setZoom(float zoom)
	{
		viewModel.zoom = zoom;
		updateListeners();
	}

	public void setShowIndividualSelections(boolean show) {
		Settings.provider().setBoolean(SETTING_FITINDIVIDUAL, show);
		setUndoPoint("Individual Fittings");
		updateListeners();
	}

	public boolean getShowIndividualSelections() {
		return Settings.provider().getBoolean(SETTING_FITINDIVIDUAL, false);
	}

	
	public void setViewLog(boolean log)
	{
		viewModel.logTransform = log;
		setUndoPoint("Log View");
		updateListeners();
	}

	public boolean getViewLog()
	{
		return viewModel.logTransform;
	}

	public void setChannelCompositeMode(ChannelCompositeMode mode)
	{
		viewModel.channelComposite = mode;
		setUndoPoint(mode.show());
		plot.filtering().filteredDataInvalidated();
	}
	

	public ChannelCompositeMode getChannelCompositeMode()
	{
		return viewModel.channelComposite;
	}

	public void setScanNumber(int number)
	{
		//negative is downwards, positive is upwards
		int direction = number - viewModel.scanNumber;

		if (direction > 0)
		{
			number = plot.data().getDataSet().getScanData().firstNonNullScanIndex(number);
		}
		else
		{
			number = plot.data().getDataSet().getScanData().lastNonNullScanIndex(number);
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
		viewModel.scanNumber = number;
		plot.filtering().filteredDataInvalidated();
	}

	public int getScanNumber()
	{
		return viewModel.scanNumber;
	}

	public void setMonochrome(boolean mono) {
		Settings.provider().setBoolean(SETTING_MONOCHROME, mono);
		setUndoPoint("Monochrome");
		updateListeners();
	}

	public boolean getMonochrome() 	{
		return Settings.provider().getBoolean(SETTING_MONOCHROME, false);
	}
	
	public void setConsistentScale(Boolean consistent) {
		Settings.provider().setBoolean(SETTING_CONSTSCALE, consistent);
		setUndoPoint("Consistent Scale");
		updateListeners();
	}
	
	public boolean getConsistentScale() {
		return Settings.provider().getBoolean(SETTING_CONSTSCALE, true);
	}

	public void setShowElementMarkers(boolean show) {
		Settings.provider().setBoolean(SETTING_FITMARKERS, show);
		setUndoPoint("Fitting Markers");
		updateListeners();
	}

	public void setShowElementIntensities(boolean show) {
		Settings.provider().setBoolean(SETTING_FITINTENSITY, show);
		setUndoPoint("Fitting Heights");
		updateListeners();
	}

	public boolean getShowElementMarkers() {
		return Settings.provider().getBoolean(SETTING_FITMARKERS, true);
	}

	public boolean getShowElementIntensities() {
		return Settings.provider().getBoolean(SETTING_FITINTENSITY, false);
	}

	public void setShowRawData(boolean show)
	{
		viewModel.backgroundShowOriginal = show;
		setUndoPoint("Raw Data Outline");
		updateListeners();
	}

	public boolean getShowRawData()
	{
		return viewModel.backgroundShowOriginal;
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
		if (scans == null) return new Pair<>(0.0f, 0.0f);

		return new Pair<>(scans.filtered.get(channel), scans.raw.get(channel));
	}

	
	public boolean getLockPlotHeight() {
		return viewModel.lockPlotHeight;
	}
	public void setLockPlotHeight(boolean lock) {
		viewModel.lockPlotHeight = lock;
		updateListeners();
	}
	
	
	public boolean getShowTitle() {
		return viewModel.showTitle;
	}
	
	public void setShowTitle(boolean showTitle) {
		viewModel.showTitle = showTitle;
		updateListeners();
	}
	

	public PlotSettings getPlotSettings() {
		
		PlotSettings settings = new PlotSettings();
		
		settings.backgroundShowOriginal = getShowRawData();
		settings.monochrome = getMonochrome();
		settings.showElementFitIntensities = getShowElementIntensities();
		settings.showElementFitMarkers = getShowElementMarkers();
		settings.showIndividualFittings = getShowIndividualSelections();
		settings.logTransform = getViewLog();
		settings.title = getShowTitle()? plot.data().getTitle() : null;
		
		return settings;
	}





	
}
