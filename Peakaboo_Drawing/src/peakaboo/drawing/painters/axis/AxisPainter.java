package peakaboo.drawing.painters.axis;


import java.util.List;

import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.painters.Painter;
import peakaboo.drawing.painters.PainterData;

/**
 * An AxisPainter is a special kind of painter used to paint Axes on a drawing. Unlike other {@link Painter}s,
 * AxisPainters reserve space for themselves, such that any further drawing should be done inside of the space
 * not used by the AxisPainter
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class AxisPainter extends Painter
{


	public static final int	FONTSIZE_TICK	= 10;
	public static final int	FONTSIZE_TITLE	= 22;

	protected AxesData		axesData;


	public AxisPainter()
	{
		axesData = new AxesData(new Range<Double>(0.0, 0.0), new Range<Double>(0.0, 0.0));
	}


	public void setDimensions(Range<Double> xPositionBounds, Range<Double> yPositionBounds)
	{
		this.axesData.xPositionBounds = xPositionBounds;
		this.axesData.yPositionBounds = yPositionBounds;
	}


	public abstract Pair<Double, Double> getAxisSizeY(PainterData p);


	public abstract Pair<Double, Double> getAxisSizeX(PainterData p);


	protected static double getTickSize(double baseSize, DrawingRequest dr)
	{
		return baseSize * 5;
	}


	protected static double getTickFontHeight(Surface context, DrawingRequest dr)
	{
		return context.getFontHeight();
	}


	protected static double getTitleFontHeight(Surface context, DrawingRequest dr)
	{
		return getTitleFontHeight(context, dr, 1.0);
	}


	protected static double getTitleFontHeight(Surface context, DrawingRequest dr, double titleScale)
	{
		double height;
		context.save();
		context.setFontSize(FONTSIZE_TITLE * titleScale);
		height = context.getFontHeight() + context.getFontLeading();
		context.restore();
		return height;
	}


	@Override
	protected double getBaseUnitSize(peakaboo.drawing.DrawingRequest dr)
	{
		return Math.min(dr.imageHeight, dr.imageWidth) / 350.0;
	}


	protected double getPenWidth(double baseSize, peakaboo.drawing.DrawingRequest dr)
	{
		double width;
		width = baseSize;
		return width;
	}
	

	public static Coord<Range<Double>> calcAxisBorders(PainterData p, List<AxisPainter> axisPainters)
	{
		Range<Double> availableX, availableY;
		availableX = new Range<Double>(0.0, p.dr.imageWidth);
		availableY = new Range<Double>(0.0, p.dr.imageHeight);

		if (axisPainters != null) {

			Pair<Double, Double> axisSizeX, axisSizeY;

			for (AxisPainter axisPainter : axisPainters) {


				axisPainter.setDimensions(

				new Range<Double>(availableX.start, availableX.end),
						new Range<Double>(availableY.start, availableY.end)

				);

				axisSizeX = axisPainter.getAxisSizeX(p);
				axisSizeY = axisPainter.getAxisSizeY(p);

				availableX.start += axisSizeX.first;
				availableX.end -= axisSizeX.second;
				availableY.start += axisSizeY.first;
				availableY.end -= axisSizeY.second;

			}

		}

		return new Coord<Range<Double>>(availableX, availableY);
	}

}
