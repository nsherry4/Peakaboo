package org.peakaboo.ui.swing.mapping.sidebar.modes;

import javax.swing.JPanel;

import org.peakaboo.app.Registry;
import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.display.map.modes.correlation.CorrelationMapMode;
import org.peakaboo.display.map.modes.overlay.OverlayMapMode;
import org.peakaboo.display.map.modes.ratio.RatioMapMode;

public class MapUIRegistry extends Registry<MapFittingController, JPanel>{

	private static MapUIRegistry system = new MapUIRegistry();

	public static MapUIRegistry get() {
		return system;
	}
	
	static {
		get().register(CompositeMapMode.MODE_NAME, CompositeUI::new);
		get().register(OverlayMapMode.MODE_NAME, OverlayUI::new);
		get().register(RatioMapMode.MODE_NAME, RatioUI::new);
		get().register(CorrelationMapMode.MODE_NAME, CorrelationUI::new);
	}
	
}
