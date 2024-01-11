package org.peakaboo.mapping.filter.plugin.plugins.sizing;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

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
	public AreaMap filter(MapFilterContext ctx) {
		AreaMap map = ctx.map();
		
		int count = 0;
		while (count < reps.getValue())
		{
			map = bin(map);
			count++;
		}
		
		return map;
	}

	private AreaMap bin(AreaMap source) {
		
		GridPerspective<Float> originalGrid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		SpectrumView originalData = source.getData();
		
		GridPerspective<Float> binnedGrid = new GridPerspective<Float>(source.getSize().x/2, source.getSize().y/2, 0f);
		Spectrum binnedData = new ArraySpectrum(binnedGrid.size());
		
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
		
		return new AreaMap(binnedData, 
				source.getElements(), 
				new Coord<>(binnedGrid.width, binnedGrid.height), 
				source.getRealDimensions()
			);
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "0231d1b9-83e7-48b4-9a94-cc2e2a9083b7";
	}


	@Override
	public boolean isReplottable() {
		return false;
	}
	
}
