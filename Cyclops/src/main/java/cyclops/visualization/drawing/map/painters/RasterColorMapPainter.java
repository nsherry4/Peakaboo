package cyclops.visualization.drawing.map.painters;


import java.util.List;
import java.util.stream.IntStream;

import cyclops.visualization.Buffer;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.palette.PaletteColour;
import cyclops.visualization.palette.palettes.SingleColourPalette;
import cyclops.visualization.template.Rectangle;

/**
 * 
 * This class implements the drawing of a map using block pixel filling
 * 
 * @author Nathaniel Sherry, 2009
 */

public class RasterColorMapPainter extends MapPainter
{
	
	private List<PaletteColour> 	pixels;
	protected Buffer 				buffer;


	public RasterColorMapPainter()
	{
		super(new SingleColourPalette(new PaletteColour(0, 0, 0, 0)));
	}


	public void setPixels(List<PaletteColour> pixels)
	{
		this.pixels = pixels;
	}
	
	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{

		p.context.save();

	
			if (p.dr.drawToVectorSurface) {
				drawAsScalar(p, pixels, cellSize);
				buffer = null;
			} else {
				
				if (buffer == null) {
					buffer = drawAsRaster(p, pixels, cellSize, p.dr.dataHeight * p.dr.dataWidth);
				}
				p.context.compose(buffer, 0, 0, cellSize);
				
			}

		p.context.restore();

	}


	private Buffer drawAsRaster(PainterData p, final List<PaletteColour> data, float cellSize, final int maximumIndex)
	{

		final Buffer b = p.context.getImageBuffer(p.dr.dataWidth, p.dr.dataHeight);

		final PaletteColour transparent = new PaletteColour(0x00000000);
		
		IntStream.range(0, data.size()).parallel().forEach(ordinal -> {		
			if (maximumIndex > ordinal) {
				PaletteColour c = data.get(ordinal);
				if (c == null) c = transparent;
				b.setPixelValue(ordinal, c);
			}
		});

		p.context.compose(b, 0, 0, cellSize);
		
		return b;
	}


	private void drawAsScalar(PainterData p, List<PaletteColour> data, float cellSize)
	{

		p.context.save();

		p.context.addShape(new Rectangle(0, 0, p.plotSize.x, p.plotSize.y));
		p.context.clip();
		
		// draw the map
		for (int y = 0; y < p.dr.dataHeight; y++) {
			for (int x = 0; x < p.dr.dataWidth; x++) {



				int index = y * p.dr.dataWidth + x;
				p.context.addShape(new Rectangle(x * cellSize, y * cellSize, cellSize + 1, cellSize + 1));
				p.context.setSource(data.get(index));
				p.context.fill();

				
			}
		}
		
		p.context.restore();
	}

	
	public void clearBuffer()
	{
		buffer = null;
	}


	@Override
	public boolean isBufferingPainter()
	{
		return true;
	}
	
}
