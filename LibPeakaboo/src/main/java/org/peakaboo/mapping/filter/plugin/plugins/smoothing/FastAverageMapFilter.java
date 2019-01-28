package org.peakaboo.mapping.filter.plugin.plugins.smoothing;

import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;

public class FastAverageMapFilter extends AbstractMapFilter {

	Parameter<Integer> reps;
	
	@Override
	public String getFilterName() {
		return "Fast Average";
	}

	@Override
	public String getFilterDescription() {
		return "The Fast Average filter is a simple filter which calculates a 9-point (3x3) average for each point.";
	}

	@Override
	public void initialize() {
		reps = new Parameter<Integer>("Repetitions", new IntegerSpinnerStyle(), 1, this::validate);
		addParameter(reps);
	}

	private boolean validate(Parameter<?> param) {
		if (reps.getValue() <= 0) { return false; }
		if (reps.getValue() > 10) { return false; }
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap map) {
		ReadOnlySpectrum data = map.getData();
		Spectrum filtered = new ISpectrum(data.size());
		GridPerspective<Float> grid = new GridPerspective<Float>(map.getSize().x, map.getSize().y, 0f);
		
		for (int rep = 0; rep < reps.getValue(); rep++) {
		
			for (int y = 0; y < map.getSize().y; y++) {
				for (int x = 0; x < map.getSize().x; x++) {
	
					float sum = 0f;
					int count = 0;
					
					if (grid.boundsCheck(x-1, y-1)) { sum += grid.get(data, x-1, y-1); count ++; }
					if (grid.boundsCheck(x  , y-1)) { sum += grid.get(data, x  , y-1); count ++; }
					if (grid.boundsCheck(x+1, y-1)) { sum += grid.get(data, x+1, y-1); count ++; }
					if (grid.boundsCheck(x-1, y  )) { sum += grid.get(data, x-1, y  ); count ++; }
					if (grid.boundsCheck(x  , y  )) { sum += grid.get(data, x  , y  ); count ++; }
					if (grid.boundsCheck(x+1, y  )) { sum += grid.get(data, x+1, y  ); count ++; }
					if (grid.boundsCheck(x-1, y+1)) { sum += grid.get(data, x-1, y+1); count ++; }
					if (grid.boundsCheck(x  , y+1)) { sum += grid.get(data, x  , y+1); count ++; }
					if (grid.boundsCheck(x+1, y+1)) { sum += grid.get(data, x+1, y+1); count ++; }
					
					grid.set(filtered, x, y, sum/count);
					
				}
			}
			
			data = filtered;
			
		}
		
		return new AreaMap(filtered, map.getSize(), map.getRealDimensions());
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

}
