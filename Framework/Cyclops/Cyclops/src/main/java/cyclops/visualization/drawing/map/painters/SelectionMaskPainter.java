package cyclops.visualization.drawing.map.painters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cyclops.log.CyclopsLog;
import cyclops.visualization.palette.PaletteColour;

public class SelectionMaskPainter extends RasterColorMapPainter {

	private int sizeX, sizeY;
	private PaletteColour selectionColour;
	
	public SelectionMaskPainter(PaletteColour c, List<Integer> points, int sizeX, int sizeY) {
		super();
		this.selectionColour = new PaletteColour(96, c.getRed(), c.getGreen(), c.getBlue());
		configure(sizeX, sizeY, points);	
	}

	public synchronized void configure(int dataWidth, int dataHeight, List<Integer> points) {
		if (this.sizeX != dataWidth || this.sizeY != dataHeight) {
			super.buffer = null;
		}
		this.sizeX = dataWidth;
		this.sizeY = dataHeight;
	
		// don't bother updating the pixel list, we won't be drawing anything when no
		// points are in the selection.
		if (points.size() == 0) {
			setEnabled(false);
			return;
		}
		
		PaletteColour transparent = new PaletteColour(0, 0, 0, 0);
		
		int size = sizeX*sizeY;
		List<PaletteColour> colors = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			colors.add(transparent);
		}
		for (Integer i : points) {
			if (i >= size || i < 0) {
				CyclopsLog.get().log(Level.WARNING, "Selected point " + i + " is out of bounds, ignoring");
			} else {
				colors.set(i, selectionColour);
			}
		}

		setPixels(colors);
		setEnabled(true);
		
	}
	
}
