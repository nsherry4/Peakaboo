package org.peakaboo.framework.cyclops.visualization.drawing.map.painters.axis;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SISize;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;



public abstract class AbstractKeyCoordAxisPainter extends AxisPainter
{

	protected Coord<Number>			coordLoXLoY, coordHiXLoY, coordLoXHiY, coordHiXHiY;
	protected SISize				coordinateUnits;
	protected static Coord<Float>	coordPadding	= new Coord<>(2.0f, 2.0f);

	protected boolean				drawCoords, drawKey, realDimensionsProvided, drawScaleBar;
	protected int					keyHeight;
	
	protected PaletteColour			colour;
	
	protected AbstractKeyCoordAxisPainter(boolean drawCoords, PaletteColour colour, Coord<Number> coordLoXLoY, Coord<Number> coordHiXLoY,
			Coord<Number> coordLoXHiY, Coord<Number> coordHiXHiY, SISize coordinateUnits,
			boolean drawKey, int keyHeight, boolean realDimensionsProvided, boolean drawScaleBar)
	{
		super();

		this.drawCoords = drawCoords;
		this.colour = colour;
		this.coordLoXLoY = coordLoXLoY;
		this.coordHiXLoY = coordHiXLoY;
		this.coordLoXHiY = coordLoXHiY;
		this.coordHiXHiY = coordHiXHiY;
		this.coordinateUnits = coordinateUnits;
		this.drawScaleBar = drawScaleBar;
		
		this.drawKey = drawKey;
		this.keyHeight = keyHeight;

		this.realDimensionsProvided = realDimensionsProvided;
		
	}

