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



public class SpectrumCoordsAxisPainter extends AbstractKeyCoordAxisPainter
{
	private int							spectrumSteps;
	private List<AbstractPalette>		colourRules;
	private boolean						negativeValues;
	private List<Pair<Double, String>>	markings;


	public SpectrumCoordsAxisPainter(boolean drawCoords, Coord<Number> topLeftCoord, Coord<Number> topRightCoord,
			Coord<Number> bottomLeftCoord, Coord<Number> bottomRightCoord, SISize coordinateUnits,
			boolean drawSpectrum, int spectrumHeight, int spectrumSteps, List<AbstractPalette> palettes,
			boolean realDimensionsProvided)
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

		this.markings = null;
		this.negativeValues = false;
		
		this.spectrumSteps = spectrumSteps;
		this.colourRules = palettes;

	}
	
	public SpectrumCoordsAxisPainter(boolean drawCoords, Coord<Number> topLeftCoord, Coord<Number> topRightCoord,
			Coord<Number> bottomLeftCoord, Coord<Number> bottomRightCoord, SISize coordinateUnits,
			boolean drawSpectrum, int spectrumHeight, int spectrumSteps, List<AbstractPalette> palettes,
			boolean realDimensionsProvided, boolean negativeValues, List<Pair<Double, String>> markings)
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

		this.markings = markings;
		this.negativeValues = negativeValues;
		
		this.spectrumSteps = spectrumSteps;
		this.colourRules = palettes;

	}


	protected void drawKey(final PainterData p)
	{

		p.context.save();

		int steps = spectrumSteps;

		Pair<Double, Double> spectrumBoundsX = getAxisSizeX(p);
		final double position = axesData.xPositionBounds.start + spectrumBoundsX.first;
		final double width = axesData.xPositionBounds.end - axesData.xPositionBounds.start - spectrumBoundsX.second
				- spectrumBoundsX.first;

		double increment = width / (steps * (negativeValues ? 2 : 1));

		double offsetY = axesData.yPositionBounds.end - getKeyBorderSize(p.context).y;
		if (drawCoords) offsetY += keyHeight;

		double spectrumPosition = position;
		for (int i = (negativeValues ? -steps : 0); i < steps; i++)
		{

			p.context.rectangle(spectrumPosition, offsetY, increment + 1.0, keyHeight);
			p.context.setSource(getColourFromRules(i, steps));
			p.context.fill();
			spectrumPosition += increment;

		}
		p.context.setSource(0, 0, 0);

		String intens;
		final double textBaseline = offsetY + keyHeight + p.context.getFontLeading() + p.context.getFontAscent();
		double textLineHeight = p.context.getFontHeight();
		double fontSize = p.context.getFontSize();

		if (markings == null)
		{
			String maxIntensity = String.valueOf((int) p.dr.maxYIntensity);
			String minIntensity = negativeValues ? maxIntensity : "0";
			intens = "Intensity (counts)";

			while (width > 0.0 && fontSize > 1.0)
			{

				double expectedTextWidth = p.context.getTextWidth(maxIntensity + " " + intens + " " + maxIntensity);
				if (expectedTextWidth < width) break;
				fontSize *= (width/expectedTextWidth) * 0.95;
				p.context.setFontSize(fontSize);
			}

			p.context.writeText(minIntensity, position, textBaseline);

			double rightEndWidth = p.context.getTextWidth(maxIntensity);
			p.context.writeText(maxIntensity, position + width - rightEndWidth, textBaseline);

		}
		else
		{
			intens = "Intensity (ratio)";

			
			String markingsText;
			//concatenate the list of strings to display so we can check the width of the total string
			markingsText = Functional.foldr(markings, "", new Function2<Pair<Double, String>, String, String>() {

				public String f(Pair<Double, String> marking, String str)
				{
					return str + marking.second;
				}
			}) + "";
			//keep shrinking the font size until all of the text until the font size is small enough that it fits
			while (width > 0.0 && fontSize > 1.0)
			{
				//get the width of the text for all of the markings
				double expectedTextWidth = p.context.getTextWidth(markingsText);
				if (expectedTextWidth < width) break;
				
				fontSize *= (width/expectedTextWidth) * 0.95; 
				p.context.setFontSize(fontSize);

			}

			Functional.each(markings, new Function1<Pair<Double, String>, Object>() {

				public Object f(Pair<Double, String> element)
				{
					if (element.first > 1.0) element.first = 1.0;
					double textWidth = p.context.getTextWidth(element.second);
					p.context.writeText(element.second, position + ((width - textWidth) * element.first), textBaseline);
					return null;
				}
			});


		}
		
		double centerWidth = p.context.getTextWidth(intens);
		p.context.writeText(intens, position + (width - centerWidth) / 2.0, textBaseline + textLineHeight);

		p.context.restore();

	}


	public Color getColourFromRules(double intensity, double maximum)
	{

		Color c;

		for (AbstractPalette r : colourRules)
		{
			c = r.getFillColour(intensity, maximum);
			if (c != null) return c;
		}

		return new Color(0.0f, 0.0f, 0.0f, 0.0f);

	}

}
