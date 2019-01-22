package peakaboo.mapping.filter.plugin.plugins;

import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;
import peakaboo.mapping.filter.model.AreaMap;

public class WeakSignalRemovalMapFilter extends AbstractMapFilter {

	Parameter<Integer> percent;
	
	@Override
	public String getFilterName() {
		return "Low Signal Removal";
	}

	@Override
	public String getFilterDescription() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void initialize() {
		percent = new Parameter<>("Cutoff Percent", new IntegerSpinnerStyle(), 10, this::validate);
		addParameter(percent);
	}

	private boolean validate(Parameter<?> param) {
		
		if (percent.getValue() < 0) { return false; }
		if (percent.getValue() > 100) { return false; }
				
		return true;
	}
	
	@Override
	public AreaMap filter(AreaMap map) {
		
		ReadOnlySpectrum oldmap = map.getData();
		float max = oldmap.max();
		float cutoff = max * percent.getValue() / 100f;
		Spectrum newmap = new ISpectrum(oldmap.size());
		for (int i = 0; i < newmap.size(); i++) {
			float value = oldmap.get(i);
			if (value < cutoff) { continue; }
			newmap.set(i, value);
		}
		return new AreaMap(newmap, map.getSize());
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
		return "1dd38076-44f4-4717-8ae7-49e4e0e0d48f";
	}

}
