package peakaboo.controller.plotter.view;

import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
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
	public String 				fittingFunctionName;
	
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
		fittingFunctionName = PseudoVoigtFittingFunction.class.getName();
		
		
	}
	
	
	public SessionViewModel(SessionViewModel copy) {
		scanNumber = copy.scanNumber;
		
		channelComposite = copy.channelComposite;
		backgroundShowOriginal = copy.backgroundShowOriginal;
		zoom = copy.zoom;
		lockPlotHeight = copy.lockPlotHeight;
		
		escape = copy.escape;
		viewTransform = copy.viewTransform;
		
		minEnergy = copy.minEnergy;
		maxEnergy = copy.maxEnergy;
		
		fwhmBase = copy.fwhmBase;
		fwhmMult = copy.fwhmMult;
		fittingFunctionName = copy.fittingFunctionName;
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
