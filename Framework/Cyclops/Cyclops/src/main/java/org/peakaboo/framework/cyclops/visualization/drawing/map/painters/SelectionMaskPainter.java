package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;

import java.util.Arrays;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class SelectionMaskPainter extends RasterColorMapPainter {

	private static final int TRANSPARENT_ARGB = 0;

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
		if (this.sizeX != dataWidth || this.sizeY != dataHeight) {
			super.buffer = null;
			this.sizeX = dataWidth;
			this.sizeY = dataHeight;
		}

		// Size is derived from the incoming dimensions, not the (possibly stale) fields.
		int size = sizeX * sizeY;

		// don't bother updating the pixel list, we won't be drawing anything when no
		// points are in the selection.
		if (points.isEmpty()) {
			setEnabled(false);
			return;
		}

		// Reuse the backing array rather than reallocating: ensure it's the right
		// length, reset every pixel to transparent (ARGB 0), then mark selected points.
		colours.size(size);
		Arrays.fill(colours.elements(), 0, size, TRANSPARENT_ARGB);

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
