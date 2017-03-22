package peakaboo.controller.plotter.settings;

import eventful.IEventful;
import fava.datatypes.Pair;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;


public interface ISettingsController extends IEventful
{

	void setZoom(float zoom);
	float getZoom();
	
	void setShowIndividualSelections(boolean showIndividualSelections);
	boolean getShowIndividualSelections();

	void setEnergyPerChannel(float energy);
	float getEnergyPerChannel();
	void setMaxEnergy(float energy);
	float getMaxEnergy();

	void setViewLog(boolean log);
	boolean getViewLog();

	void setShowChannelMode(ChannelCompositeMode mode);
	ChannelCompositeMode getChannelCompositeType();
	
	void setScanNumber(int number);
	int getScanNumber();

	void setShowAxes(boolean axes);
	boolean getShowAxes();

	boolean getShowTitle();
	void setShowTitle(boolean show);

	void setMonochrome(boolean mono);
	boolean getMonochrome();

	void setShowElementTitles(boolean show);
	boolean getShowElementTitles();

	void setShowElementMarkers(boolean show);
	boolean getShowElementMarkers();

	void setShowElementIntensities(boolean show);
	boolean getShowElementIntensities();

	void setShowRawData(boolean show);
	boolean getShowRawData();
	
	
	float getEnergyForChannel(int channel);
	Pair<Float, Float> getValueForChannel(int channel);

	
	EscapePeakType getEscapePeakType();
	void setEscapePeakType(EscapePeakType type);
	SettingsModel getSettingsModel();
	
}
