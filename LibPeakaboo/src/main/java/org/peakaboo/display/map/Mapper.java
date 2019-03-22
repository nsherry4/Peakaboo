package org.peakaboo.display.map;


import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.map.MapDrawing;
import org.peakaboo.framework.cyclops.visualization.palette.Spectrums;

public class Mapper {

	private MapMode mapmode;
	
	public Mapper() {
		mapmode = new CompositeMapMode();
	}

	public void draw(MapRenderData data, MapRenderSettings settings, Surface context, Coord<Integer> size) {
			
		if (settings == null) {
			settings = new MapRenderSettings();
		}

		final int spectrumSteps = (settings.contours) ? settings.contourSteps : Spectrums.DEFAULT_STEPS;
		
		if (mapmode.getMode() != settings.mode) {
			mapmode = settings.mode.getMapper();
		}
		
		mapmode.draw(size, data, settings, context, spectrumSteps);

	}

	public MapDrawing getMap() {
		return mapmode.getMap();
	}
	
	public Coord<Integer> getCoordinate(float x, float y, boolean allowOutOfBounds) {
		return getMap().getMapCoordinateAtPoint(x, y, allowOutOfBounds);
	}
	
	public void setNeedsRedraw() {
		mapmode.invalidate();
	}

}
