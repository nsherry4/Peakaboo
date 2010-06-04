package peakaboo.drawing.map.painters.axis;



import java.awt.Color;
import java.util.List;

import peakaboo.calculations.functional.Function2;
import peakaboo.calculations.functional.Functional;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.SISize;
import peakaboo.drawing.painters.PainterData;



public class LegendCoordsAxisPainter extends AbstractKeyCoordAxisPainter
{

	private List<Pair<Color, String>>	entries;


	public LegendCoordsAxisPainter(boolean drawCoords, Coord<Number> topLeftCoord, Coord<Number> topRightCoord,
			Coord<Number> bottomLeftCoord, Coord<Number> bottomRightCoord, SISize coordinateUnits,
			boolean drawSpectrum, int spectrumHeight, boolean realDimensionsProvided, List<Pair<Color, String>> entries)
	{
		super(
			drawCoords,
			topLeftCoord,
			topRightCoord,
			bottomLeftCoord,
			bottomRightCoord,
			coordinateUnits,
			drawSpectrum,
			spectrumHeight,
			realDimensionsProvided);

		this.entries = entries;

	}


	protected void drawKey(final PainterData p)
	{

		p.context.save();

		Pair<Double, Double> spectrumBoundsX = getAxisSizeX(p);
		final double offsetX = axesData.xPositionBounds.start + spectrumBoundsX.first;
		final double width = axesData.xPositionBounds.end - axesData.xPositionBounds.start - spectrumBoundsX.second
				- spectrumBoundsX.first;

		double offsetY = axesData.yPositionBounds.end - getKeyBorderSize(p.context).y;
		if (drawCoords) offsetY += keyHeight;

		String intens;
		final double textLineHeight = p.context.getFontHeight();
		final double textBaseline = offsetY + keyHeight + (drawCoords ? 0.0 : textLineHeight/2.0);
		double fontSize = p.context.getFontSize();

		intens = "Colour";

		String markingsText;
		// concatenate the list of strings to display so we can check the width of the total string
		markingsText = Functional.foldr(entries, "", new Function2<Pair<Color, String>, String, String>() {

			public String f(Pair<Color, String> marking, String str)
			{
				return str + marking.second;
			}
		}) + "";
		double legendSquareWidth = entries.size() * keyHeight * 2.5 - keyHeight; // -keyHeight because we don't need
																					// padding on the end

		// keep shrinking the font size until all of the text until the font size is small enough that it fits
		double expectedTextWidth = width;
		while (width > 0.0 && fontSize > 1.0)
		{
			// get the width of the text for all of the markings
			expectedTextWidth = p.context.getTextWidth(markingsText) + legendSquareWidth;
			if (expectedTextWidth < width) break;

			fontSize *= (width / expectedTextWidth) * 0.95;
			p.context.setFontSize(fontSize);

		}

		double startX = offsetX + ((width - expectedTextWidth) / 2.0);
		final double startY = offsetY;
		Functional.foldr(entries, startX, new Function2<Pair<Color, String>, Double, Double>() {

			public Double f(Pair<Color, String> entry, Double position)
			{

				p.context.rectangle(position, textBaseline, keyHeight, -keyHeight);
				p.context.setSource(entry.first);
				p.context.fillPreserve();
				p.context.setSource(Color.black);
				p.context.stroke();

				p.context.writeText(entry.second, position + keyHeight * 1.5, textBaseline);

				return position + p.context.getTextWidth(entry.second) + keyHeight * 2.5;
			}
		});

		double centerWidth = p.context.getTextWidth(intens);
		p.context.writeText(intens, offsetX + (width - centerWidth) / 2.0, textBaseline + textLineHeight*(drawCoords ? 2.0 : 1.25));

		p.context.restore();

	}

}
