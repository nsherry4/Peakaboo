package org.peakaboo.controller.plotter.view.mode;

import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.controller.plotter.view.ViewController;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class MaximumViewMode implements ChannelViewMode {

	public static final String UUID = "5daddb84-aa3f-46a6-af1c-f232d011f01d";
	
	@Override
	public String shortName() {
		return "Max";
	}

	@Override
	public String longName() {
		return "Max per Channel";
	}

	@Override
	public String description() {
		return "Shows the maximum counts per channel across all spectra";
	}

	@Override
	public SpectrumView primaryScan(DataController data, ViewController view) {
		return data.getDataSet().getAnalysis().maximumPlot();
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
