package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;


import java.util.List;
import java.util.stream.IntStream;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.Palette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * 
 * This class implements the drawing of a map using block pixel filling
 * 
 * @author Nathaniel Sherry, 2009
 */

public class RasterSpectrumMapPainter extends SpectrumMapPainter
{

	protected Buffer buffer;

	
	public RasterSpectrumMapPainter(List<Palette> colourRules, Spectrum data)
	{
		super(colourRules, data);	
	}


	public RasterSpectrumMapPainter(Palette colourRule, Spectrum data)
	{
		super(colourRule, data);
	}
	

	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{
		
		p.context.save();

			boolean isVector = p.dr.drawToVectorSurface;

			// Fast path for UI frame rate
			if (!isVector && buffer != null) {
				// Raster backend that we already have buffered
				p.context.compose(buffer, 0, 0, cellSize);
			} else {

				// We don't want to spend time on this unless we're really drawing
				Spectrum modData = transformDataForMap(p.dr, data);
				float maxIntensity = calcMaxIntensity(p);

				if (isVector) {
					// Vector backend
					drawAsScalar(p, modData, cellSize, maxIntensity);
				} else {
					// Raster backend, but no buffer
					buffer = drawAsRaster(p, modData, maxIntensity, p.dr.dataHeight * p.dr.dataWidth);
					p.context.compose(buffer, 0, 0, cellSize);
				}

			}

		p.context.restore();

	}
	
	public float calcMaxIntensity(PainterData p) {
		if (p.dr.maxYIntensity <= 0) {
			return data.max();
		} else {
			return p.dr.maxYIntensity;
		}
	}


	private Buffer drawAsRaster(PainterData p, final Spectrum data, float maxIntensity,
			final int maximumIndex)
	{

		final Buffer b = p.context.getImageBuffer(p.dr.dataWidth, p.dr.dataHeight);

		int size = Math.min(maximumIndex, data.size());
		if (p.dr.viewTransform == ViewTransform.LOG) {
			//intensity will already have been log'd, we just have to log the max
			maxIntensity = (float) Math.log1p(maxIntensity);
		}
		
		for (int ordinal = 0; ordinal < size; ordinal++) {
			b.setPixelValue(ordinal, getColourFromRules(data.get(ordinal), maxIntensity));
		}	
		
		return b;
	}


	private void drawAsScalar(PainterData p, Spectrum data, float cellSize, float maxIntensity)
	{
		float intensity;
		PaletteColour c;
		int index;

		p.context.save();
		
		
		if (p.dr.viewTransform == ViewTransform.LOG) {
			//intensity will already have been log'd, we just have to log the max
			maxIntensity = (float) Math.log1p(maxIntensity);
		}
		
		// draw the map
		for (int y = 0; y < p.dr.dataHeight; y++) {
			for (int x = 0; x < p.dr.dataWidth; x++) {

				index = y * p.dr.dataWidth + x;
				intensity = data.get(index);

				c = getColourFromRules(intensity, maxIntensity);

				p.context.rectAt(x * cellSize, y * cellSize, cellSize + 1, cellSize + 1);

				p.context.setSource(c);
				p.context.fill();
				
			}
		}
		
	
		p.context.restore();
	}


	@Override
	public boolean isBufferingPainter()
	{
		return true;
	}


	@Override
	public void clearBuffer()
	{
		buffer = null;
	}

	
}
