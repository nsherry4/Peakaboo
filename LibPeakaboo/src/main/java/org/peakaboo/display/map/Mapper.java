package org.peakaboo.display.map;


import java.util.LinkedHashMap;
import java.util.Map;

import org.peakaboo.display.Display;
import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.display.map.modes.MapModeRegistry;
import org.peakaboo.framework.cyclops.Coord;
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
		
		/*
		 * Determine if we want to buffer the drawing or not. When memory is tight, we
		 * take the performance hit to save space
		 */
		boolean doBuffer = Display.useBuffer(size);
		
		
		
		if (context.getSurfaceDescriptor().isVector()) {
			//We can't do raster-based buffering if the drawing target is vector
			//so just draw directly to the surface
			
			// Apply centering transform
			Coord<Integer> offset = calculateCenteringOffset(size);
			context.save();
			context.translate(offset.x, offset.y);
			mapmode.draw(size, data, settings, context, spectrumSteps);
			context.restore();
		} else if (doBuffer) {

			Buffer buffer = bufferer.get(size.x, size.y);
			Coord<Integer> currentOffset = calculateCenteringOffset(size);
			boolean offsetChanged = lastOffset == null || !lastOffset.equals(currentOffset);
			boolean needsRedraw = buffer == null || lastSize == null || !lastSize.equals(size) || invalidated || offsetChanged;
			
			//if there is no cached buffer meeting our size requirements, create it and draw to it
			if (needsRedraw) {
				if (buffer == null) {
					buffer = bufferer.create(context);
				}
				// Clear the buffer first to remove any old content
				buffer.clear();
				
				// Apply centering when drawing to buffer
				buffer.save();
				buffer.translate(currentOffset.x, currentOffset.y);
				mapmode.draw(size, data, settings, buffer, spectrumSteps);
				buffer.restore();
				lastSize = new Coord<>(size);
				lastOffset = new Coord<>(currentOffset);
				invalidated = false;
			}
						
			context.rectAt(0, 0, size.x, size.y);
			context.clip();
			
			// Buffer already has centering applied, compose at origin
			context.compose(buffer, 0, 0, 1f);
		} else {
			lastSize = null;
			
			// Apply centering transform
			Coord<Integer> offset = calculateCenteringOffset(size);
			context.save();
			context.translate(offset.x, offset.y);
			mapmode.draw(size, data, settings, context, spectrumSteps);
			context.restore();
		}

	}

	public MapDrawing getMap() {
		return mapmode.getMap();
	}
	
	private Coord<Integer> calculateCenteringOffset(Coord<Integer> canvasSize) {
		Coord<Float> mapSize = getMap().calcTotalSize();
		int offsetX = Math.max(0, (int)((canvasSize.x - mapSize.x) / 2));
		int offsetY = Math.max(0, (int)((canvasSize.y - mapSize.y) / 2));
		return new Coord<>(offsetX, offsetY);
	}
	
	public Coord<Integer> getCoordinate(float x, float y, boolean allowOutOfBounds, Coord<Integer> canvasSize) {
		// Adjust coordinates to account for centering offset
		Coord<Integer> offset = calculateCenteringOffset(canvasSize);
		return getMap().getMapCoordinateAtPoint(x - offset.x, y - offset.y, allowOutOfBounds);
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
