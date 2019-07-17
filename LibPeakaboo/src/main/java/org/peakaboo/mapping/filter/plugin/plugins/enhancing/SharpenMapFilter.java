package org.peakaboo.mapping.filter.plugin.plugins.enhancing;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractConvolvingMapFilter;

public class SharpenMapFilter extends AbstractConvolvingMapFilter {

	public SharpenMapFilter() {
		super(1);
	}

	@Override
	public void initialize() {
		super.initialize();
		super.reps.setEnabled(false);
	}
	
	protected boolean validateIntensity(Parameter<?> param) {
		if (intensity.getValue() <= 0.1) { return false; }
		if (intensity.getValue() > 1.5) { return false; }
		return true;
	}
	
	protected float[][] getKernel(float intensity) {
		float ci = 20/intensity;
		return new float[][] {
			new float[] {-1, -1, -1},
			new float[] {-1, ci, -1},
			new float[] {-1, -1, -1},
		};
	}
	
	@Override
	public String getFilterName() {
		return "Sharpen";
	}

	@Override
	public String getFilterDescription() {
		return "The Sharpen filter enhances detail and edges in a map";
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
		return "9c4f531b-4a55-48c8-98cb-e509d85875dd";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.SHARPENING;
	}


	@Override
	public boolean isReplottable() {
		return true;
	}
	
}
