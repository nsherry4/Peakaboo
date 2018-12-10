package cyclops.visualization.drawing.map.painters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cyclops.log.CyclopsLog;
import cyclops.visualization.palette.PaletteColour;

public class SelectionMaskPainter extends RasterColorMapPainter {

	public SelectionMaskPainter(PaletteColour c, List<Integer> points, int sizeX, int sizeY) {
		super();
		
		c = new PaletteColour(96, c.getRed(), c.getGreen(), c.getBlue());
		PaletteColour transparent = new PaletteColour(0, 0, 0, 0);
		
		List<PaletteColour> colors = new ArrayList<>();
		for (int i = 0; i < (sizeX*sizeY); i++) {
			colors.add(transparent);
		}
		for (Integer i : points) {
			if (i >= sizeX*sizeY || i < 0) {
				CyclopsLog.get().log(Level.WARNING, "Selected point " + i + " is out of bounds, ignoring");
			}
			colors.set(i, c);
		}

		setPixels(colors);
	}
	
}
