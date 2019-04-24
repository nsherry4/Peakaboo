package org.peakaboo.framework.eventful;

import java.util.function.Consumer;

public class EventfulConfig {

	public static Consumer<Runnable> uiThreadRunner = r -> {
		throw new RuntimeException("Eventful UI Hook has not been configured");
	};
	
}
