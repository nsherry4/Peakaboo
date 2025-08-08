package org.peakaboo.controller.plotter.view.mode;

import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.controller.plotter.view.ViewController;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class AverageViewMode implements ChannelViewMode {

	public static final String UUID = "b9e5b5b6-0b86-4f64-ba20-c610f38a65f8";
	
	@Override
	public String name() {
		return "Mean";
	}

	@Override
	public String description() {
		return "Average of all spectra";
	}

	@Override
	public String tooltip() {
		return "A synthetic spectrum made from the average of all spectra";
	}

	@Override
	public SpectrumView primaryScan(DataController data, ViewController view) {
		return data.getDataSet().getAnalysis().averagePlot();
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
		if (other == null) { return false; }
		return (other.getClass() == getClass());
	}
	
	@Override
	public int hashCode() {
		return pluginUUID().hashCode();
	}
	
}
