package peakaboo.drawing.map.painters;


import java.util.List;

import peakaboo.calculations.ListCalculations;
import peakaboo.drawing.backends.Buffer;
import peakaboo.drawing.map.MapDrawing;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.painters.PainterData;

/**
 * @author Nathaniel Sherry, 2009
 * 
 *         This class implements the drawing of a map using block pixel filling
 * 
 */

public class RasterMapPainter extends MapPainter
{

	private boolean yFlip;
	
	public RasterMapPainter(List<AbstractPalette> colourRules, List<Double> data, boolean yFlip)
	{
		super(colourRules, data);
		this.yFlip = yFlip;
	}


	@Override
	public void drawElement(PainterData p)
	{

		p.context.save();

		List<Double> modData;
		if (p.dr.maxYIntensity <= 0){
			modData = ListCalculations.normalize(data);	
		} else {
			modData = ListCalculations.divideBy(data, p.dr.maxYIntensity);
		}
		

		// get the size of the cells
		double cellSize = MapDrawing.calcCellSize(p.plotSize.x, p.plotSize.y, p.dr);
		
		// clip the region
		p.context.rectangle(0, 0, p.dr.dataWidth * cellSize, p.dr.dataHeight * cellSize);
		p.context.clip();


		double intensity;
		

		if (p.dr.drawToVectorSurface){
			// draw the map
			for (int y = 0; y < p.dr.dataHeight; y++) {

				int y_reverse;
				if (yFlip == true) {
					y_reverse = p.dr.dataHeight - 1 - y;
				} else {
					y_reverse = y;
				}
				
				for (int x = 0; x < p.dr.dataWidth; x++) {
					
					int index = y * p.dr.dataWidth + x;
					intensity = modData.get(index);
					p.context.rectangle(x, y_reverse, cellSize, cellSize);
					p.context.setSource(getColourFromRules(intensity, p.dr.maxYIntensity));
					p.context.fill();
				}
			}
			
			
		}else {
			Buffer b = p.context.getImageBuffer(p.dr.dataWidth, p.dr.dataHeight);
			
			// draw the map
			for (int y = 0; y < p.dr.dataHeight; y++) {

				int y_reverse;
				if (yFlip == true) {
					y_reverse = p.dr.dataHeight - 1 - y;
				} else {
					y_reverse = y;
				}
			
				for (int x = 0; x < p.dr.dataWidth; x++) {
					int index = y * p.dr.dataWidth + x;
					intensity = modData.get(index);
					b.setPixelValue(x, y_reverse, getColourFromRules(intensity, p.dr.maxYIntensity));
				}
			}
			p.context.compose(b, 0, 0, cellSize);
		}


		p.context.restore();

	}




}
