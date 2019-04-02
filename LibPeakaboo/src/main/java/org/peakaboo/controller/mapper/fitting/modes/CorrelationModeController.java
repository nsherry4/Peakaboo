package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.correlation.CorrelationMapMode;
import org.peakaboo.display.map.modes.correlation.CorrelationModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.SignalOutlierCorrectionMapFilter;

public class CorrelationModeController extends ModeController {

	private MappingController map;
	private Map<ITransitionSeries, Integer> sides = new LinkedHashMap<>();
	private boolean clip = false;
	
	public CorrelationModeController(MappingController map) {
		super(map);
		this.map = map;
		
		for (ITransitionSeries ts : map.rawDataController.getMapResultSet().getAllTransitionSeries()) {
			sides.put(ts, 1);
		}
		
	}




	public List<ITransitionSeries> forSide(final int side)
	{
		return super.getVisible().stream().filter(e -> {
			Integer thisSide = this.sides.get(e);
			return thisSide == side;
		}).collect(toList());
	}
	
	public int getSide(ITransitionSeries ts)
	{
		return this.sides.get(ts);
	}
	public void setSide(ITransitionSeries ts, int side)
	{
		this.sides.put(ts, side);
		updateListeners();
	}



	public boolean isClip() {
		return clip;
	}

	public void setClip(boolean clip) {
		this.clip = clip;
		updateListeners();
	}



	public CorrelationModeData getData() {
		
		
		// get transition series on ratio side 1
		List<ITransitionSeries> xTS = forSide(1);
		// get transition series on ratio side 2
		List<ITransitionSeries> yTS = forSide(2);
		
		// sum all of the maps for the given transition series for each side
		Spectrum xData = super.sumGivenMaps(xTS);
		Spectrum yData = super.sumGivenMaps(yTS);
		
		//Generate histograms
		List<Float> xSorted = xData.stream().collect(Collectors.toList());
		List<Float> ySorted = yData.stream().collect(Collectors.toList());
		xSorted.sort(Float::compare);
		ySorted.sort(Float::compare);
		int index999 = (int)(xSorted.size() * 0.999f);
		

		//float s1max = s1Data.max();
		//float s2max = s2Data.max();
		//mnax value is 95th percentile in histogram
		float xMax = xSorted.get(index999);
		float yMax = ySorted.get(index999);
		
		//if it's absolute, we use the larger max to scale both histograms
		if (map.getFitting().getMapScaleMode() != MapScaleMode.RELATIVE) {
			xMax = Math.max(xMax, yMax);
			yMax = Math.max(xMax, yMax);
		}
		
		
		GridPerspective<Float> grid = new GridPerspective<Float>(CorrelationMapMode.CORRELATION_MAP_SIZE, CorrelationMapMode.CORRELATION_MAP_SIZE, 0f);
		Spectrum correlation = new ISpectrum(CorrelationMapMode.CORRELATION_MAP_SIZE*CorrelationMapMode.CORRELATION_MAP_SIZE);
		for (int i = 0; i < xData.size(); i++) {

			float xpct = xData.get(i) / xMax;
			float ypct = yData.get(i) / yMax;
						
			if (xpct <= 0.01f && ypct <= 0.01f) {
				/*
				 * Don't measure areas where neither element exists, this just creates a large
				 * spike at 0,0 which can drown out everything else
				 */
				continue;
			}
			
			
			int xbin = (int)(xpct*CorrelationMapMode.CORRELATION_MAP_SIZE);
			int ybin = (int)(ypct*CorrelationMapMode.CORRELATION_MAP_SIZE);
			if (xbin >= CorrelationMapMode.CORRELATION_MAP_SIZE) { xbin = CorrelationMapMode.CORRELATION_MAP_SIZE-1; }
			if (ybin >= CorrelationMapMode.CORRELATION_MAP_SIZE) { ybin = CorrelationMapMode.CORRELATION_MAP_SIZE-1; }
			
			grid.set(correlation, xbin, ybin, grid.get(correlation, xbin, ybin)+1);
			
		}

		//clip outlying intense points if selected
		if (clip) {
			SignalOutlierCorrectionMapFilter filter = new SignalOutlierCorrectionMapFilter();
			filter.initialize();
			AreaMap filterContainer = new AreaMap(correlation, new Coord<>(100, 100), null);
			filterContainer = filter.filter(filterContainer);
			correlation = (Spectrum) filterContainer.getData();
		}

		
		CorrelationModeData data = new CorrelationModeData();
		data.data = correlation;
		data.xAxisTitle = getDatasetTitle(xTS) + " (Intensity)";
		data.yAxisTitle = getDatasetTitle(yTS) + " (Intensity)";
		data.xMaxCounts = xMax;
		data.yMaxCounts = yMax;
		
		return data;
	}




	@Override
	public String longTitle() {
		String axis1Title = getDatasetTitle(forSide(1));
		String axis2Title = getDatasetTitle(forSide(2));
		return "Correlation of " + axis1Title + " & " + axis2Title;
	}
	


	
	
	
}
