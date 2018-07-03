package peakaboo.controller.plotter.view;

import peakaboo.curvefit.peak.escape.EscapePeakType;
import scidraw.drawing.ViewTransform;

public class SessionViewModel {

	public int					scanNumber;
	public ChannelCompositeMode	channelComposite;
	public boolean				backgroundShowOriginal;
	public float				zoom;
	public boolean				lockPlotHeight;
	public EscapePeakType		escape;
	public ViewTransform		viewTransform;
	public float				minEnergy, maxEnergy;
	public float 				fwhmBase, fwhmMult;
	
	public SessionViewModel() {

		scanNumber = 0;
		channelComposite = ChannelCompositeMode.AVERAGE;
		backgroundShowOriginal = false;
		zoom = 1.0f;
		lockPlotHeight = true;
		escape = EscapePeakType.SILICON;
		viewTransform = ViewTransform.LOG;
		minEnergy = 0.0f;
		maxEnergy = 0.0f;
		fwhmBase = 0.080f;
		fwhmMult = 0.013f;
		
		
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
