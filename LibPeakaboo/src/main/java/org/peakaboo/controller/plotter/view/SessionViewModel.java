package org.peakaboo.controller.plotter.view;


public class SessionViewModel {

	public int					scanNumber;
	public ChannelCompositeMode	channelComposite;
	public boolean				backgroundShowOriginal;
	public float				zoom;
	public boolean				lockPlotHeight;
	public boolean				logTransform;
	public boolean				showTitle;
	
	public SessionViewModel() {

		scanNumber = 0;
		channelComposite = ChannelCompositeMode.AVERAGE;
		backgroundShowOriginal = false;
		zoom = 1.0f;
		lockPlotHeight = true;
		logTransform = true;
		showTitle = false;
		
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
