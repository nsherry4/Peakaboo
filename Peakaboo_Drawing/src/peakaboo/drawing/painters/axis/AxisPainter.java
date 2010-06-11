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
		axesData = new AxesData(new Range<Float>(0.0f, 0.0f), new Range<Float>(0.0f, 0.0f));
	}


	public void setDimensions(Range<Float> xPositionBounds, Range<Float> yPositionBounds)
	{
		this.axesData.xPositionBounds = xPositionBounds;
		this.axesData.yPositionBounds = yPositionBounds;
	}


	public abstract Pair<Float, Float> getAxisSizeY(PainterData p);


	public abstract Pair<Float, Float> getAxisSizeX(PainterData p);


	protected static float getTickSize(float baseSize, DrawingRequest dr)
	{
		return baseSize * 5;
	}


	protected static float getTickFontHeight(Surface context, DrawingRequest dr)
	{
		return context.getFontHeight();
	}


	protected static float getTitleFontHeight(Surface context, DrawingRequest dr)
	{
		return getTitleFontHeight(context, dr, 1.0f);
	}


	protected static float getTitleFontHeight(Surface context, DrawingRequest dr, float titleScale)
	{
		float height;
		context.save();
		context.setFontSize(FONTSIZE_TITLE * titleScale);
		height = context.getFontHeight() + context.getFontLeading();
		context.restore();
		return height;
	}


	@Override
	protected float getBaseUnitSize(peakaboo.drawing.DrawingRequest dr)
	{
		return (float)Math.min(dr.imageHeight, dr.imageWidth) / 350.0f;
	}


	protected float getPenWidth(float baseSize, peakaboo.drawing.DrawingRequest dr)
	{
		float width;
		width = baseSize;
		return width;
	}
	

	public static Coord<Range<Float>> calcAxisBorders(PainterData p, List<AxisPainter> axisPainters)
	{
		Range<Float> availableX, availableY;
		availableX = new Range<Float>(0.0f, p.dr.imageWidth);
		availableY = new Range<Float>(0.0f, p.dr.imageHeight);

		if (axisPainters != null) {

			Pair<Float, Float> axisSizeX, axisSizeY;

			for (AxisPainter axisPainter : axisPainters) {


				axisPainter.setDimensions(

				new Range<Float>(availableX.start, availableX.end),
						new Range<Float>(availableY.start, availableY.end)

				);

				axisSizeX = axisPainter.getAxisSizeX(p);
				axisSizeY = axisPainter.getAxisSizeY(p);

				availableX.start += axisSizeX.first;
				availableX.end -= axisSizeX.second;
				availableY.start += axisSizeY.first;
				availableY.end -= axisSizeY.second;

			}

		}

		return new Coord<Range<Float>>(availableX, availableY);
	}

}
