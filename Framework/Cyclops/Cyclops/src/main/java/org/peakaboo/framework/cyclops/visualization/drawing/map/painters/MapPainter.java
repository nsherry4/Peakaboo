package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;


import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.map.MapDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.Painter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.Palette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * 
 * A MapPainter is a specific way of drawing the {@link MapDrawing}
 * 
 * @author Nathaniel Sherry, 2009
 * @see MapDrawing
 *
 */

public abstract class MapPainter extends Painter
{

	
	protected List<Palette>	colourRules;


	protected MapPainter(List<Palette> colourRules)
	{
		this.colourRules = colourRules;
	}
	
	protected MapPainter(Palette colourRule)
	{
		List<Palette> rules = new ArrayList<>();
		rules.add(colourRule);
		this.colourRules = rules;
	}


	@Override
	protected float getBaseUnitSize(org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest dr)
	{
		// TODO Auto-generated method stub
		return 1;
	}
	
	public PaletteColour getColourFromRules(double intensity, double maximum)
	{

		PaletteColour c;
		
		for (Palette r : colourRules) {
			c = r.getFillColour(intensity, maximum);
			if (c != null) return c;
		}

		return new PaletteColour(0x00000000);

	}
	
	public void setPalette(Palette palette)
	{
		colourRules.clear();
		colourRules.add(palette);
	}
	public void setPalettes(List<Palette> palettes)
	{
		colourRules.clear();
		colourRules.addAll(palettes);
	}


	
	
	@Override
	public final void drawElement(PainterData p)
	{
		
		p.context.save();
			
			// get the size of the cells
			float cellSize = MapDrawing.calcInterpolatedCellSize(p.plotSize.x, p.plotSize.y, p.dr);
			float rawCellSize = MapDrawing.calcUninterpolatedCellSize(p.plotSize.x, p.plotSize.y, p.dr);
	
			// clip the region
			p.context.rectAt(0, 0, p.dr.dataWidth * cellSize, p.dr.dataHeight * cellSize);
			p.context.clip();
			
			drawMap(p, cellSize, rawCellSize);
			
		p.context.restore();
		
	}
	
	public abstract void drawMap(PainterData p, float cellSize, float rawCellSize);
	
	protected Spectrum transformDataForMap(DrawingRequest dr, SpectrumView data)
	{
		Spectrum transformedData = new ArraySpectrum(data);
		
		//log transform
		if (dr.viewTransform == ViewTransform.LOG) transformedData = SpectrumCalculations.logList(transformedData);
		
		//vertical orientation flip
		if (!dr.screenOrientation) {
			/*
			 * the screenOrientation setting puts the origin (0,0) in the top left rather
			 * than in the bottom left. BUT, having the origin in the top left is the
			 * cyclops-native format. So when flipY is set, we don't have to do anything.
			 * It's when it's not set that we have to flip it...
			 */
			int width = dr.dataWidth;
			int height = dr.dataHeight;
			
			Spectrum flip = new ArraySpectrum(transformedData.size());
			float[] transformedDataArray = transformedData.backingArray();
			float[] flipArray = flip.backingArray();
			for (int y = 0; y < height; y++) {
				//Copy the memory row by row, since we know that's how this reversal works
				System.arraycopy(transformedDataArray, width*y, flipArray, width*(height-1-y), width);
			}
			transformedData = flip;
			
		}		
		
		return transformedData;
	}
	
	public abstract boolean isBufferingPainter();
	public abstract void clearBuffer();
	
}
