package peakaboo.drawing.map.painters.axis;



import java.awt.Color;
import java.util.List;

import peakaboo.calculations.functional.Function1;
import peakaboo.calculations.functional.Function2;
import peakaboo.calculations.functional.Functional;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.SISize;
import peakaboo.datatypes.SigDigits;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.painters.axis.AxisPainter;



public abstract class AbstractKeyCoordAxisPainter extends AxisPainter
{

	protected Coord<Number>			topLeftCoord, topRightCoord, bottomLeftCoord, bottomRightCoord;
	protected SISize				coordinateUnits;
	protected static Coord<Double>	coordPadding	= new Coord<Double>(3.0, 3.0);

	protected boolean				drawCoords, drawKey, realDimensionsProvided;
	protected int					keyHeight;
	protected String				descriptor;


	public AbstractKeyCoordAxisPainter(boolean drawCoords, Coord<Number> topLeftCoord, Coord<Number> topRightCoord,
			Coord<Number> bottomLeftCoord, Coord<Number> bottomRightCoord, SISize coordinateUnits,
			boolean drawKey, int keyHeight, boolean realDimensionsProvided, String descriptor)
	{
		super();

		this.drawCoords = drawCoords;
		this.topLeftCoord = topLeftCoord;
		this.topRightCoord = topRightCoord;
		this.bottomLeftCoord = bottomLeftCoord;
		this.bottomRightCoord = bottomRightCoord;
		this.coordinateUnits = coordinateUnits;

		this.drawKey = drawKey;
		this.keyHeight = keyHeight;

		this.realDimensionsProvided = realDimensionsProvided;

		this.descriptor = descriptor;
		
	}


	@Override
	public Pair<Double, Double> getAxisSizeX(PainterData p)
	{

		Coord<Range<Double>> borderSize = getBorderSize(p.context);
		return new Pair<Double, Double>(borderSize.x.start, borderSize.x.end);
	}


	@Override
	public Pair<Double, Double> getAxisSizeY(PainterData p)
	{
		Coord<Range<Double>> borderSize = getBorderSize(p.context);
		return new Pair<Double, Double>(borderSize.y.start, borderSize.y.end);
	}


	@Override
	public void drawElement(PainterData p)
	{

		if (drawKey) drawKey(p);
		drawCoordinates(p, getCoordinateBorderSize(p.context));
		if (realDimensionsProvided && coordinateUnits != null) drawScaleBar(p);

	}


	protected abstract void drawKey(PainterData p);


	private void drawScaleBar(PainterData p)
	{

		double width = bottomRightCoord.x.doubleValue() - bottomLeftCoord.x.doubleValue();
		if (width == 0d) return;
		double totalWidth = width;
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

		double widthAsPercentOfTotal = width / totalWidth;

		Pair<Double, Double> otherAxis = getAxisSizeX(p);
		double drawableWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxis.first
				- otherAxis.second;
		double drawingWidth = drawableWidth * widthAsPercentOfTotal;
		double widthPosition = axesData.xPositionBounds.start + otherAxis.first;

		Pair<Double, Double> heightAxis = getAxisSizeY(p);
		double heightPosition = axesData.yPositionBounds.end - heightAxis.second;

		p.context.save();

		double lineWidth = getBaseUnitSize(p.dr) * 2.0;
		p.context.setLineWidth(lineWidth);
		p.context.setSource(0.0, 0.0, 0.0);

		p.context.setFontSize(getCoordFontSize(p));

		heightPosition += coordPadding.y;
		heightPosition += (p.context.getFontAscent() / 2.0);

		p.context.moveTo(widthPosition + (drawableWidth - drawingWidth) / 2.0, heightPosition);
		p.context.lineTo(widthPosition + (drawableWidth + drawingWidth) / 2.0, heightPosition);
		p.context.stroke();

		heightPosition += (p.context.getFontAscent() / 2.0);
		heightPosition += p.context.getFontHeight();

		String unitText = (int) width + " " + units;
		double unitTextWidth = p.context.getTextWidth(unitText);
		p.context.writeText(unitText, widthPosition + ((drawableWidth - unitTextWidth) / 2.0), heightPosition);

		p.context.restore();

	}


