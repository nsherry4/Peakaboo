package org.peakaboo.mapping.filter.plugin.plugins.mathematical;

import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class LogMapFilter extends AbstractMapFilter {

	@Override
	public String getFilterName() {
		return "Logarithm";
	}

	@Override
	public String getFilterDescription() {
		return "Calculates the natural log of each point";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return new MapFilterDescriptor(MapFilterDescriptor.GROUP_MATH, "Logged");
	}

	@Override
	public void initialize() {}

	@Override
	public AreaMap filter(AreaMap source) {
		ReadOnlySpectrum data = source.getData();
		Spectrum logged = new ISpectrum(data.size());
		for (int i = 0; i < data.size(); i++) {
			logged.set(i, (float) Math.log1p(data.get(i)));
		}
		return new AreaMap(logged, source);
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "318aee9c-6bd1-4f5f-aef5-11f5e59c4bdc";
	}

}
