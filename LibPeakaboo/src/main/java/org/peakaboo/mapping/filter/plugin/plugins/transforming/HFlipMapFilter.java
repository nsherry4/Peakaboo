package org.peakaboo.mapping.filter.plugin.plugins.transforming;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class HFlipMapFilter extends AbstractMapFilter {

	@Override
	public String getFilterName() {
		return "Horizontal Flip";
	}

	@Override
	public String getFilterDescription() {
		return "Flips the data horizontally, so that the top right corner is in the top left, and the bottom right corner is in the bottom left";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.TRANSFORMING;
	}

	@Override
	public void initialize() {}

	@Override
	public AreaMap filter(MapFilterContext ctx) {
		AreaMap source = ctx.map();
		SpectrumView sourceData = source.getData();
		Spectrum target = new ArraySpectrum(sourceData.size());
		GridPerspective<Float> grid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		
		int maxx = source.getSize().x-1;
		
		for (int y = 0; y < grid.height; y++) {
			for (int x = 0; x < grid.width; x++) {
				float value = grid.get(sourceData, maxx - x, y);
				grid.set(target, x, y, value);
			}
		}
		
		Coord<Bounds<Number>> origDim = source.getRealDimensions();
		Coord<Bounds<Number>> newDim = null;
		if (origDim != null) {
			newDim = new Coord<>(new Bounds<>(origDim.x.end, origDim.x.start), origDim.y);
		}
		
		return new AreaMap(target, source.getElements(), source.getSize(), newDim);
		
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "725c5443-9794-4d99-90a8-302e9c04b694";
	}
	

	@Override
	public boolean isReplottable() {
		return false;
	}

}
