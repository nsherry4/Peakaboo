package peakaboo.drawing.map.painters.axis;



import java.awt.Color;
import java.util.List;

import fava.*;
import fava.datatypes.Pair;
import fava.signatures.FunctionCombine;
import static fava.Fn.*;
import static fava.Functions.*;

import peakaboo.datatypes.Coord;
import peakaboo.datatypes.SISize;
import peakaboo.drawing.painters.PainterData;



public class LegendCoordsAxisPainter extends AbstractKeyCoordAxisPainter
{

	private List<Pair<Color, String>>	entries;

	public LegendCoordsAxisPainter(boolean drawCoords, Coord<Number> topLeftCoord, Coord<Number> topRightCoord,
			Coord<Number> bottomLeftCoord, Coord<Number> bottomRightCoord, SISize coordinateUnits,
			boolean drawSpectrum, int spectrumHeight, boolean realDimensionsProvided, String descriptor, List<Pair<Color, String>> entries)
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
			realDimensionsProvided,
			descriptor);

		this.entries = entries;

	}


	protected void drawKey(final PainterData p)
	{

		p.context.save();

		//calculate some coordinates
		Pair<Float, Float> spectrumBoundsX = getAxisSizeX(p);
		final float offsetX = axesData.xPositionBounds.start + spectrumBoundsX.first;
		final float width = axesData.xPositionBounds.end - axesData.xPositionBounds.start - spectrumBoundsX.second
				- spectrumBoundsX.first;
		float offsetY = axesData.yPositionBounds.end - getKeyBorderSize(p.context).y;
		if (drawCoords) offsetY += keyHeight;
		final float textLineHeight = p.context.getFontHeight();
		final float textBaseline = offsetY + keyHeight + (drawCoords ? 0.0f : textLineHeight/2.0f);
		float fontSize = p.context.getFontSize();

		
		
		// concatenate the list of strings to display so we can check the width of the total string
		String markingsText = foldr(map(entries, Functions.<Color, String>second()), strcat(" "));
		float legendSquareWidth = entries.size() * keyHeight * 2.5f - keyHeight; // -keyHeight because we don't need
																					// padding on the end

		
		// keep shrinking the font size until all of the text until the font size is small enough that it fits
		float expectedTextWidth = width;
		while (width > 0.0 && fontSize > 1.0)
		{
			// get the width of the text for all of the markings
			expectedTextWidth = p.context.getTextWidth(markingsText) + legendSquareWidth;
			if (expectedTextWidth < width) break;

			fontSize *= (width / expectedTextWidth) * 0.95;
			p.context.setFontSize(fontSize);

		}

		float startX = offsetX + ((width - expectedTextWidth) / 2.0f);
		foldr(entries, startX, new FunctionCombine<Pair<Color, String>, Float, Float>() {

			public Float f(Pair<Color, String> entry, Float position)
			{

				p.context.rectangle(position, textBaseline, keyHeight, -keyHeight);
				p.context.setSource(entry.first);
				p.context.fillPreserve();
				p.context.setSource(Color.black);
				p.context.stroke();

				p.context.writeText(entry.second, position + keyHeight * 1.5f, textBaseline);

				return position + p.context.getTextWidth(entry.second) + keyHeight * 2.5f;
			}
		});

		float centerWidth = p.context.getTextWidth(descriptor);
		p.context.writeText(descriptor, offsetX + (width - centerWidth) / 2.0f, textBaseline + textLineHeight*(drawCoords ? 2.0f : 1.25f));

		p.context.restore();

	}

}
