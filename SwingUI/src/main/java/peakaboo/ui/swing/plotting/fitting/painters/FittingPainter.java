package peakaboo.ui.swing.plotting.fitting.painters;


import java.awt.Color;
import java.util.List;

import peakaboo.curvefit.fitting.FittingResult;
import peakaboo.curvefit.fitting.FittingResultSet;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.PlotDrawing;
import scidraw.drawing.plot.painters.PlotPainter;


/**
 * 
 * A {@link PlotPainter} for {@link PlotDrawing}s which draws the data for each {@link FittingResult} in a {@link FittingResultSet}
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingPainter extends PlotPainter
{

	private List<FittingResult>	data;
	private Color				stroke;
	private Color				fill;



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
		this(data.getFits(), stroke, fill);
	}
	
	
	/**
	 * 
	 * Create a new FittingPainter
	 * 
	 * @param data the data to draw on the plot
	 * @param stroke the {@link Color} to stroke the data with
	 * @param fill the {@link Color} to fill the data with
	 */
	public FittingPainter(List<FittingResult> data, Color stroke, Color fill) {
		this.data = data;
		this.stroke = stroke;
		this.fill = fill;
	}



	@Override
	public void drawElement(PainterData p)
	{
		
		for (FittingResult fitResult : data) {

			traceData(p, fitResult.getFit());

			p.context.setSource(fill);
			p.context.fillPreserve();

			p.context.setSource(stroke);
			p.context.stroke();
		}
		

	}

}
