package peakaboo.curvefit.painters;


import java.awt.Color;

import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.Plot;
import peakaboo.drawing.plot.painters.PlotPainter;

/**
 * 
 * A {@link PlotPainter} for {@link Plot}s which draws the data for each {@link FittingResult} in a {@link FittingResultSet}
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingPainter extends PlotPainter
{

	private FittingResultSet	data;
	private Color				stroke;
	private Color				fill;


	/**
	 * 
	 * Create a new FittingPainter without a fill {@link Color}
	 * 
	 * @param data the data to draw on the plot
	 * @param stroke the {@link Color} to stroke the data with
	 */
	public FittingPainter(FittingResultSet data, Color stroke)
	{
		init(data, stroke, new Color(0.0f, 0.0f, 0.0f, 0.0f));
	}


	/**
	 * 
	 * Create a new FittingPainter
	 * 
	 * @param data the data to draw on the plot
	 * @param stroke the {@link Color} to stroke the data with
	 * @param fill the {@link Color} to fill the data with
	 */
	public FittingPainter(FittingResultSet data, Color stroke, Color fill)
	{
		init(data, stroke, fill);
	}


	private void init(FittingResultSet data, Color stroke, Color fill)
	{
		this.data = data;
		this.stroke = stroke;
		this.fill = fill;
	}


	@Override
	public void drawElement(PainterData p)
	{
		
		for (FittingResult fitResult : data.fits) {

			
			traceData(p, fitResult.fit);

			p.context.setSource(fill);
			p.context.fillPreserve();

			p.context.setSource(stroke);
			p.context.stroke();
		}
		

	}

}
