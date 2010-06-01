package peakaboo.curvefit.painters;


import java.util.List;

import peakaboo.curvefit.fitting.GaussianFittingFunction;
import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.Transition;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.Plot;
import peakaboo.drawing.plot.painters.PlotPainter;

/**
 * 
 * A {@link PlotPainter} for {@link Plot}s which draws lines at the centrepoints of {@link Transition}s
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
		double channel, markerHeight;
		List<Double> markerHeights = DataTypeFactory.<Double> list();

		p.context.save();
		p.context.setLineWidth(1.0);

		for (FittingResult fit : fitResults.fits) {

			markerHeights.clear();
			for (int i = 0; i < p.dr.dataWidth; i++) {
				markerHeights.add(0.0);
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
				
				double positionX = getXForChannel(p, channel);
				
				markerHeight = transformValueForPlot((DrawingRequest)p.dr, markerHeight);
				
				p.context.setSource(0.0, 0.0, 0.0);
				p.context.moveTo(positionX, p.plotSize.y);
				p.context.lineTo(positionX, p.plotSize.y * (1.0 - markerHeight) );
				
				
				
				channel = getChannelAtEnergy(p.dr, t.energyValue - TransitionSeriesFitting.escape);
				if (channel < 0) continue;
				
				positionX = getXForChannel(p, channel);
				
				
				markerHeight = gauss.getHeightAtPoint(t.energyValue / dr.unitSize) * fit.scaleFactor / fit.normalizationScale;
				markerHeight *= TransitionSeriesFitting.escapeIntensity(fit.transitionSeries.element);
				markerHeight = transformValueForPlot((DrawingRequest)p.dr, markerHeight);
				
			
				p.context.setSource(0.0, 0.0, 0.0);
				p.context.moveTo(positionX, p.plotSize.y);
				p.context.lineTo(positionX, p.plotSize.y * (1.0 - markerHeight) );
				

				//markerHeights.set((int)channel, markerHeight * TransitionSeriesFitting.escapeIntensity(fit.transitionSeries.element));

			}

			//traceData(p.context, p.dr, p.plotSize, p.dataHeights, TraceType.LINE, markerHeights);
			p.context.stroke();

		}
		
		
		p.context.restore();
		
	}


}
