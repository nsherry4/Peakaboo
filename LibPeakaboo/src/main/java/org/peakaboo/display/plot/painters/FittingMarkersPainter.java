package org.peakaboo.display.plot.painters;

import java.awt.Color;
import java.util.List;

import org.peakaboo.curvefit.curve.fitting.FittingParameters;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.ROFittingParameters;
import org.peakaboo.curvefit.peak.detector.DetectorMaterial;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.PlotPainter;


/**
 * 
 * A {@link PlotPainter} for {@link PlotDrawing}s which draws lines at the centrepoints of {@link Transition}s
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingMarkersPainter extends PlotPainter
{

	private ROFittingParameters parameters;
	private List<FittingLabel> labels;

	/**
	 * Create a FittingMarkersPainter
	 * @param fitResults the {@link FittingResultSet} for the data being drawn
	 * @param detectorMaterial the {@link DetectorMaterialType} used to generate the {@link FittingResultSet}
	 * @param c the {@link Color} to use when drawing the markings
	 */
	public FittingMarkersPainter(ROFittingParameters parameters, List<FittingLabel> labels)
	{
		this.parameters = parameters;
		this.labels = labels;
	}


	@Override
	public void drawElement(PainterData p)
	{
		//Don't draw without energy calibration
		if (parameters.getCalibration().isZero()) {
			return;
		}

		p.context.save();
		p.context.setLineWidth(1.0f);
		
		for (FittingLabel label : labels) {		
			drawTS(p, label);		
		}
		
		p.context.restore();
		
	}
	
	private void drawTS(PainterData p, FittingLabel label) {
		
		p.context.setSource(label.palette.markings);
		
		float channel, markerHeight;
		ITransitionSeries ts = label.fit.getTransitionSeries();
		
		for (Transition t : ts) {

			channel = parameters.getCalibration().fractionalChannelFromEnergy(t.energyValue);
			if (channel >= p.dr.dataWidth || channel < 0) continue;
			
			FittingFunction fitFn = parameters.forTransition(t, ts.getShell());

			
			//get a height value from the fitting function, then apply the same transformation as the fitting did
			markerHeight = fitFn.forEnergy(t.energyValue) * label.fit.getTotalScale();
						
			float positionX = getXForChannel(p, channel);
			
			markerHeight = transformValueForPlot(p.dr, markerHeight);
			
			p.context.moveTo(positionX, p.plotSize.y);
			p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
			
			DetectorMaterial detectorMaterial = parameters.getDetectorMaterial().get();
			if (detectorMaterial.hasOffset() && parameters.getShowEscapePeaks())
			{
				for (Transition esc : detectorMaterial.offset()) {
				
					float escEnergy = t.energyValue - esc.energyValue; 
					channel = parameters.getCalibration().fractionalChannelFromEnergy(escEnergy);
					if (channel < 0) continue;
					
					positionX = getXForChannel(p, channel);
					
					FittingFunction escFn = parameters.forEscape(t, esc, ts.getElement(), ts.getShell());
					markerHeight = escFn.forEnergy(escEnergy) * label.fit.getTotalScale();
					markerHeight = transformValueForPlot(p.dr, markerHeight);
					
				
					p.context.moveTo(positionX, p.plotSize.y);
					p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
					
				}
			}
		}
		
		p.context.stroke();
	}

	public float getXForChannel(PainterData p, float channel)
	{
		float channelWidth = p.plotSize.x / p.dr.dataWidth;
		return channel * channelWidth;
	}
	

}
