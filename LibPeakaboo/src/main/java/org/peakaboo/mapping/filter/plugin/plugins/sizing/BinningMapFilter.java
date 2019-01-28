package org.peakaboo.mapping.filter.plugin.plugins.sizing;

import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

import cyclops.Coord;
import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;

public class BinningMapFilter extends AbstractMapFilter {

	private Parameter<Integer> reps;
	
	@Override
	public String getFilterName() {
		return "Binning";
	}

	@Override
	public String getFilterDescription() {
		return "The Binning Map Filter scales a map down by a factor of 2 by merging 2x2 areas into a single point. If a map has an odd numbered dimension, a single pixel at the end will be removed.";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.SIZING;
	}

	@Override
	public void initialize() {
		reps = new Parameter<Integer>("Repetitions", new IntegerSpinnerStyle(), 1, this::validate);
		addParameter(reps);
	}
	
	private boolean validate(Parameter<?> param) {
		if (reps.getValue() <= 0 || reps.getValue() > 5) {
			return false;
		}
		return true;
	}

	@Override
	public AreaMap filter(AreaMap map) {

		int count = 0;
		while (count < reps.getValue())
		{
			map = bin(map);
			count++;
		}
		
		return map;
	}

	private AreaMap bin(AreaMap map) {
		
		GridPerspective<Float> originalGrid = new GridPerspective<Float>(map.getSize().x, map.getSize().y, 0f);
		ReadOnlySpectrum originalData = map.getData();
		
		GridPerspective<Float> binnedGrid = new GridPerspective<Float>(map.getSize().x/2, map.getSize().y/2, 0f);
		Spectrum binnedData = new ISpectrum(binnedGrid.width * binnedGrid.height);
		
		for (int y = 0; y < binnedGrid.height; y++) {
			for (int x = 0; x < binnedGrid.width; x++) {
				int sx = x*2;
				int sy = y*2;
				
				float sum = 0f;
				sum += originalGrid.get(originalData, sx,   sy);
				sum += originalGrid.get(originalData, sx+1, sy);
				sum += originalGrid.get(originalData, sx,   sy+1);
				sum += originalGrid.get(originalData, sx+1, sy+1);
				
				binnedGrid.set(binnedData, x, y, sum/4f);
			}
		}
		
		return new AreaMap(binnedData, new Coord<>(binnedGrid.width, binnedGrid.height));
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
		return "0231d1b9-83e7-48b4-9a94-cc2e2a9083b7";
	}

}
