package org.peakaboo.mapping.filter.plugin.plugins.mathematical;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class MultiplyMapFilter extends AbstractMapFilter {

	Parameter<Float> multiplier;
	
	@Override
	public String getFilterName() {
		return "Multiply";
	}

	@Override
	public String getFilterDescription() {
		return "Multiplies each map by the given multiplier";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.MATH;
	}

	@Override
	public void initialize() {
		multiplier = new Parameter<>("Multiplier", new RealSpinnerStyle(), 1f, this::validate);
		addParameter(multiplier);
	}

	private boolean validate(Parameter<?> param) {
		if (multiplier.getValue() <= 0f) { return false; }
		if (multiplier.getValue() > 1000f) { return false; }
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap source) {
		return new AreaMap(SpectrumCalculations.multiplyBy(source.getData(), multiplier.getValue()), source);
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
		return "503ca760-e39e-45a9-9218-2103f99dfc3d";
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

}
