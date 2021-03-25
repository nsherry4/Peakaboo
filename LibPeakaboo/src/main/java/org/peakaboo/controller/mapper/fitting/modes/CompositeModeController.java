package org.peakaboo.controller.mapper.fitting.modes;

import java.util.List;
import java.util.Optional;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.composite.CompositeModeData;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

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

		List<Integer> invalidPoints = getMap().getFiltering().getInvalidPoints();

		return new CompositeModeData(data, getSize(), invalidPoints);
		
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
