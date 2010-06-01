package peakaboo.drawing.map.painters;


import java.util.List;

import peakaboo.calculations.Calculations;
import peakaboo.calculations.ListCalculations;
import peakaboo.datatypes.GridPerspective;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.executor.implementations.SplittingTicketedTaskExecutor;
import peakaboo.drawing.backends.Buffer;
import peakaboo.drawing.map.Map;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.painters.PainterData;

/**
 * 
 * This class implements the drawing of a map using block pixel filling
 * 
 * @author Nathaniel Sherry, 2009
 */

public class ThreadedRasterMapPainter extends MapPainter
{

	private Buffer buffer;
	public boolean useBuffer = false;

	public ThreadedRasterMapPainter(List<AbstractPalette> colourRules, List<Double> data)
	{
		super(colourRules, data);
	}


	public ThreadedRasterMapPainter(AbstractPalette colourRule, List<Double> data)
	{
		super(colourRule, data);
	}


	@Override
	public void drawElement(PainterData p)
	{

		p.context.save();

		List<Double> modData = data;
		double maxIntensity;
		if (p.dr.maxYIntensity <= 0) {
			maxIntensity = ListCalculations.max(data);
		} else {
			maxIntensity = p.dr.maxYIntensity;
		}


		// get the size of the cells
		double cellSize = Map.calcCellSize(p.plotSize.x, p.plotSize.y, p.dr);

		// clip the region
		p.context.rectangle(0, 0, p.dr.dataWidth * cellSize, p.dr.dataHeight * cellSize);
		p.context.clip();

		GridPerspective<Double> grid = new GridPerspective<Double>(p.dr.dataWidth, p.dr.dataHeight, 0.0);
		modData = Calculations.gridYReverse(modData, grid);

		if (p.dr.drawToVectorSurface) {
			drawAsScalar(p, modData, cellSize, maxIntensity);
			buffer = null;
		} else {
			if (useBuffer && buffer != null){
				drawBuffer(p, buffer, cellSize);
			} else {
				buffer = drawAsRaster(p, modData, cellSize, maxIntensity, p.dr.dataHeight * p.dr.dataWidth);
			}
		}

		p.context.restore();

	}


	private Buffer drawAsRaster(PainterData p, final List<Double> data, double cellSize, final double maxIntensity,
			final int maximumIndex)
	{

		final Buffer b = p.context.getImageBuffer(p.dr.dataWidth, p.dr.dataHeight);

		final Task drawPixel = new Task("Draw Raster") {

			@Override
			public boolean work(int ordinal)
			{
				if (maximumIndex > ordinal) {
					b.setPixelValue(ordinal, getColourFromRules(data.get(ordinal), maxIntensity));
				}
				return true;
			}
		};


		new SplittingTicketedTaskExecutor(data.size(), drawPixel).executeBlocking();

		p.context.compose(b, 0, 0, cellSize);
		
		return b;
	}

	private void drawBuffer(PainterData p, Buffer b, double cellSize)
	{
		p.context.compose(b, 0, 0, cellSize);
	}

	private void drawAsScalar(PainterData p, List<Double> data, double cellSize, final double maxIntensity)
	{
		double intensity;

		// draw the map
		for (int y = 0; y < p.dr.dataHeight; y++) {
			for (int x = 0; x < p.dr.dataWidth; x++) {

				p.context.save();

				p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
				p.context.clip();

				int index = y * p.dr.dataWidth + x;
				intensity = data.get(index);
				p.context.rectangle(x * cellSize, y * cellSize, cellSize + 1, cellSize + 1);
				p.context.setSource(getColourFromRules(intensity, maxIntensity));
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
