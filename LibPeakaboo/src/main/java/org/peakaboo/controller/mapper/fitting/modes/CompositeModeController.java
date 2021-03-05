package org.peakaboo.controller.mapper.fitting.modes;

import java.util.List;
import java.util.Optional;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.composite.CompositeModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.Interpolation;

public class CompositeModeController extends SimpleModeController {
	
	public CompositeModeController(MappingController map) {
		super(map);
	}

	public CompositeModeData getData() {
		return getData(Optional.empty());
	}
	
	public CompositeModeData getData(Optional<ITransitionSeries> fitting) {
		
		Spectrum data;
		if (fitting.isPresent()) {
			data = sumSingleMap(fitting.get());
		} else {
			data = sumVisibleMaps();
		}
		
		GridPerspective<Float>	grid	= new GridPerspective<>(
				getMap().getUserDimensions().getUserDataWidth(),
				getMap().getUserDimensions().getUserDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, data, getMap().rawDataController.getBadPoints());
		List<Integer> invalidPoints = getMap().getFiltering().getInvalidPoints();

		return new CompositeModeData(data, getSize(), invalidPoints);
		
	}
	
	public Coord<Integer> getSize() {
		int w = getMap().getFiltering().getFilteredDataWidth();
		int h = getMap().getFiltering().getFilteredDataHeight();
		Coord<Integer> size = new Coord<>(w, h);
		return size;
	}



	@Override
	public String longTitle() {
		if (super.getVisible().size() > 1) {
			return "Composite of " + getDatasetTitle(super.getVisible());
		} else {
			return "Map of " + getDatasetTitle(super.getVisible());
		}
	}
	
}
