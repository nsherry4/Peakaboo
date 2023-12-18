package org.peakaboo.framework.cyclops.visualization.drawing.map.painters.axis;



import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SISize;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;


public class LegendCoordsAxisPainter extends AbstractKeyCoordAxisPainter
{

	private List<Pair<PaletteColour, String>>	entries;

	public LegendCoordsAxisPainter(boolean drawCoords, PaletteColour colour, Coord<Number> coordLoXLoY, Coord<Number> coordHiXLoY,
			Coord<Number> coordLoXHiY, Coord<Number> coordHiXHiY, SISize coordinateUnits,
			boolean drawSpectrum, int spectrumHeight, boolean realDimensionsProvided, boolean drawScaleBar, List<Pair<PaletteColour, String>> entries)
	{
		super(
			drawCoords,
			colour,
			coordLoXLoY,
			coordHiXLoY,
			coordLoXHiY,
			coordHiXHiY,
			coordinateUnits,
			drawSpectrum,
			spectrumHeight,
			realDimensionsProvided,
			drawScaleBar);

		this.entries = entries;

	}


	@Override
	protected void drawKey(final PainterData p)
	{

		p.context.save();

		//calculate some coordinates
		Pair<Float, Float> spectrumBoundsX = getAxisSizeX(p);
		final float offsetX = axesData.xPositionBounds.start + spectrumBoundsX.first;
		final float width = axesData.xPositionBounds.end - axesData.xPositionBounds.start - spectrumBoundsX.second
				- spectrumBoundsX.first;
		float offsetY = axesData.yPositionBounds.end - getKeyBorderSize(p.context).y;
		if (drawCoords) offsetY += 0.3f*keyHeight;
		final float textLineHeight = p.context.getFontHeight();
		final float textBaseline = offsetY + keyHeight + (drawCoords ? 0.0f : textLineHeight/2.0f);
		float fontSize = p.context.getFontSize();

		
		
		// concatenate the list of strings to display so we can check the width of the total string
		//String markingsText = foldr(map(entries, Functions.<Color, String>second()), strcat(" "));
		String markingsText = entries.stream().map(e -> e.second).collect(Collectors.joining(" "));
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
		
		float position = startX;
		for (Pair<PaletteColour, String> entry : entries) {
			p.context.rectAt(position, textBaseline-keyHeight+p.context.getFontDescent(), keyHeight, keyHeight);
			p.context.setSource(entry.first);
			p.context.fillPreserve();
			p.context.setSource(colour);
			p.context.stroke();
			
			p.context.writeText(entry.second, position + keyHeight * 1.5f, textBaseline);

			position = position + p.context.getTextWidth(entry.second) + keyHeight * 2.5f;
		}

		p.context.restore();

	}

}
