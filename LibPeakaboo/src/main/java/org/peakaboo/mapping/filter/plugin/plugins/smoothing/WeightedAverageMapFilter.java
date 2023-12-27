package org.peakaboo.mapping.filter.plugin.plugins.smoothing;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class WeightedAverageMapFilter extends AbstractMapFilter{

	Parameter<Integer> radius;
	Parameter<Integer> reps;
	
	@Override
	public String getFilterName() {
		return "Weighted Average";
	}

	@Override
	public String getFilterDescription() {
		return "The Weighted Average filter performs a weighted average within a given radius, favouring closer points more than points farther away";
	}

	@Override
	public void initialize() {
		radius = new Parameter<Integer>("Radius", new IntegerSpinnerStyle(), 1, this::validate);
		addParameter(radius);
		reps = new Parameter<Integer>("Repetitions", new IntegerSpinnerStyle(), 1, this::validate);
		addParameter(reps);
	}

	private boolean validate(Parameter<?> param) {
		
		if (radius.getValue() <= 0) { return false; }
		if (radius.getValue() > 5) { return false; }
		
		if (reps.getValue() <= 0) { return false; }
		if (reps.getValue() > 5) { return false; }
		
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap source) {
		ReadOnlySpectrum data = source.getData();
		Spectrum filtered = new ISpectrum(data.size());
		GridPerspective<Float> grid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);

		int r = radius.getValue();
		
		for (int rep = 0; rep < reps.getValue(); rep++) {
		
			for (int y = 0; y < source.getSize().y; y++) {
				for (int x = 0; x < source.getSize().x; x++) {
					
					float sum = 0f;
					float weights = 0f;
					
					for (int dy = -r; dy <= +r; dy++) {
						int sy = y+dy;
						for (int dx = -r; dx <= +r; dx++) {
							int sx = x+dx;
														
							//don't include oob points
							if (!grid.boundsCheck(sx, sy)) {
								continue;
							}
							
							// do a fast distance check to avoid unneeded sqrt calls
							// this rough distance should never be less than the pythagorean distance,
							// although when x or y = 0, it will be equal.
							int roughdist = Math.abs(dx) + Math.abs(dy);
							if (roughdist > r+1f) { continue; }
							
							
							//calculate weight for this point and do a proper distance check
							double dist = Math.sqrt(dx*dx+dy*dy);
							float weight = (float) (r+1f - dist);
							if (weight < 0) { continue; }
							
							//normalize and square the weight to give extra weight to the central points
							float maxweight = r+1f;
							weight = (weight/maxweight);
							weight *= weight;
							
							//add to the sum
							sum += grid.get(data, sx, sy) * weight;
							weights += weight;
						}
					}
					
					grid.set(filtered, x, y, sum/weights);
					
				}
			}
			
			data = filtered;
			
		}

		
		return new AreaMap(filtered, source);
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "fc2c3147-07a7-4823-b508-1a20d0579ccf";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.SMOOTHING;
	}

	@Override
	public boolean isReplottable() {
		return true;
	}
	
}
