package peakaboo.controller.plotter;

import eventful.IEventfulType;


public interface SettingsController extends IEventfulType<String>
{

	public void setZoom(float zoom);
	public float getZoom();
	
	public void setShowIndividualSelections(boolean showIndividualSelections);
	public boolean getShowIndividualSelections();

	public void setEnergyPerChannel(float energy);
	public float getEnergyPerChannel();
	public void setMaxEnergy(float energy);
	public float getMaxEnergy();

	public void setViewLog(boolean log);
	public boolean getViewLog();

	public void setShowChannelAverage();
	public void setShowChannelMaximum();
	public void setShowChannelSingle();
	public ChannelCompositeMode getChannelCompositeType();
	
	public void setScanNumber(int number);
	public int getScanNumber();

	public void setShowAxes(boolean axes);
	public boolean getShowAxes();

	public boolean getShowTitle();
	public void setShowTitle(boolean show);

	public void setMonochrome(boolean mono);
	public boolean getMonochrome();

	public void setShowElementTitles(boolean show);
	public boolean getShowElementTitles();

	public void setShowElementMarkers(boolean show);
	public boolean getShowElementMarkers();

	public void setShowElementIntensities(boolean show);
	public boolean getShowElementIntensities();

	public void setShowRawData(boolean show);
	public boolean getShowRawData();

	
}
