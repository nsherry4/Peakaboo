package org.peakaboo.controller.plotter.view.mode;

import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.controller.plotter.view.ViewController;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class SingleViewMode implements ChannelViewMode {

	public static final String UUID = "f2f55957-0733-4915-a41d-099c54ef5f76";
	
	@Override
	public String name() {
		return "Single";
	}

	@Override
	public String description() {
		return "An individual spectrum";
	}

	@Override
	public String tooltip() {
		return "Display one specific spectrum from the dataset";
	}

	@Override
	public SpectrumView primaryScan(DataController data, ViewController view) {
		return data.getDataSet().getScanData().get(view.getScanNumber());
	}


	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return UUID;
	}
	
	@Override
	public boolean equals(Object other) {
		// Compare class names to handle cross-classloader scenarios (plugin upgrades)
		return other != null && other.getClass().getName().equals(getClass().getName());
	}
	
	@Override
	public int hashCode() {
		return pluginUUID().hashCode();
	}
	
	
}
