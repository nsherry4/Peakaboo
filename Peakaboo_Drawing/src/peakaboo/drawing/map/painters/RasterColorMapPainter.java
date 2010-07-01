package peakaboo.drawing.map.painters;


import java.awt.Color;
import java.util.List;

import peakaboo.calculations.Calculations;
import peakaboo.calculations.SpectrumCalculations;
import peakaboo.datatypes.GridPerspective;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.executor.implementations.SplittingTicketedTaskExecutor;
import peakaboo.drawing.backends.Buffer;
import peakaboo.drawing.map.MapDrawing;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.map.palettes.ThermalScalePalette;
import peakaboo.drawing.painters.PainterData;

/**
 * 
 * This class implements the drawing of a map using block pixel filling
 * 
 * @author Nathaniel Sherry, 2009
 */

public class RasterColorMapPainter extends MapPainter
{

	private Buffer buffer;
	public boolean useBuffer = false;
	
	private List<Color> pixels;

	public RasterColorMapPainter()
	{
		super(new ThermalScalePalette(), null);
	}


	public void setPixels(List<Color> pixels)
	{
		this.pixels = pixels;
	}
	
	@Override
	public void drawElement(PainterData p)
	{

		p.context.save();

			// get the size of the cells
			float cellSize = MapDrawing.calcCellSize(p.plotSize.x, p.plotSize.y, p.dr);
	
			
			// clip the region
			p.context.rectangle(0, 0, p.dr.dataWidth * cellSize, p.dr.dataHeight * cellSize);
			p.context.clip();
	
			
			GridPerspective<Color> grid = new GridPerspective<Color>(p.dr.dataWidth, p.dr.dataHeight, Color.gray);
			//pixels = Calculations.gridYReverse(pixels, grid);
	
			if (p.dr.drawToVectorSurface) {
				drawAsScalar(p, pixels, cellSize);
				buffer = null;
			} else {
				if (useBuffer && buffer != null){
					drawBuffer(p, buffer, cellSize);
				} else {
					buffer = drawAsRaster(p, pixels, cellSize, p.dr.dataHeight * p.dr.dataWidth);
				}
			}

		p.context.restore();

	}


	private Buffer drawAsRaster(PainterData p, final List<Color> data, float cellSize, final int maximumIndex)
	{

		final Buffer b = p.context.getImageBuffer(p.dr.dataWidth, p.dr.dataHeight);

		final Task drawPixel = new Task("Draw Raster") {

			@Override
			public boolean work(int ordinal)
			{
				if (maximumIndex > ordinal) {
					b.setPixelValue(ordinal, data.get(ordinal));
				}
				return true;
			}
		};


		new SplittingTicketedTaskExecutor(data.size(), drawPixel).executeBlocking();

		p.context.compose(b, 0, 0, cellSize);
		
		return b;
	}

	private void drawBuffer(PainterData p, Buffer b, float cellSize)
	{
		p.context.compose(b, 0, 0, cellSize);
	}

	private void drawAsScalar(PainterData p, List<Color> data, float cellSize)
	{

		// draw the map
		for (int y = 0; y < p.dr.dataHeight; y++) {
			for (int x = 0; x < p.dr.dataWidth; x++) {

				p.context.save();

				p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
				p.context.clip();

				int index = y * p.dr.dataWidth + x;
				p.context.rectangle(x * cellSize, y * cellSize, cellSize + 1, cellSize + 1);
				p.context.setSource(data.get(index));
				p.context.fill();

				p.context.restore();
			}
		}
	}

	
	public void clearBuffer()
	{
		buffer = null;
	}
	
}
