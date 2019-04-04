package org.peakaboo.controller.mapper.fitting.modes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.display.map.modes.composite.CompositeModeData;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.eventful.Eventful;
import org.peakaboo.mapping.filter.Interpolation;

public class CompositeModeController extends ModeController {

	
	public CompositeModeController(MappingController map) {
		super(map);
	}
	



	
	
	public CompositeModeData getData() {
		return getData(Optional.empty());
	}
	public CompositeModeData getData(Optional<ITransitionSeries> fitting)
	{
		
		Spectrum data;
		if (fitting.isPresent()) {
			data = sumSingleMap(fitting.get());
		} else {
			data = sumVisibleMaps();
		}
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				getMap().getUserDimensions().getUserDataWidth(),
				getMap().getUserDimensions().getUserDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, data, getMap().rawDataController.getBadPoints());
		

		int w = getMap().getFiltering().getFilteredDataWidth();
		int h = getMap().getFiltering().getFilteredDataHeight();
		Coord<Integer> size = new Coord<Integer>(w, h);
		CompositeModeData modedata = new CompositeModeData(data, size);
		
		return modedata;
		
	}



	@Override
	public String longTitle() {
		if (super.getVisible().size() > 1) {
			return "Composite of " + getDatasetTitle(super.getVisible());
		} else {
			return "Map of " + getDatasetTitle(super.getVisible());
		}
	}


	@Override
	public boolean isSelectable() {
		return true;
	}


	@Override
	public boolean isReplottable() {
		return true;
	}

	
}
