package org.peakaboo.mapping.filter.plugin.plugins.clipping;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class WeakSignalRemovalMapFilter extends AbstractMapFilter {

	Parameter<Integer> percent;
	
	@Override
	public String getFilterName() {
		return "Low Signal Removal";
	}

	@Override
	public String getFilterDescription() {
		return "The Low Signal Removal filter removes the weakest n% signal from each map.";
	}

	@Override
	public void initialize() {
		percent = new Parameter<>("Cutoff Percent", new IntegerSpinnerStyle(), 30, this::validate);
		addParameter(percent);
	}

	private boolean validate(Parameter<?> param) {
		
		if (percent.getValue() < 0) { return false; }
		if (percent.getValue() > 100) { return false; }
				
		return true;
	}
	
	@Override
	public AreaMap filter(MapFilterContext ctx) {
		AreaMap source = ctx.map();
		
		
		ReadOnlySpectrum oldmap = source.getData();
		float max = oldmap.max();
		float cutoff = max * percent.getValue() / 100f;
		Spectrum newmap = new ISpectrum(oldmap.size());
		for (int i = 0; i < newmap.size(); i++) {
			float value = oldmap.get(i);
			if (value < cutoff) { continue; }
			newmap.set(i, value);
		}
		return new AreaMap(newmap, source);
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "1dd38076-44f4-4717-8ae7-49e4e0e0d48f";
	}
	
	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.CLIPPING;
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

}
