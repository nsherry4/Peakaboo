package peakaboo.controller.plotter;

import java.io.Serializable;

import peakaboo.curvefit.fitting.EscapePeakType;



public class PlotViewOptions implements Serializable{
	
	public int scanNumber;
	public boolean showIndividualFittings;
	public boolean showElementFitTitles;
	public boolean showElementFitMarkers;
	public boolean showElementFitIntensities;
	public boolean showPlotTitle;
	public ChannelCompositeMode channelComposite;
	public boolean backgroundShowOriginal;
	public float zoom;
	public boolean monochrome;
	public boolean showAxes;
	
	public EscapePeakType escape;
	
	public PlotViewOptions(){
		scanNumber = 0;
		showIndividualFittings = false;
		channelComposite = ChannelCompositeMode.NONE;		
		backgroundShowOriginal = false;
		zoom = 1.0f;
		monochrome = false;
		showAxes = false;
		escape = EscapePeakType.SILICON;
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
