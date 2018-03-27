package peakaboo.curvefit.view;

import java.awt.Color;

import peakaboo.curvefit.model.FittingResult;
import peakaboo.curvefit.model.FittingResultSet;
import peakaboo.curvefit.model.fittingfunctions.FittingFunction;
import peakaboo.curvefit.model.fittingfunctions.FittingFunctionFactory;
import peakaboo.curvefit.model.transition.Transition;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesFitting;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.PlotDrawing;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.ISpectrum;
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

	/**
	 * Create a FittingMarkersPainter
	 * @param fitResults the {@link FittingResultSet} for the data being drawn
	 * @param escapeType the {@link EscapePeakType} used to generate the {@link FittingResultSet}
	 * @param c the {@link Color} to use when drawing the markings
	 */
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
		Spectrum markerHeights = new ISpectrum(dr.dataWidth);

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
				
				FittingFunction gauss = FittingFunctionFactory.get(
						t.energyValue,
						TransitionSeriesFitting.getSigmaForTransition(TransitionSeriesFitting.SIGMA, t),
						t.relativeIntensity
					);

				
				
				markerHeight = gauss.getHeightAtPoint(t.energyValue) * fit.scaleFactor / fit.normalizationScale;
							
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
						
						
						markerHeight = gauss.getHeightAtPoint(t.energyValue) * fit.scaleFactor / fit.normalizationScale;
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
