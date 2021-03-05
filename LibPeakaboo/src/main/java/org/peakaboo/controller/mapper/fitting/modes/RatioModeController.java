package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.components.GroupState;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.ratio.RatioModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Ratios;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;
import org.peakaboo.mapping.filter.Interpolation;

public class RatioModeController extends SimpleModeController {

	private GroupState groups;
	
	public RatioModeController(MappingController map) {
		super(map);
		groups = new GroupState(this);
	}

	@Override
	public RatioModeData getData()
	{

		// get transition series on ratio side 1
		List<ITransitionSeries> side1 = forSide(1);
		// get transition series on ratio side 2
		List<ITransitionSeries> side2 = forSide(2);
		
		// sum all of the maps for the given transition series for each side
		Spectrum side1Data = sumGivenMaps(side1);
		Spectrum side2Data = sumGivenMaps(side2);
		
		if (getMap().getFitting().getMapScaleMode() == MapScaleMode.RELATIVE) {
			SpectrumCalculations.normalize_inplace(side1Data);
			SpectrumCalculations.normalize_inplace(side2Data);
		}
				
		Spectrum ratioData = new ISpectrum(side1Data.size());
		
		
		for (int i = 0; i < ratioData.size(); i++) {
			Float side1Value = side1Data.get(i);
			Float side2Value = side2Data.get(i);
			
			if (side1Value <= 0.0 || side2Value <= 0.0) {
				ratioData.set(i, Float.NaN);
				continue;
			}

			float value = side1Value / side2Value;

			if (value < 1.0) {
				value = (1.0f / value);
				value = (float) (Math.log(value) / Math.log(Ratios.logValue));
				value = -value;
			} else {
				value = (float) (Math.log(value) / Math.log(Ratios.logValue));
			}

			ratioData.set(i, value);
		}
		
		
		GridPerspective<Float> grid = new GridPerspective<>(
				getMap().getUserDimensions().getUserDataWidth(),
				getMap().getUserDimensions().getUserDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, ratioData, getMap().rawDataController.getBadPoints());
		

		Spectrum invalidPoints = new ISpectrum(ratioData.size(), 0f);
		for (int i = 0; i < ratioData.size(); i++) {
			if (  Float.isNaN(ratioData.get(i))  ) {
				invalidPoints.set(i, 1f);
				ratioData.set(i, 0f);
			}
		}

		Pair<Spectrum, Spectrum> data = new Pair<>(ratioData, invalidPoints);
		boolean relative = getMap().getFitting().getMapScaleMode() == MapScaleMode.RELATIVE;
		return new RatioModeData(data, getSize(), relative);
				
	}
	
	
	@Override
	public Coord<Integer> getSize() {
		int w = getMap().getFiltering().getFilteredDataWidth();
		int h = getMap().getFiltering().getFilteredDataHeight();
		Coord<Integer> size = new Coord<>(w, h);
		return size;
	}

	@Override
	public String longTitle() {
		String side1Title = getDatasetTitle(forSide(1));
		String side2Title = getDatasetTitle(forSide(2));

		return "Map of " + side1Title + " : " + side2Title;
	}
	
	
	///// Grouping delegators /////
	public List<ITransitionSeries> forSide(int side) { return groups.getVisibleMembers(side); }
	public int getSide(ITransitionSeries ts) { return groups.getGroup(ts); }
	public void setSide(ITransitionSeries ts, int side) { groups.setGroup(ts, side); }
	
}
