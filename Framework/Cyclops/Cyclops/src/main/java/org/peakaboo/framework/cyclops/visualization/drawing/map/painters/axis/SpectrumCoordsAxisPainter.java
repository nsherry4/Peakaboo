package org.peakaboo.framework.cyclops.visualization.drawing.map.painters.axis;



import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SISize;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.Palette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;



public class SpectrumCoordsAxisPainter extends AbstractKeyCoordAxisPainter
{
	private int							spectrumSteps;
	private Palette						colourRule;
	private boolean						negativeValues;
	private List<Pair<Float, String>>	markings;
	private int 						decimalPoints;
	
	
	public SpectrumCoordsAxisPainter(boolean drawCoords, PaletteColour colour, Coord<Number> coordLoXLoY, Coord<Number> coordHiXLoY,
			Coord<Number> coordLoXHiY, Coord<Number> coordHiXHiY, SISize coordinateUnits,
			boolean drawSpectrum, int spectrumHeight, int spectrumSteps, Palette palette,
			boolean realDimensionsProvided, boolean drawScaleBar)
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

		this.markings = null;
		this.negativeValues = false;
		
		this.spectrumSteps = spectrumSteps;
		this.colourRule = palette;
		
		this.decimalPoints = 1;

	}


	public SpectrumCoordsAxisPainter(boolean drawCoords, PaletteColour colour, Coord<Number> coordLoXLoY, Coord<Number> coordHiXLoY,
			Coord<Number> coordLoXHiY, Coord<Number> coordHiXHiY, SISize coordinateUnits,
			boolean drawSpectrum, int spectrumHeight, int spectrumSteps, Palette palette,
			boolean realDimensionsProvided, boolean drawScaleBar, int decimalPoints, boolean negativeValues, List<Pair<Float, String>> markings)
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

		this.markings = new ArrayList<>(markings);
		this.negativeValues = negativeValues;

		this.spectrumSteps = spectrumSteps;
		this.colourRule = palette;
		
		this.decimalPoints = decimalPoints;
		
	}


	@Override
	protected void drawKey(final PainterData p)
	{

		p.context.save();

		int steps = spectrumSteps;

		Pair<Float, Float> spectrumBoundsX = getAxisSizeX(p);
		final float position = axesData.xPositionBounds.start + spectrumBoundsX.first;
		final float width = axesData.xPositionBounds.end - axesData.xPositionBounds.start - spectrumBoundsX.second
				- spectrumBoundsX.first;

		float increment = width / (steps * (negativeValues ? 2 : 1));

		float offsetY = axesData.yPositionBounds.end - getKeyBorderSize(p.context).y;
		if (drawCoords) offsetY += 0.3f*keyHeight;

		float spectrumPosition = position;
		float maxY = p.dr.maxYIntensity;
		float scale = maxY / steps;
		for (int i = (negativeValues ? -steps : 0); i < steps; i++) {
			p.context.rectAt(spectrumPosition, offsetY, increment + 1.0f, keyHeight);
			var source = getColourFromRules(i * scale, maxY, p.dr.viewTransform);
			p.context.setSource(source);
			p.context.fill();
			spectrumPosition += increment;

		}
		p.context.setSource(colour);

		final float textBaseline = offsetY + keyHeight + p.context.getFontLeading() + p.context.getFontAscent();
		float fontSize = p.context.getFontSize();

		if (markings == null)
		{
			String maxIntensity = SigDigits.roundFloatTo(p.dr.maxYIntensity, decimalPoints);
			String minIntensity = negativeValues ? "-" + maxIntensity : "0";

			while (width > 0.0 && fontSize > 1.0)
			{

				float expectedTextWidth = p.context.getTextWidth(minIntensity + " " + maxIntensity);
				if (expectedTextWidth < width) break;
				fontSize *= (width/expectedTextWidth) * 0.95;
				p.context.setFontSize(fontSize);
			}

			p.context.writeText(minIntensity, position, textBaseline);

			float rightEndWidth = p.context.getTextWidth(maxIntensity);
			p.context.writeText(maxIntensity, position + width - rightEndWidth, textBaseline);

		}
		else
		{
					
			
			String longestMarking = markings.stream().map(e -> e.second).reduce("", (s1, s2) -> {
				Float l1 = p.context.getTextWidth(s1);
				Float l2 = p.context.getTextWidth(s2);
				if (l1 > l2) return s1;
				return s2;
			});
			
			//keep shrinking the font size until all of the text until the font size is small enough that it fits
			while (width > 0.0 && fontSize > 1.0)
			{
				//get the width of the text for all of the markings
				float expectedTextWidth = p.context.getTextWidth(longestMarking) * markings.size();
				if (expectedTextWidth < width) break;
				
				fontSize *= (width/expectedTextWidth) * 0.95; 
				p.context.setFontSize(fontSize);

			}

			markings.stream().forEach((Pair<Float, String> element) -> {
				if (element.first > 1.0) element.first = 1.0f;
				float textWidth = p.context.getTextWidth(element.second);
				p.context.writeText(element.second, position + ((width - textWidth) * element.first), textBaseline);
			});


		}

		p.context.restore();

	}

	private PaletteColour getColourFromRules(float intensity, float maximum, ViewTransform transform) {
		if (transform == ViewTransform.LOG) {
			intensity = (float)Math.log1p(intensity);
			maximum = (float)Math.log1p(maximum);
		}
		return colourRule.getFillColour(intensity, maximum);
	}

}