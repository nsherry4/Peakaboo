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
	private Coord<Integer> lastOffset;
	
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
			context.restore();
		} else if (doBuffer) {

			Buffer buffer = bufferer.get(size.x, size.y);
			int currentOffsetX = calculateHorizontalCenteringOffset(size);
			boolean offsetChanged = lastOffset == null || lastOffset.x != currentOffsetX;
			boolean needsRedraw = buffer == null || lastSize == null || !lastSize.equals(size) || invalidated || offsetChanged;

			//if there is no cached buffer meeting our size requirements, create it and draw to it
			if (needsRedraw) {
				if (buffer == null) {
					buffer = bufferer.create(context);
				}
				// Clear the buffer first to remove any old content
				buffer.clear();

				// Draw map to buffer at original position
				mapmode.draw(size, data, settings, buffer, spectrumSteps);
				lastSize = new Coord<>(size);
				lastOffset = new Coord<>(currentOffsetX, 0);
				invalidated = false;
			}

			context.save();
				context.rectAt(0, 0, size.x, size.y);
				context.clip();
				// Compose buffer with horizontal centering offset applied
				context.compose(buffer, currentOffsetX, 0, 1f);
			context.restore();

		} else {
			lastSize = null;

			// Apply horizontal centering transform
			int offsetX = calculateHorizontalCenteringOffset(size);

			context.save();
				context.translate(offsetX, 0);
				mapmode.draw(size, data, settings, context, spectrumSteps);
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
		if (deep) {
			mapmode.invalidate();
		}
		this.invalidated = true;
		// Also clear the buffer cache to force complete redraw - force resize to clear cache
		this.bufferer.resize(1, 1);
		this.lastSize = null;
		this.lastOffset = null;
	}

}
