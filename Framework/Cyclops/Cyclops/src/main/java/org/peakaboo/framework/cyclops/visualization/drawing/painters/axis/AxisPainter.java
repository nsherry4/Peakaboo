package org.peakaboo.framework.cyclops.visualization.drawing.painters.axis;


import java.util.List;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.Painter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;

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
	public static final int FONTSIZE_TEXT	= 12;
	
	protected AxesData		axesData;


	public AxisPainter()
	{
		axesData = new AxesData(new Bounds<Float>(0.0f, 0.0f), new Bounds<Float>(0.0f, 0.0f));
	}


	public void setDimensions(Bounds<Float> xPositionBounds, Bounds<Float> yPositionBounds)
	{
		this.axesData.xPositionBounds = xPositionBounds;
		this.axesData.yPositionBounds = yPositionBounds;
	}


	/**
	 * Returns the vertical size of the axes. It <i>does not</i> get the size of the
	 * y-axes. Usually, this is measuring the height of the top and bottom x-axes
	 */
	public abstract Pair<Float, Float> getAxisSizeY(PainterData p);

	/**
	 * Returns the horizontal size of the axis. It <i>does not</i> get the size of
	 * the x-axes. Usually, this is measuring the width of the left and right y-axes
	 */
	public abstract Pair<Float, Float> getAxisSizeX(PainterData p);



	protected static float getTitleFontHeight(Surface context)
	{
		return getTitleFontHeight(context, 1.0f);
	}


	protected static float getTitleFontHeight(Surface context, float titleScale)
	{
		float height;
		context.save();
		context.setFontSize(FONTSIZE_TEXT * titleScale);
		height = context.getFontHeight() + context.getFontLeading();
		context.restore();
		return height;
	}


	@Override
	protected float getBaseUnitSize(DrawingRequest dr)
	{
		return Math.min(dr.imageHeight, dr.imageWidth) / 350.0f;
	}


	protected float getPenWidth(float baseSize)
	{
		float width;
		width = baseSize;
		return width;
	}
	

	public static Coord<Bounds<Float>> calcAxisBorders(PainterData p, List<AxisPainter> axisPainters)
	{
		Bounds<Float> availableX, availableY;
		availableX = new Bounds<>(0.0f, p.dr.imageWidth);
		availableY = new Bounds<>(0.0f, p.dr.imageHeight);

		if (axisPainters != null) {

			Pair<Float, Float> axisSizeX, axisSizeY;

			for (AxisPainter axisPainter : axisPainters) {


				axisPainter.setDimensions(

				new Bounds<Float>(availableX.start, availableX.end),
						new Bounds<Float>(availableY.start, availableY.end)

				);

				axisSizeX = axisPainter.getAxisSizeX(p);
				axisSizeY = axisPainter.getAxisSizeY(p);

				availableX.start += axisSizeX.first;
				availableX.end -= axisSizeX.second;
				availableY.start += axisSizeY.first;
				availableY.end -= axisSizeY.second;

			}

		}

		return new Coord<Bounds<Float>>(availableX, availableY);
	}

}
