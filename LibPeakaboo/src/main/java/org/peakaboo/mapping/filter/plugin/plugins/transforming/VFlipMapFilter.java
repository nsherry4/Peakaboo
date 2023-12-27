package org.peakaboo.mapping.filter.plugin.plugins.transforming;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class VFlipMapFilter extends AbstractMapFilter {

	@Override
	public String getFilterName() {
		return "Vertical Flip";
	}

	@Override
	public String getFilterDescription() {
		return "Flips the data vertically, so that the top right corner is in the bottom right, and the top left corner is in the bottom left";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.TRANSFORMING;
	}

	@Override
	public void initialize() {}

	@Override
	public AreaMap filter(AreaMap source) {
		
		ReadOnlySpectrum sourceData = source.getData();
		Spectrum target = new ISpectrum(sourceData.size());
		GridPerspective<Float> grid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		
		int maxy = source.getSize().y-1;
		
		for (int y = 0; y < grid.height; y++) {
			for (int x = 0; x < grid.width; x++) {
				float value = grid.get(sourceData, x, maxy - y);
				grid.set(target, x, y, value);
			}
		}
		
		Coord<Bounds<Number>> origDim = source.getRealDimensions();
		Coord<Bounds<Number>> newDim = null;
		if (origDim != null) {
			newDim = new Coord<>(origDim.x, new Bounds<>(origDim.y.end, origDim.y.start));
		}
		
		return new AreaMap(target, source.getElements(), source.getSize(), newDim);
		
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "41c16908-5880-45ff-b157-d4d0f7c41b1c";
	}

	@Override
	public boolean isReplottable() {
		return false;
	}

}
