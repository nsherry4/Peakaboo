package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.toList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
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

public class RatioModeController extends ModeController {

	private Map<ITransitionSeries, Integer> ratioSide = new LinkedHashMap<>();
	
	public RatioModeController(MappingController map) {
		super(map);
		
		for (ITransitionSeries ts : map.rawDataController.getMapResultSet().getAllTransitionSeries()) {
			ratioSide.put(ts, 1);
		}
	}


	public RatioModeData getData()
	{

		// get transition series on ratio side 1
		List<ITransitionSeries> side1 = forSide(1);
		// get transition series on ratio side 2
		List<ITransitionSeries> side2 = forSide(2);
		
		// sum all of the maps for the given transition series for each side
		Spectrum side1Data = sumGivenMaps(side1);
		Spectrum side2Data = sumGivenMaps(side2);
		
		if (getMap().getFitting().getMapScaleMode() == MapScaleMode.RELATIVE)
		{
			SpectrumCalculations.normalize_inplace(side1Data);
			SpectrumCalculations.normalize_inplace(side2Data);
		}
				
		Spectrum ratioData = new ISpectrum(side1Data.size());
		
		
		for (int i = 0; i < ratioData.size(); i++)
		{
			Float side1Value = side1Data.get(i);
			Float side2Value = side2Data.get(i);
			
			if (side1Value <= 0.0 || side2Value <= 0.0) {
				ratioData.set(i, Float.NaN);
				continue;
			}

			float value = side1Value / side2Value;

			if (value < 1.0)
			{
				value = (1.0f / value);
				value = (float) (Math.log(value) / Math.log(Ratios.logValue));
				value = -value;
			}
			else
			{
				value = (float) (Math.log(value) / Math.log(Ratios.logValue));
			}

			ratioData.set(i, value);
		}
		
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				getMap().getUserDimensions().getUserDataWidth(),
				getMap().getUserDimensions().getUserDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, ratioData, getMap().rawDataController.getBadPoints());
		

		Spectrum invalidPoints = new ISpectrum(ratioData.size(), 0f);
		for (int i = 0; i < ratioData.size(); i++)
		{
			if (  Float.isNaN(ratioData.get(i))  )
			{
				invalidPoints.set(i, 1f);
				ratioData.set(i, 0f);
			}
		}
		
		
		int w = getMap().getFiltering().getFilteredDataWidth();
		int h = getMap().getFiltering().getFilteredDataHeight();
		Coord<Integer> size = new Coord<Integer>(w, h);
		Pair<Spectrum, Spectrum> data = new Pair<Spectrum, Spectrum>(ratioData, invalidPoints);
		boolean relative = getMap().getFitting().getMapScaleMode() == MapScaleMode.RELATIVE;
		RatioModeData modedata = new RatioModeData(data, size, relative);
		
		return modedata;

		
	}
	

	public List<ITransitionSeries> forSide(final int side)
	{
		return getVisible().stream().filter(e -> {
			Integer thisSide = this.ratioSide.get(e);
			return thisSide == side;
		}).collect(toList());
	}


	@Override
	public String longTitle() {
		String side1Title = getDatasetTitle(forSide(1));
		String side2Title = getDatasetTitle(forSide(2));

		return "Map of " + side1Title + " : " + side2Title;
	}
	

	public int getSide(ITransitionSeries ts)
	{
		return this.ratioSide.get(ts);
	}
	public void setSide(ITransitionSeries ts, int side)
	{
		this.ratioSide.put(ts, side);
		updateListeners();
	}
	
	
}
