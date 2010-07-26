package peakaboo.curvefit.painters;

import java.awt.Color;

import peakaboo.curvefit.fitting.EscapePeakType;
import peakaboo.curvefit.fitting.GaussianFittingFunction;
import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.peaktable.Transition;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;


/**
 * 
 * A {@link PlotPainter} for {@link PlotDrawing}s which draws lines at the centrepoints of {@link Transition}s
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingMarkersPainter extends PlotPainter
{

	private FittingResultSet	fitResults;
	private EscapePeakType		escapeType;
	private Color				colour;

	public FittingMarkersPainter(FittingResultSet fitResults, EscapePeakType escapeType, Color c)
	{
		this.fitResults = fitResults;
		this.escapeType = escapeType;
		this.colour = new Color(c.getRed(), c.getGreen(), c.getBlue());
	}


	@Override
	public void drawElement(PainterData p)
	{
		DrawingRequest dr = p.dr;
		float channel, markerHeight;
		Spectrum markerHeights = new Spectrum(dr.dataWidth);

		p.context.save();
		p.context.setLineWidth(1.0f);
		p.context.setSource(colour);
		
		for (FittingResult fit : fitResults.fits) {

			
			for (int i = 0; i < p.dr.dataWidth; i++) {
				markerHeights.set(i, 0.0f);
			}

			for (Transition t : fit.transitionSeries) {

				channel = getChannelAtEnergy(p.dr, t.energyValue);

				if (channel > p.dr.dataWidth) continue;
				
				GaussianFittingFunction gauss = new GaussianFittingFunction(
						t.energyValue / dr.unitSize,
						TransitionSeriesFitting.getSigmaForTransition(TransitionSeriesFitting.SIGMA, t) / dr.unitSize,
						t.relativeIntensity
					);

				
				
				markerHeight = gauss.getHeightAtPoint(t.energyValue / dr.unitSize) * fit.scaleFactor / fit.normalizationScale;
							
				//markerHeights.set((int) channel, markerHeight);
				
				float positionX = getXForChannel(p, channel);
				
				markerHeight = transformValueForPlot(p.dr, markerHeight);
				
				p.context.moveTo(positionX, p.plotSize.y);
				p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
				
				
				if (escapeType.hasOffset())
				{
					for (Transition esc : escapeType.offset()) {
					
						channel = getChannelAtEnergy(p.dr, t.energyValue - esc.energyValue);
						if (channel < 0) continue;
						
						positionX = getXForChannel(p, channel);
						
						
						markerHeight = gauss.getHeightAtPoint(t.energyValue / dr.unitSize) * fit.scaleFactor / fit.normalizationScale;
						markerHeight *= TransitionSeriesFitting.escapeIntensity(fit.transitionSeries.element);
						markerHeight *= esc.relativeIntensity;
						markerHeight = transformValueForPlot(p.dr, markerHeight);
						
					
						p.context.moveTo(positionX, p.plotSize.y);
						p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
						
					}
				}

				//markerHeights.set((int)channel, markerHeight * TransitionSeriesFitting.escapeIntensity(fit.transitionSeries.element));

			}

			//traceData(p.context, p.dr, p.plotSize, p.dataHeights, TraceType.LINE, markerHeights);
			p.context.stroke();

		}
		
		
		p.context.restore();
		
	}


}