	private void drawCoordinates(PainterData p, Coord<Double> borders)
	{

		if (!drawCoords) return;

		p.context.setSource(0, 0, 0);

		Pair<Double, Double> borderX, borderY;
		borderX = getAxisSizeX(p);
		borderY = getAxisSizeY(p);

		double mapXStart, mapYStart, mapXEnd, mapYEnd;
		mapXStart = axesData.xPositionBounds.start;
		mapYStart = axesData.yPositionBounds.start;
		mapXEnd = axesData.xPositionBounds.end - borderX.second;
		mapYEnd = axesData.yPositionBounds.end - borderY.second;

		drawCoordinatePair(p, topLeftCoord, borders, mapXStart, mapYStart);
		drawCoordinatePair(p, topRightCoord, borders, mapXEnd, mapYStart);

		drawCoordinatePair(p, bottomLeftCoord, borders, mapXStart, mapYEnd);
		drawCoordinatePair(p, bottomRightCoord, borders, mapXEnd, mapYEnd);
	}


	private void drawCoordinatePair(PainterData p, Coord<Number> pair, Coord<Double> border, double x, double y)
	{
		p.context.save();

		double textX;
		double textY;
		String text;

		String units = (!realDimensionsProvided | coordinateUnits == null) ? "" : " " + coordinateUnits;

		p.context.setFontSize(getCoordFontSize(p));

		text = pair.x.toString() + units + ",";
		textX = x + border.x - coordPadding.x - p.context.getTextWidth(text);
		textY = y + coordPadding.y + p.context.getFontAscent();
		p.context.writeText(text, textX, textY);

		text = pair.y.toString() + units;
		textX = x + border.x - coordPadding.x - p.context.getTextWidth(text + ",");
		textY = y + coordPadding.y + p.context.getFontHeight() + p.context.getFontAscent();
		p.context.writeText(text, textX, textY);

		p.context.restore();
	}


	private double getCoordFontSize(PainterData p)
	{
		return p.context.getFontSize() - 2;
	}


	private double getScaleBarHeight(Surface context)
	{

		double y = context.getFontHeight() * 2 - context.getFontLeading() - context.getFontDescent();
		return y;

	}


	private Coord<Double> getCoordinateBorderSize(Surface context)
	{

		if (!drawCoords) return new Coord<Double>(0.0, 0.0);

		double x = 0.0;
		double y = 0.0;

		double cx;

		context.save();

		String units = coordinateUnits == null ? "" : " " + coordinateUnits;

		context.setFontSize(context.getFontSize() - 2);

		// X

		cx = context.getTextWidth(topLeftCoord.x.toString() + units + ",");
		if (cx > x) x = cx;

		cx = context.getTextWidth(topRightCoord.x.toString() + units + ",");
		if (cx > x) x = cx;

		cx = context.getTextWidth(bottomLeftCoord.x.toString() + units + ",");
		if (cx > x) x = cx;

		cx = context.getTextWidth(bottomRightCoord.x.toString() + units + ",");
		if (cx > x) x = cx;

		cx = context.getTextWidth(topLeftCoord.y.toString() + units + ",");
		if (cx > x) x = cx;

		cx = context.getTextWidth(topRightCoord.y.toString() + units + ",");
		if (cx > x) x = cx;

		cx = context.getTextWidth(bottomLeftCoord.y.toString() + units + ",");
		if (cx > x) x = cx;

		cx = context.getTextWidth(bottomRightCoord.y.toString() + units + ",");
		if (cx > x) x = cx;

		// Y
		y = context.getFontHeight() * 2 - context.getFontLeading() - context.getFontDescent();

		context.restore();

		return new Coord<Double>(x + (coordPadding.x * 2.0), y + (coordPadding.y * 2.0));

	}


	protected Coord<Double> getKeyBorderSize(Surface context)
	{

		if (!drawKey) return new Coord<Double>(0.0, 0.0);

		double textHeight = context.getFontHeight();

		if (drawCoords)
		{
			return new Coord<Double>(0.0, keyHeight * 3.0 + textHeight + textHeight);
		}
		else
		{
			return new Coord<Double>(0.0, keyHeight + textHeight + textHeight);
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
	private Coord<Range<Double>> getBorderSize(Surface context)
	{

		Coord<Double> coordBorder = getCoordinateBorderSize(context);
		Coord<Double> spectBorder = getKeyBorderSize(context);

		double bottomCoordHeight;
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
			bottomCoordHeight = 0.0;
		}

		return new Coord<Range<Double>>(new Range<Double>(coordBorder.x, coordBorder.x), new Range<Double>(
			coordBorder.y,
			bottomCoordHeight + spectBorder.y));

	}

}