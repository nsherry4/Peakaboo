package org.peakaboo.controller.plotter.view.mode;

import java.util.HashMap;
import java.util.Map;

import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.controller.plotter.view.ViewController;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public interface ChannelViewMode extends BoltJavaPlugin {

	String name();
	String description();
	String tooltip();
	
	SpectrumView primaryScan(DataController data, ViewController view);
	default Map<String, SpectrumView> otherScans(DataController data, ViewController view) {
		return new HashMap<>();
	}
	
	default String pluginName() {
		return name();
	}
	
	default String pluginDescription() {
		return tooltip();
	}
	
}
