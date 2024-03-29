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
import org.peakaboo.framework.cyclops.visualization.palette.Spectrums;

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

		final int spectrumSteps = (settings.contours) ? settings.contourSteps : Spectrums.DEFAULT_STEPS;
		
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
			mapmode.draw(size, data, settings, context, spectrumSteps);
		} else if (doBuffer) {

			Buffer buffer = bufferer.get(size.x, size.y);
			boolean needsRedraw = buffer == null || lastSize == null || !lastSize.equals(size) || invalidated;
			//if there is no cached buffer meeting our size requirements, create it and draw to it
			if (needsRedraw) {
				if (buffer == null) {
					buffer = bufferer.create(context);
				}
				mapmode.draw(size, data, settings, buffer, spectrumSteps);
				lastSize = new Coord<>(size);
				invalidated = false;
			}
						
			context.rectAt(0, 0, size.x, size.y);
			context.clip();
			context.compose(buffer, 0, 0, 1f);
		} else {
			lastSize = null;
			mapmode.draw(size, data, settings, context, spectrumSteps);
		}

	}

	public MapDrawing getMap() {
		return mapmode.getMap();
	}
	
	public Coord<Integer> getCoordinate(float x, float y, boolean allowOutOfBounds) {
		return getMap().getMapCoordinateAtPoint(x, y, allowOutOfBounds);
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
	}

}
