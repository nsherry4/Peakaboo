package org.peakaboo.mapping.filter.plugin.plugins.smoothing;

import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractConvolvingMapFilter;

public class FastAverageMapFilter extends AbstractConvolvingMapFilter {


	public FastAverageMapFilter() {
		super(1);
	}

	@Override
	public String getFilterName() {
		return "Fast Average";
	}

	@Override
	public String getFilterDescription() {
		return "The Fast Average filter is a simple filter which calculates a 9-point (3x3) average for each point.";
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
		return "983260dc-e133-4dc0-a736-f646bb8998ed";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.SMOOTHING;
	}


	@Override
	public boolean isReplottable() {
		return true;
	}

	@Override
	protected float[][] getKernel(float intensity) {
		return new float[][] {
			new float[] {1, 1, 1},
			new float[] {1, 1, 1},
			new float[] {1, 1, 1},
		};
	}
}
