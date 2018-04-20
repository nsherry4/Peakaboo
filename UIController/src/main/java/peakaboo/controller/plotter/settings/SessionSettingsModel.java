package peakaboo.controller.plotter.settings;

import peakaboo.curvefit.transition.EscapePeakType;
import scidraw.drawing.ViewTransform;

public class SessionSettingsModel {

	public int					scanNumber;
	public ChannelCompositeMode	channelComposite;
	public boolean				backgroundShowOriginal;
	public float				zoom;
	public boolean				lockPlotHeight;
	public EscapePeakType		escape;
	public ViewTransform		viewTransform;
	public float				minEnergy, maxEnergy;
	
	public SessionSettingsModel() {

		scanNumber = 0;
		channelComposite = ChannelCompositeMode.AVERAGE;
		backgroundShowOriginal = false;
		zoom = 1.0f;
		lockPlotHeight = true;
		escape = EscapePeakType.SILICON;
		viewTransform = ViewTransform.LOG;
		minEnergy = 0.0f;
		maxEnergy = 0.0f;
		
	}
	
	public SessionSettingsModel(SessionSettingsModel copy) {
		scanNumber = copy.scanNumber;
		
		channelComposite = copy.channelComposite;
		backgroundShowOriginal = copy.backgroundShowOriginal;
		zoom = copy.zoom;
		lockPlotHeight = copy.lockPlotHeight;
		
		escape = copy.escape;
		viewTransform = copy.viewTransform;
		
		minEnergy = copy.minEnergy;
		maxEnergy = copy.maxEnergy;
	}
	
	//For JYAML Serialization Purposes -- Needs this to handle enums
	public String getChannelComposite()
	{
		return channelComposite.name();
	}


	public void setChannelComposite(String channelComposite)
	{
		this.channelComposite = ChannelCompositeMode.valueOf(channelComposite);
	}
	
}
