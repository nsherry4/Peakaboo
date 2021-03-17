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

public class Rotate180MapFilter extends AbstractMapFilter {

	@Override
	public String getFilterName() {
		return "Rotate 180 Degrees";
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
	public AreaMap filter(AreaMap source) {
		
		ReadOnlySpectrum sourceData = source.getData();
		GridPerspective<Float> sourceGrid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		
		Spectrum target = new ISpectrum(sourceData.size());
		GridPerspective<Float> targetGrid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		
		int maxy = source.getSize().y-1;
		int maxx = source.getSize().x-1;
		
		for (int y = 0; y < sourceGrid.height; y++) {
			for (int x = 0; x < sourceGrid.width; x++) {
				float value = sourceGrid.get(sourceData, x, y);
				targetGrid.set(target, maxx-x, maxy-y, value);
			}
		}
		
		Coord<Bounds<Number>> origDim = source.getRealDimensions();
		Coord<Bounds<Number>> newDim = null;
		if (origDim != null) {
			newDim = new Coord<>(new Bounds<>(origDim.x.end, origDim.x.start), new Bounds<>(origDim.y.end, origDim.y.start));
		}
		
		Coord<Integer> oldsize = source.getSize();
		return new AreaMap(target, source.getElements(), new Coord<>(oldsize.x, oldsize.y), newDim);
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
		return "ba124149-18c5-4661-b14c-547c9bee0cdc";
	}
	

	@Override
	public boolean isReplottable() {
		return false;
	}

}
