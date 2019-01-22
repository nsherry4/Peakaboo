package peakaboo.mapping.filter.plugin.plugins;

import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;
import peakaboo.mapping.filter.model.AreaMap;

public class WeightedAverageMapFilter extends AbstractMapFilter{

	Parameter<Integer> radius;
	Parameter<Integer> reps;
	
	@Override
	public String getFilterName() {
		return "Weighted Average";
	}

	@Override
	public String getFilterDescription() {
		// TODO Auto-generated method stub
		return "";
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
		if (reps.getValue() > 10) { return false; }
		
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap map) {
		ReadOnlySpectrum data = map.getData();
		Spectrum filtered = new ISpectrum(data.size());
		GridPerspective<Float> grid = new GridPerspective<Float>(map.getSize().x, map.getSize().y, 0f);

		int r = radius.getValue();
		
		for (int rep = 0; rep < reps.getValue(); rep++) {
		
			for (int y = 0; y < map.getSize().y; y++) {
				for (int x = 0; x < map.getSize().x; x++) {
					
					float sum = 0f;
					float weights = 0f;
					
					for (int dy = -r; dy <= +r; dy++) {
						for (int dx = -r; dx <= +r; dx++) {
							//don't include oob points
							if (!grid.boundsCheck(x+dx, y+dy)) {
								continue;
							}
							
							//calculate weight for this point
							double dist = Math.sqrt(dx*dx+dy*dy);
							float weight = (float) (r+1f - dist);
							
							//this will form a circular sampling area
							if (weight < 0) { continue; }
							
							//normalize and square the weight to give extra weight to the central points
							float maxweight = r+1f;
							weight = (weight/maxweight);
							weight *= weight;
							
							//add to the sum
							sum += grid.get(data, x+dx, y+dy) * weight;
							weights += weight;
						}
					}
					
					grid.set(filtered, x, y, sum/weights);
					
				}
			}
			
			data = filtered;
			
		}

		
		return new AreaMap(filtered, map.getSize());
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
		// TODO Auto-generated method stub
		return "ccccccccccccccccccccccccccccc";
	}

}
