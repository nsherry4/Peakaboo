package org.peakaboo.controller.plotter.view.mode;

import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.controller.plotter.view.ViewController;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class SingleViewMode implements ChannelViewMode {

	public static final String UUID = "f2f55957-0733-4915-a41d-099c54ef5f76";
	
	@Override
	public String shortName() {
		return "Single";
	}

	@Override
	public String longName() {
		return "Single Spectrum";
	}

	@Override
	public String description() {
		return "A single spectrum at a time";
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
		return (other.getClass() == getClass());
	}
	
	@Override
	public int hashCode() {
		return pluginUUID().hashCode();
	}
	
	
}
