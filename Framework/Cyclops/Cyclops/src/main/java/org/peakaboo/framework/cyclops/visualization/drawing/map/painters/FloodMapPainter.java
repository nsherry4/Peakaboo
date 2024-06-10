package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;


import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.SingleColourPalette;


/**
 * 
 * This class implements the drawing of a map using block pixel filling
 * 
 * @author Nathaniel Sherry, 2009
 */

public class FloodMapPainter extends MapPainter
{

	private PaletteColour c;
	
	public FloodMapPainter(PaletteColour c)
	{
		super(new SingleColourPalette(c));
		
		this.c = c;
		
	}


	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{

		p.context.save();
	
			p.context.setSource(c);
			
			p.context.rectAt(0, 0, p.dr.dataWidth * cellSize, p.dr.dataHeight * cellSize);
			p.context.fill();

		p.context.restore();

	}

	
	@Override
	public void clearBuffer()
	{
	}


	@Override
	public boolean isBufferingPainter()
	{
		return false;
	}
	
}