	@Override
	public Pair<Float, Float> getAxisSizeX(PainterData p)
	{

		Coord<Bounds<Float>> borderSize = getBorderSize(p.context);
		return new Pair<>(borderSize.x.start, borderSize.x.end);
	}


	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p)
	{
		Coord<Bounds<Float>> borderSize = getBorderSize(p.context);
		return new Pair<>(borderSize.y.start, borderSize.y.end);
	}


	@Override
	public void drawElement(PainterData p)
	{

		if (drawKey) drawKey(p);
		drawCoordinates(p);
		if (realDimensionsProvided && coordinateUnits != null && drawScaleBar) drawScaleBar(p);

	}


	protected abstract void drawKey(PainterData p);


	private void drawScaleBar(PainterData p)
	{

		float width = Math.abs(coordHiXHiY.x.floatValue() - coordLoXHiY.x.floatValue());
		if (width == 0d) return;
		float totalWidth = width;
		width /= 3.0;

				
		SISize units = coordinateUnits;

		while (width < 1.0)
		{
			width *= 1000;
			totalWidth *= 1000;
			units = SISize.lower(units);
		}

		while (width > 1000.0)
		{
			width /= 1000.0;
			totalWidth /= 1000;
			units = SISize.raise(units);
		}

		width = SigDigits.toIntSigDigit(width, 1);

		float widthAsPercentOfTotal = width / totalWidth;

		Pair<Float, Float> otherAxis = getAxisSizeX(p);
		float drawableWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxis.first
				- otherAxis.second;
		float drawingWidth = drawableWidth * widthAsPercentOfTotal;
		float widthPosition = axesData.xPositionBounds.start + otherAxis.first;

		Pair<Float, Float> heightAxis = getAxisSizeY(p);
		float heightPosition = axesData.yPositionBounds.end - heightAxis.second;

		p.context.save();

		float lineWidth = getBaseUnitSize(p.dr) * 2.0f;
		p.context.setLineWidth(lineWidth);
		p.context.setSource(colour);

		p.context.setFontSize(getCoordFontSize(p));

		heightPosition += coordPadding.y;
		heightPosition += (p.context.getFontAscent() / 2.0);

		p.context.moveTo(widthPosition + (drawableWidth - drawingWidth) / 2.0f, heightPosition);
		p.context.lineTo(widthPosition + (drawableWidth + drawingWidth) / 2.0f, heightPosition);
		p.context.stroke();

		heightPosition += (p.context.getFontAscent() / 2.0);
		heightPosition += p.context.getFontHeight();

		String unitText = (int) width + " " + units;
		float unitTextWidth = p.context.getTextWidth(unitText);
		p.context.writeText(unitText, widthPosition + ((drawableWidth - unitTextWidth) / 2.0f), heightPosition);

		p.context.restore();

	}


	private void drawCoordinates(PainterData p)
	{

		if (!drawCoords) return;

		p.context.setSource(colour);

		Pair<Float, Float> borderX, borderY;
		borderX = getAxisSizeX(p);
		borderY = getAxisSizeY(p);

		float mapLoX, mapLoY, mapHiX, mapHiY;
		if (p.dr.screenOrientation) {
			mapLoX = axesData.xPositionBounds.start + borderX.first;
			mapLoY = axesData.yPositionBounds.start;
			mapHiX = axesData.xPositionBounds.end - borderX.second;
			mapHiY = axesData.yPositionBounds.end - borderY.second;
		} else {
			mapLoX = axesData.xPositionBounds.start + borderX.first;
			mapHiY = axesData.yPositionBounds.start; 
			mapHiX = axesData.xPositionBounds.end - borderX.second;
			mapLoY = axesData.yPositionBounds.end - borderY.second;
		}
		

		drawCoordinatePair(p, coordLoXLoY, mapLoX, mapLoY, true);
		drawCoordinatePair(p, coordHiXLoY, mapHiX, mapLoY, false);

		drawCoordinatePair(p, coordLoXHiY, mapLoX, mapHiY, true);
		drawCoordinatePair(p, coordHiXHiY, mapHiX, mapHiY, false);
	}


	private void drawCoordinatePair(PainterData p, Coord<Number> pair, float x, float y, boolean forwards)
	{
		p.context.save();

		float textX;
		float textY;
		String text;

		String units = (!realDimensionsProvided || coordinateUnits == null) ? "" : " " + coordinateUnits;

		p.context.setFontSize(getCoordFontSize(p));

		text = pair.x.toString() + units + "," + pair.y.toString() + units;
		// We don't apply coordPadding here because it's really not meant to push the
		// text further horizontally towards the center of the image than it needs to be
		// to line up with the edge of the map.
		if (forwards) {
			textX = x;
		} else {
			textX = x - p.context.getTextWidth(text);
		}
		textY = y + coordPadding.y + p.context.getFontAscent();
		p.context.writeText(text, textX, textY);

		p.context.restore();
	}


	private float getCoordFontSize(PainterData p)
	{
		return p.context.getFontSize() - 2;
	}


	private float getScaleBarHeight(Surface context)
	{
		return context.getFontHeight() * 2 - context.getFontLeading() - context.getFontDescent();
	}


	private Coord<Float> getCoordinateBorderSize(Surface context)
	{

		if (!drawCoords) return new Coord<>(0.0f, 0.0f);

		float x, y;

		context.save();

		context.setFontSize(context.getFontSize() - 2);
		
		// Y
		y = context.getFontHeight();

		// X
		x = y;
		
		context.restore();

		return new Coord<>(x + (coordPadding.x * 2.0f), y + (coordPadding.y * 2.0f));

	}


	protected Coord<Float> getKeyBorderSize(Surface context)
	{

		if (!drawKey) return new Coord<>(0.0f, 0.0f);

		float textHeight = context.getFontHeight();

		if (drawCoords)
		{
			return new Coord<>(0.0f, keyHeight * 1.6f + textHeight);
		}
		else
		{
			return new Coord<>(0.0f, keyHeight * 1.3f + textHeight);
		}

	}


	/**
	 * Calculates the amount of space needed for anything other than the map itself
	 * 
	 * @param dr
	 *            the DrawingRequest to define how maps should be drawn
	 * @param context
	 *            a Surface for use in calculating things like Font sizes.
	 * @return a Coordinate object containing the total width and height not available to the map proper.
	 */
	private Coord<Bounds<Float>> getBorderSize(Surface context)
	{

		Coord<Float> coordBorder = getCoordinateBorderSize(context);
		Coord<Float> spectBorder = getKeyBorderSize(context);

		float bottomCoordHeight;
		if (drawCoords)
		{
			bottomCoordHeight = coordBorder.y;
		}
		else if (realDimensionsProvided && coordinateUnits != null)
		{
			bottomCoordHeight = getScaleBarHeight(context);
		}
		else
		{
			bottomCoordHeight = 0.0f;
		}
		
		return new Coord<>(new Bounds<>(coordBorder.x, coordBorder.x), new Bounds<>(
			coordBorder.y,
			bottomCoordHeight + spectBorder.y));

	}

}