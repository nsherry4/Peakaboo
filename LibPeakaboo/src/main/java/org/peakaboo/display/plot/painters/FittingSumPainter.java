package org.peakaboo.display.plot.painters;


import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.PlotPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PlotPalette;

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
	 * @param palette the colours to use
	 * @param fill indicates if the shapes drawn should be filled or traced
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
