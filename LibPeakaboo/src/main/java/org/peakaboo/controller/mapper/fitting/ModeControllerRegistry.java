package org.peakaboo.controller.mapper.fitting;

import org.peakaboo.app.Registry;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.CompositeModeController;
import org.peakaboo.controller.mapper.fitting.modes.CorrelationModeController;
import org.peakaboo.controller.mapper.fitting.modes.ModeController;
import org.peakaboo.controller.mapper.fitting.modes.OverlayModeController;
import org.peakaboo.controller.mapper.fitting.modes.RatioModeController;
import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.display.map.modes.correlation.CorrelationMapMode;
import org.peakaboo.display.map.modes.overlay.OverlayMapMode;
import org.peakaboo.display.map.modes.ratio.RatioMapMode;

public class ModeControllerRegistry extends Registry<MappingController, ModeController> {

	private static ModeControllerRegistry system = new ModeControllerRegistry();
	public static ModeControllerRegistry get() { return system; }
	
	static {
		get().register(CompositeMapMode.MODE_NAME, CompositeModeController::new);
		get().register(OverlayMapMode.MODE_NAME, OverlayModeController::new);
		get().register(RatioMapMode.MODE_NAME, RatioModeController::new);
		get().register(CorrelationMapMode.MODE_NAME, CorrelationModeController::new);
	}
	
}
