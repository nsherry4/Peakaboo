package peakaboo.curvefit.painters;


import java.awt.Color;
import java.util.List;

import peakaboo.curvefit.results.FittingResult;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.Plot;
import peakaboo.drawing.plot.painters.PlotPainter;

/**
 * 
 * A {@link PlotPainter} for {@link Plot}s which draws the data for the sum of a set of {@link FittingResult}s
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingSumPainter extends PlotPainter
{

	private List<Double>	data;
	private Color			stroke;
	private Color			fill;

	/**
	 * 
	 * Create a new FittingSumPainter without a fill {@link Color}
	 * 
	 * @param data the data to draw on the plot
	 * @param stroke the {@link Color} to stroke the data with
	 */
	public FittingSumPainter(List<Double> data, Color stroke)
	{
		init(data, stroke, new Color(0.0f, 0.0f, 0.0f, 0.0f));
	}

	/**
	 * 
	 * Create a new FittingSumPainter
	 * 
	 * @param data the data to draw on the plot
	 * @param stroke the {@link Color} to stroke the data with
	 * @param fill the {@link Color} to fill the data with
	 */
	public FittingSumPainter(List<Double> data, Color stroke, Color fill)
	{
		init(data, stroke, fill);
	}


	private void init(List<Double> data, Color stroke, Color fill)
	{
		this.data = data;
		this.stroke = stroke;
		this.fill = fill;
	}


	@Override
	public void drawElement(PainterData p)
	{
		
		traceData(p, data);

		p.context.setSource(fill);
		p.context.fillPreserve();

		p.context.setSource(stroke);
		p.context.stroke();
	}

}
