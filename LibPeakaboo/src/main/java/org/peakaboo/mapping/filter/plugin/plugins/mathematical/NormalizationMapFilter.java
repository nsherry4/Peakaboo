package org.peakaboo.mapping.filter.plugin.plugins.mathematical;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

import cyclops.SpectrumCalculations;

public class NormalizationMapFilter extends AbstractMapFilter {

	Parameter<Float> level;
	
	@Override
	public String getFilterName() {
		return "Normalize";
	}

	@Override
	public String getFilterDescription() {
		return "Normalizes each map to a certain maximum value. Note that filters are applied to fitting maps individually.";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.MATH;
	}

	@Override
	public void initialize() {
		level = new Parameter<>("Maximum", new RealSpinnerStyle(), 100f, this::validate);
		addParameter(level);
	}

	private boolean validate(Parameter<?> param) {
		if (level.getValue() <= 0f) { return false; }
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap map) {
		return new AreaMap(SpectrumCalculations.addToList(map.getData(), level.getValue()), map.getSize(), map.getRealDimensions());
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
		return "6fefa14d-bc4b-4b3f-8235-16735a70190d";
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

}
