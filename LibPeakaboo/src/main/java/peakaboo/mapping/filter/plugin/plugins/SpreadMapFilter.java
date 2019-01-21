package peakaboo.mapping.filter.plugin.plugins;

import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;
import peakaboo.mapping.filter.model.AreaMap;

public class SpreadMapFilter extends AbstractMapFilter {

	Parameter<Integer> radius;
	
	@Override
	public String getFilterName() {
		return "Signal Spread";
		
	}

	@Override
	public String getFilterDescription() {
		//TODO
		return "";
	}

	@Override
	public void initialize() {
		radius = new Parameter<Integer>("Radius", new IntegerSpinnerStyle(), 1, this::validate);
		addParameter(radius);
	}

	private boolean validate(Parameter<?> param) {
		if (radius.getValue() <= 0) { return false; }
		if (radius.getValue() > 5) { return false; }
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap map) {
		ReadOnlySpectrum data = map.getData();
		Spectrum filtered = new ISpectrum(data.size());
		GridPerspective<Float> grid = new GridPerspective<Float>(map.getSize().x, map.getSize().y, 0f);

		int r = radius.getValue();
		
		for (int y = 0; y < map.getSize().y; y++) {
			for (int x = 0; x < map.getSize().x; x++) {

				float value = grid.get(data, x, y);
				
				for (int dy = -r; dy <= +r; dy++) {
					for (int dx = -r; dx <= +r; dx++) {
						//skip points out in the corners, this is a circle
						double dist = Math.sqrt(dx*dx+dy*dy);
						if (dist > r+1) { continue; }
						float fraction = 1f;
						if (dist > r ) { fraction = (float) (1f - (dist - (float)r)); }
						
						float current = grid.get(filtered, x+dx, y+dy);
						float added = value * fraction;
						
						grid.set(filtered, x+dx, y+dy, current + added);
						
					}
				}
				
			}
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
		return "bbbbbbbbbbbbbbbbbbbbbbbbbb";
	}

}
