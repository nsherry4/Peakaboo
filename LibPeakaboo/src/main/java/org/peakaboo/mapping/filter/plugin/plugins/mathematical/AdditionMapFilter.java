package org.peakaboo.mapping.filter.plugin.plugins.mathematical;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.SpectrumCalculations;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class AdditionMapFilter extends AbstractMapFilter {

	Parameter<Float> added;
	
	@Override
	public String getFilterName() {
		return "Addition";
	}

	@Override
	public String getFilterDescription() {
		return "Adds the given amount to each map";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.MATH;
	}

	@Override
	public void initialize() {
		added = new Parameter<>("Amount Added", new RealSpinnerStyle(), 0f, this::validate);
		addParameter(added);
	}

	private boolean validate(Parameter<?> param) {
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap source) {
		return new AreaMap(SpectrumCalculations.addToList(source.getData(), added.getValue()), source);
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
		return "0044766b-d091-42a2-9833-480672a81ee0";
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

}
