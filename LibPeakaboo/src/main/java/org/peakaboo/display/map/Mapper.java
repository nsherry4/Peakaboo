package org.peakaboo.display.map;


import java.util.LinkedHashMap;
import java.util.Map;

import org.peakaboo.display.Display;
import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.display.map.modes.MapModeRegistry;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.ManagedBuffer;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.map.MapDrawing;
import org.peakaboo.framework.cyclops.visualization.palette.Gradient;

public class Mapper {

	private boolean invalidated;
	private ManagedBuffer bufferer = new ManagedBuffer(Display.OVERSIZE);
	private Coord<Integer> lastSize;
	
	private Map<String, MapMode> modecache = new LinkedHashMap<>();
	private MapMode mapmode;
	
	public Mapper() {
		for (var key : MapModeRegistry.get().typeNames()) {
			modecache.put(key, MapModeRegistry.get().create(key, null));
		}
		mapmode = modecache.get(MapModeRegistry.get().defaultType());
	}

	public void draw(MapRenderData data, MapRenderSettings settings, Surface context, Coord<Integer> size) {
			
		if (settings == null) {
			settings = new MapRenderSettings();
		}

		final int spectrumSteps = (settings.contours) ? settings.contourSteps : Gradient.DEFAULT_STEPS;
		
		if (! mapmode.mapModeName().equals(settings.mode)) {
			mapmode = modecache.get(settings.mode);
		}
		
		// Update dimensions to sync DrawingRequest with current component size
		// We must set this early so that when we calculate the horizontal offset
		// required to centre the map in the view, we get the new, correct values
		size = mapmode.setDimensions(settings, size);
		
		/*
		 * Determine if we want to buffer the drawing or not. When memory is tight, we
		 * take the performance hit to save space
		 */
		boolean doBuffer = Display.useBuffer(size);

		// Because we horizontally centre the map, we need to paint the background blank here
		// because the map drawing won't always blank out the whole component
		context.save();
		context.rectAt(0, 0, (float)size.x, (float)size.y);
		context.setSource(settings.getBg());
		context.fill();
		context.restore();

		if (context.getSurfaceDescriptor().isVector()) {
			//We can't do raster-based buffering if the drawing target is vector
			//so just draw directly to the surface

			// Apply horizontal centering transform
			int offsetX = calculateHorizontalCenteringOffset(size);
			context.save();
				context.translate(offsetX, 0);
				mapmode.draw(size, data, settings, context, spectrumSteps);
				mapmode.drawSelection(size, data, settings, context);
			context.restore();
		} else if (doBuffer) {

			Buffer buffer = bufferer.get(size.x, size.y);
			boolean needsRedraw = buffer == null || lastSize == null || !lastSize.equals(size) || invalidated;

			//The base map (everything except the selection overlay) is cached in the
			//buffer and only rebuilt when the data or size changes. A selection-only
			//change skips this and just recomposites the overlay below.
			if (needsRedraw) {
				if (buffer == null) {
					buffer = bufferer.create(context);
				}
				// Clear the buffer first to remove any old content
				buffer.clear();

				// Draw the base map to the buffer at the original position
				mapmode.draw(size, data, settings, buffer, spectrumSteps);
				lastSize = new Coord<>(size);
				invalidated = false;
			}

			// Compute the centering offset *after* the base map has been (re)built. The
			// title/border geometry that calcTotalSize() relies on is only refreshed as a
			// side-effect of mapmode.draw(), so computing the offset earlier would use the
			// previous frame's layout and misalign the map on a layout-affecting change
			// (e.g. toggling the title) until a later repaint. The buffer is always drawn
			// at the origin and centering is applied only at compose time, so the offset is
			// independent of the buffer contents and is safe to compute last.
			int currentOffsetX = calculateHorizontalCenteringOffset(size);

			context.save();
				context.rectAt(0, 0, size.x, size.y);
				context.clip();
				// Compose the cached base map with the horizontal centering offset...
				context.compose(buffer, currentOffsetX, 0, 1f);
				// ...then draw the live selection overlay on top at the same offset.
				context.translate(currentOffsetX, 0);
				mapmode.drawSelection(size, data, settings, context);
			context.restore();

		} else {
			lastSize = null;

			// Apply horizontal centering transform
			int offsetX = calculateHorizontalCenteringOffset(size);

			context.save();
				context.translate(offsetX, 0);
				mapmode.draw(size, data, settings, context, spectrumSteps);
				mapmode.drawSelection(size, data, settings, context);
			context.restore();
		}

	}

	public MapDrawing getMap() {
		return mapmode.getMap();
	}
	
	private int calculateHorizontalCenteringOffset(Coord<Integer> canvasSize) {
		Coord<Float> mapSize = getMap().calcTotalSize();
		return Math.max(0, (int)((canvasSize.x - mapSize.x) / 2));
	}
	
	public Coord<Integer> getCoordinate(float x, float y, boolean allowOutOfBounds, Coord<Integer> canvasSize) {
		// Adjust coordinates to account for horizontal centering offset
		int offsetX = calculateHorizontalCenteringOffset(canvasSize);
		return getMap().getMapCoordinateAtPoint(x - offsetX, y, allowOutOfBounds);
	}
	
	/**
	 * Indicates that this map needs to be redrawn.
	 * 
	 * @param deep indicates that the map data itself has changed and the map cannot
	 *             simply be recomposited
	 */
	public void setNeedsRedraw(boolean deep) {
		/*
		 * A deep change (data, dimensions, palette, ...) invalidates the cached base map
		 * and the per-painter buffers. A shallow change (deep == false) is selection-only:
		 * the base map is untouched and the selection overlay is simply recomposited on
		 * top of the cached base on the next paint, so there is nothing to invalidate here.
		 */
		if (deep) {
			mapmode.invalidate();
			this.invalidated = true;
			// Also clear the buffer cache to force complete redraw - force resize to clear cache
			this.bufferer.resize(1, 1);
			this.lastSize = null;
		}
	}

}
