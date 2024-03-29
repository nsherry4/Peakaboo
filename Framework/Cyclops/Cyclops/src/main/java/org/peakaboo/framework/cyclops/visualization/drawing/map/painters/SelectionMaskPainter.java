package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;

import java.util.logging.Level;

import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class SelectionMaskPainter extends RasterColorMapPainter {

	private int sizeX, sizeY;
	private int selectionColour;
	private IntArrayList colours;
	
	public SelectionMaskPainter(PaletteColour c, IntArrayList points, int sizeX, int sizeY) {
		super();
		this.selectionColour = c.getARGB();
		colours = new IntArrayList(sizeX * sizeY);
		configure(sizeX, sizeY, points);
	}

	public synchronized void configure(int dataWidth, int dataHeight, IntArrayList points) {
		int size = sizeX*sizeY;
		
		if (this.sizeX != dataWidth || this.sizeY != dataHeight) {
			super.buffer = null;
			this.sizeX = dataWidth;
			this.sizeY = dataHeight;
		}

		colours.clear();
		
		// don't bother updating the pixel list, we won't be drawing anything when no
		// points are in the selection.
		if (points.isEmpty()) {
			setEnabled(false);
			return;
		}
		
		PaletteColour transparent = new PaletteColour(0, 0, 0, 0);
		int transparentARGB = transparent.getARGB();
		for (int i = 0; i < size; i++) {
			colours.add(transparentARGB);
		}
		
		for (int i = 0; i < points.size(); i++) {
			int point = points.getInt(i);
			if (point >= size || point < 0) {
				CyclopsLog.get().log(Level.FINE, "Selected point " + point + " is out of bounds, ignoring");
			} else {
				colours.set(point, selectionColour);
			}
		}

		setPixels(colours);
		setEnabled(true);
		
	}
	
}
