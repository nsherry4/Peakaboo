package peakaboo.display.plot.painters;


import java.awt.Color;

import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.display.plot.painters.FittingLabel.PlotPalette;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.PlotDrawing;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

/**
 * 
 * A {@link PlotPainter} for {@link PlotDrawing}s which draws the data for the sum of a set of {@link FittingResult}s
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingSumPainter extends PlotPainter
{

	private Spectrum data;
	private PlotPalette palette;
	private boolean fill;



	/**
	 * 
	 * Create a new FittingSumPainter
	 * 
	 * @param data the data to draw on the plot
	 * @param stroke the {@link Color} to stroke the data with
	 * @param fill the {@link Color} to fill the data with
	 */
	public FittingSumPainter(Spectrum data, PlotPalette palette, boolean fill)
	{
		this.data = data;
		this.palette = palette;
		this.fill = fill;
	}


	@Override
	public void drawElement(PainterData p)
	{
		if (data != null)
		{
			traceData(p, data);
	
			if (fill) {
				p.context.setSource(palette.fitFill);
				p.context.fillPreserve();
			}
	
			p.context.setSource(palette.sumStroke);
			p.context.stroke();
		}
	}

}
