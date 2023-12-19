package org.peakaboo.controller.plotter.view;


public class SessionViewModel {

	public int					scanNumber;
	public ChannelCompositeMode	channelComposite;
	public boolean				backgroundShowOriginal;
	public float				zoom;
	public boolean				logTransform;
	public boolean				showTitle;
	public boolean				darkMode;
	
	public SessionViewModel() {

		scanNumber = 0;
		channelComposite = ChannelCompositeMode.AVERAGE;
		backgroundShowOriginal = false;
		zoom = 1.0f;
		logTransform = true;
		showTitle = false;
		darkMode = false;
		
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


	public void copy(SessionViewModel view) {
		this.scanNumber = view.scanNumber;
		this.channelComposite = view.channelComposite;
		this.backgroundShowOriginal = view.backgroundShowOriginal;
		this.zoom = view.zoom;
		this.logTransform = view.logTransform;
		this.showTitle = view.showTitle;
		this.darkMode = view.darkMode;
	}
	
}
