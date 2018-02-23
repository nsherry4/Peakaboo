package peakaboo.controller.plotter.settings;



import java.io.Serializable;

import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import scidraw.drawing.ViewTransform;


// Holds settings related to the way the data is presented to the user.
// This is here, rather than in the view because the drawing of the plot
// is actually executed in the controller so that it may set up the
// appropriate DrawingExtensions. Does not include information regarding
// how to draw the plot, although it does contain settings about which
// data to plot
public class SettingsModel implements Serializable
{
	
	public int					scanNumber;
	public boolean				showIndividualFittings;
	
	public boolean				showElementFitTitles;
	public boolean				showElementFitMarkers;
	public boolean				showElementFitIntensities;
	
	public boolean				showPlotTitle;
	public ChannelCompositeMode	channelComposite;
	public boolean				backgroundShowOriginal;
	public float				zoom;
	public boolean				monochrome;
	public boolean				showAxes;
	public boolean				lockPlotHeight;
	
	public EscapePeakType		escape;
	
	public ViewTransform		viewTransform;


	public SettingsModel()
	{
		scanNumber = 0;
		showIndividualFittings = false;
		channelComposite = ChannelCompositeMode.NONE;
		backgroundShowOriginal = false;
		zoom = 1.0f;
		monochrome = false;
		showAxes = false;
		lockPlotHeight = true;
		escape = EscapePeakType.SILICON;
		viewTransform = ViewTransform.LINEAR;
	}

	public void copy(SettingsModel copy)
	{
		scanNumber = copy.scanNumber;
		showIndividualFittings = copy.showIndividualFittings;
		
		showElementFitTitles = copy.showElementFitTitles;
		showElementFitMarkers = copy.showElementFitMarkers;
		showElementFitIntensities = copy.showElementFitIntensities;
		
		showPlotTitle = copy.showPlotTitle;
		channelComposite = copy.channelComposite;
		backgroundShowOriginal = copy.backgroundShowOriginal;
		zoom = copy.zoom;
		monochrome = copy.monochrome;
		showAxes = copy.showAxes;
		lockPlotHeight = copy.lockPlotHeight;
		
		escape = copy.escape;
		viewTransform = copy.viewTransform;
		
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
