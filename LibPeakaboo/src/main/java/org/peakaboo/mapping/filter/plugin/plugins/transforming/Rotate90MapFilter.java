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

public class Rotate90MapFilter extends AbstractMapFilter {

	@Override
	public String getFilterName() {
		return "Rotate 90 Degrees";
	}

	@Override
	public String getFilterDescription() {
		// TODO Auto-generated method stub
		return "";
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
		GridPerspective<Float> sourceGrid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		
		Spectrum target = new ArraySpectrum(sourceData.size());
		GridPerspective<Float> targetGrid = new GridPerspective<Float>(source.getSize().y, source.getSize().x, 0f);
		
		int maxy = source.getSize().y-1;
		int maxx = source.getSize().x-1;
		
		for (int y = 0; y < sourceGrid.height; y++) {
			for (int x = 0; x < sourceGrid.width; x++) {
				float value = sourceGrid.get(sourceData, x, y);
				targetGrid.set(target, y, maxx-x, value);
			}
		}
		
		Coord<Integer> oldsize = source.getSize();
		
		Coord<Bounds<Number>> origDim = source.getRealDimensions();
		Coord<Bounds<Number>> newDim = null;
		if (origDim != null) {
			newDim = new Coord<>(new Bounds<>(origDim.y.start, origDim.y.end), new Bounds<>(origDim.x.end, origDim.x.start));
		}
		
		return new AreaMap(target, source.getElements(), new Coord<>(oldsize.y, oldsize.x), newDim);
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "6d39f7a6-39fb-4500-ad6b-86ced0f59754";
	}

	@Override
	public boolean isReplottable() {
		return false;
	}

}
