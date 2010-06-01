package peakaboo.controller.plotter;

import java.io.Serializable;



public class PlotViewOptions implements Serializable{
	
	public int scanNumber;
	public boolean showIndividualFittings;
	public boolean showElementFitTitles;
	public boolean showElementFitMarkers;
	public boolean showElementFitIntensities;
	public boolean showPlotTitle;
	public ChannelCompositeMode channelComposite;
	public boolean backgroundShowOriginal;
	public double zoom;
	public boolean monochrome;
	public boolean showAxes;
	
	public PlotViewOptions(){
		scanNumber = 0;
		showIndividualFittings = false;
		channelComposite = ChannelCompositeMode.NONE;		
		backgroundShowOriginal = false;
		zoom = 1.0;
		monochrome = false;
		showAxes = false;
	}
	
}
