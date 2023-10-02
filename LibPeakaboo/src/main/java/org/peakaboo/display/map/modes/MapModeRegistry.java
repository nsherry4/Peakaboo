package org.peakaboo.display.map.modes;

import org.peakaboo.app.Registry;
import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.display.map.modes.correlation.CorrelationMapMode;
import org.peakaboo.display.map.modes.overlay.OverlayMapMode;
import org.peakaboo.display.map.modes.ratio.RatioMapMode;

public class MapModeRegistry extends Registry<Void, MapMode> {

	private static MapModeRegistry system = new MapModeRegistry();

	public static MapModeRegistry get() {
		return system;
	}
	
	static {
		get().register(CompositeMapMode.MODE_NAME, arg -> new CompositeMapMode());
		get().register(OverlayMapMode.MODE_NAME, arg -> new OverlayMapMode());
		get().register(RatioMapMode.MODE_NAME, arg -> new RatioMapMode());
		get().register(CorrelationMapMode.MODE_NAME, arg -> new CorrelationMapMode());
	}

}
