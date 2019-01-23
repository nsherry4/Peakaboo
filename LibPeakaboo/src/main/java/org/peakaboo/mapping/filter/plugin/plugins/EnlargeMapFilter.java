package org.peakaboo.mapping.filter.plugin.plugins;

import org.peakaboo.mapping.filter.Interpolation;
import org.peakaboo.mapping.filter.model.AreaMap;

import cyclops.Coord;
import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;

public class EnlargeMapFilter extends AbstractMapFilter {

	private Parameter<Integer> reps;
	
	@Override
	public String getFilterName() {
		return "Enlarge";
	}

	@Override
	public String getFilterDescription() {
		return "Enlarges a map by interpolating the values between adjacent points";
	}

	@Override
	public void initialize() {
		reps = new Parameter<Integer>("Repetitions", new IntegerSpinnerStyle(), 1, this::validate);
		addParameter(reps);
	}

	@Override
	public AreaMap filter(AreaMap map) {
		
		GridPerspective<Float> interpGrid = new GridPerspective<Float>(map.getSize().x, map.getSize().y, 0f);
		Spectrum mapdata = new ISpectrum(map.getData());
		
		Pair<GridPerspective<Float>, Spectrum> interpolationResult;
		int count = 0;
		while (count < reps.getValue())
		{
			interpolationResult = Interpolation.interpolateGridLinear(interpGrid, mapdata);
			interpGrid = interpolationResult.first;
			mapdata = interpolationResult.second;
			count++;
		}
		
		return new AreaMap(mapdata, new Coord<>(interpGrid.width, interpGrid.height));
	}

	@Override
	public boolean pluginEnabled() {
		return false;
	}

	@Override
	public String pluginVersion() {
		return "0.1";
	}

	@Override
	public String pluginUUID() {
		return "c4b30160-ae47-4768-aca7-26791e016e50";
	}
	
	private boolean validate(Parameter<?> param) {
		if (reps.getValue() <= 0 || reps.getValue() >= 4) {
			return false;
		}
		return true;
		//TODO: Limit by map size?
	}

	@Override
	public String getFilterAction() {
		return "Enlarged";
	}

}
