package org.peakaboo.display.map;


import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.display.map.modes.overlay.OverlayMapMode;
import org.peakaboo.display.map.modes.ratio.RatioMapMode;
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
			switch(settings.mode) {
			case COMPOSITE:
				mapmode = new CompositeMapMode();
				break;
			case OVERLAY:
				mapmode = new OverlayMapMode();
				break;
			case RATIO:
				mapmode = new RatioMapMode();
				break;
			default:
				throw new IllegalArgumentException("Unknown Map Mode");		
			}
		}
		
		//TODO: remove context.isvecotrsurface as argument
		mapmode.draw(size, data, settings, context, spectrumSteps);

	}

	public MapDrawing getMap() {
		return mapmode.getMap();
	}
	
	public Coord<Integer> getCoordinate(float x, float y, boolean allowOutOfBounds) {
		return mapmode.getCoordinate(x, y, allowOutOfBounds);
	}
	
	public void setNeedsRedraw() {
		mapmode.invalidate();
	}

}
