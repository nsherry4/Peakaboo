package peakaboo.display.plot.painters;

import java.util.List;

import cyclops.ISpectrum;
import cyclops.Spectrum;
import cyclops.visualization.drawing.DrawingRequest;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.plot.painters.PlotPainter;
import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.fitting.FittingFunction;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;


/**
 * 
 * A {@link PlotPainter} for {@link PlotDrawing}s which draws lines at the centrepoints of {@link Transition}s
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingMarkersPainter extends PlotPainter
{

	private FittingParameters 	parameters;
	private List<FittingLabel> labels;
	private EscapePeakType		escapeType;

	/**
	 * Create a FittingMarkersPainter
	 * @param fitResults the {@link FittingResultSet} for the data being drawn
	 * @param escapeType the {@link EscapePeakType} used to generate the {@link FittingResultSet}
	 * @param c the {@link Color} to use when drawing the markings
	 */
	public FittingMarkersPainter(FittingParameters parameters, List<FittingLabel> labels, EscapePeakType escapeType)
	{
		this.parameters = parameters;
		this.labels = labels;
		this.escapeType = escapeType;
	}


	@Override
	public void drawElement(PainterData p)
	{
		//Don't draw without energy calibration
		if (parameters.getCalibration().isZero()) {
			return;
		}
		
		DrawingRequest dr = p.dr;
		float channel, markerHeight;
		Spectrum markerHeights = new ISpectrum(dr.dataWidth);

		p.context.save();
		p.context.setLineWidth(1.0f);
		
		
		for (FittingLabel label : labels) {

			p.context.setSource(label.palette.markings);	
			for (int i = 0; i < p.dr.dataWidth; i++) {
				markerHeights.set(i, 0.0f);
			}

			TransitionSeries ts = label.fit.getTransitionSeries();
			for (Transition t : ts) {

				channel = parameters.getCalibration().fractionalChannelFromEnergy(t.energyValue);
				if (channel >= p.dr.dataWidth || channel < 0) continue;
				
				FittingFunction fitFn = parameters.forTransition(t, ts.getShell());

				
				//get a height value from the fitting function, then apply the same transformation as the fitting did
				markerHeight = fitFn.forEnergy(t.energyValue) * label.fit.getTotalScale();
							
				//markerHeights.set((int) channel, markerHeight);
				
				float positionX = getXForChannel(p, channel);
				
				markerHeight = transformValueForPlot(p.dr, markerHeight);
				
				p.context.moveTo(positionX, p.plotSize.y);
				p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
				
				
				if (escapeType.get().hasOffset())
				{
					for (Transition esc : escapeType.get().offset()) {
					
						channel = parameters.getCalibration().fractionalChannelFromEnergy(t.energyValue - esc.energyValue);
						if (channel < 0) continue;
						
						positionX = getXForChannel(p, channel);
						
						FittingFunction escFn = parameters.forEscape(t, esc, ts.getElement(), ts.getShell());
						markerHeight = escFn.forEnergy(t.energyValue) * label.fit.getTotalScale();
						//markerHeight *= Curve.escapeIntensity(fit.getTransitionSeries().element);
						//markerHeight *= esc.relativeIntensity;
						markerHeight = transformValueForPlot(p.dr, markerHeight);
						
					
						p.context.moveTo(positionX, p.plotSize.y);
						p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
						
					}
				}


			}

			p.context.stroke();

		}
		
		
		p.context.restore();
		
	}

	public float getXForChannel(PainterData p, float channel)
	{
		float channelWidth = p.plotSize.x / p.dr.dataWidth;
		return channel * channelWidth;
	}
	

}
