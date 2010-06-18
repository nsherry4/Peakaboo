package peakaboo.curvefit.painters;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.fitting.GaussianFittingFunction;
import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.peaktable.Transition;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.PlotDrawing;
import peakaboo.drawing.plot.painters.PlotPainter;

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

	public FittingMarkersPainter(FittingResultSet fitResults)
	{
		this.fitResults = fitResults;
	}


	@Override
	public void drawElement(PainterData p)
	{
		DrawingRequest dr = (DrawingRequest)p.dr;
		float channel, markerHeight;
		Spectrum markerHeights = new Spectrum(dr.dataWidth);

		p.context.save();
		p.context.setLineWidth(1.0f);

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
				
				markerHeight = transformValueForPlot((DrawingRequest)p.dr, markerHeight);
				
				p.context.setSource(0.0f, 0.0f, 0.0f);
				p.context.moveTo(positionX, p.plotSize.y);
				p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
				
				
				
				channel = getChannelAtEnergy(p.dr, t.energyValue - FittingSet.escape);
				if (channel < 0) continue;
				
				positionX = getXForChannel(p, channel);
				
				
				markerHeight = gauss.getHeightAtPoint(t.energyValue / dr.unitSize) * fit.scaleFactor / fit.normalizationScale;
				markerHeight *= TransitionSeriesFitting.escapeIntensity(fit.transitionSeries.element);
				markerHeight = transformValueForPlot((DrawingRequest)p.dr, markerHeight);
				
			
				p.context.setSource(0.0f, 0.0f, 0.0f);
				p.context.moveTo(positionX, p.plotSize.y);
				p.context.lineTo(positionX, p.plotSize.y * (1.0f - markerHeight) );
				

				//markerHeights.set((int)channel, markerHeight * TransitionSeriesFitting.escapeIntensity(fit.transitionSeries.element));

			}

			//traceData(p.context, p.dr, p.plotSize, p.dataHeights, TraceType.LINE, markerHeights);
			p.context.stroke();

		}
		
		
		p.context.restore();
		
	}


}
