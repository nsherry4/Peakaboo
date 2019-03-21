package org.peakaboo.mapping.filter.plugin.plugins.mathematical;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class PowerMapFilter extends AbstractMapFilter {

	Parameter<Float> power;
	
	@Override
	public String getFilterName() {
		return "Exponent";
	}

	@Override
	public String getFilterDescription() {
		return "Raises the values in each map to the given power";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return new MapFilterDescriptor(MapFilterDescriptor.GROUP_MATH, "Exponentiated");
	}

	@Override
	public void initialize() {
		power = new Parameter<>("Power", new RealSpinnerStyle(), 2f, this::validate);
		addParameter(power);
	}

	private boolean validate(Parameter<?> param) {
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap map) {
		ReadOnlySpectrum data = map.getData();
		Spectrum exped = new ISpectrum(data.size());
		float exponent = power.getValue();
		
		for (int i = 0; i < data.size(); i++) {
			exped.set(i, (float) Math.pow(data.get(i), exponent));
		}
		return new AreaMap(exped, map.getSize(), map.getRealDimensions());
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
		return "dcae3d1a-a8d7-44cb-9c0b-458fab37dc03";
	}

}
