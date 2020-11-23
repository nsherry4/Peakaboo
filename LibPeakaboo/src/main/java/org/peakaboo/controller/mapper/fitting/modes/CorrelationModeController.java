package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
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
	private int bins = 100;
	
	private Map<Integer, List<Integer>> translation = new LinkedHashMap<>();
	private boolean invalidated = true;
	
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


	public int getBins() {
		return bins;
	}

	public void setBins(int bins) {
		if (bins != this.bins) {
			this.bins = bins;
			updateListeners();
		}
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
		

		//mnax value is 99.9th percentile in histogram
		float xMax = xSorted.get(index999);
		float yMax = ySorted.get(index999);
		
		//if it's absolute, we use the larger max to scale both histograms
		if (map.getFitting().getMapScaleMode() != MapScaleMode.RELATIVE) {
			xMax = Math.max(xMax, yMax);
			yMax = Math.max(xMax, yMax);
		}
		
		
		GridPerspective<Float> grid = new GridPerspective<>(bins, bins, 0f);
		Spectrum correlation = new ISpectrum(bins*bins);
		
		//we track which points on the original (spatial) maps each bin in the correlation map
		//comes from so that selections can be mapped back to them
		translation.clear();
		for (int i = 0; i < bins*bins; i++) {
			translation.put(i, new ArrayList<>());
		}
		invalidated = false;
		
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
			
			
			int xbin = (int)(xpct*bins);
			int ybin = (int)(ypct*bins);
			if (xbin >= bins) { xbin = bins-1; }
			if (ybin >= bins) { ybin = bins-1; }
			
			int bindex = grid.getIndexFromXY(xbin, ybin);
			if (bindex == -1 || bindex > bins*bins) {
				//index was out of bounds
				Map<String, Number> valueMap = Map.of("xMax", xMax, "yMax", yMax, "xpct", xpct, "ypct", ypct, "xbin", xbin, "ybin", ybin);
				String values = valueMap.entrySet().stream().map(e -> "\t" + e.getKey() + ": " + e.getValue().toString()).reduce((a, b) -> a + "\n" + b).get();
				throw new IndexOutOfBoundsException("index " + bindex + "is not within the expected range of 0 to " + bins*bins + "\n" + values);
			}
			translation.get(bindex).add(i);
			correlation.set(bindex, correlation.get(bindex)+1);
			
		}

		//clip outlying intense points if selected
		if (clip) {
			SignalOutlierCorrectionMapFilter filter = new SignalOutlierCorrectionMapFilter();
			filter.initialize();
			AreaMap filterContainer = new AreaMap(correlation, Collections.emptyList(), new Coord<>(100, 100), null);
			filterContainer = filter.filter(filterContainer);
			correlation = (Spectrum) filterContainer.getData();
		}

		
		CorrelationModeData data = new CorrelationModeData(bins);
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
	

	@Override
	public boolean isTranslatable() {
		return true;
	}

	@Override
	public boolean isSpatial() {
		return false;
	}

	@Override
	public List<Integer> translateSelection(List<Integer> points) {
		if (invalidated) {
			getData();
		}
		Set<Integer> translated = new HashSet<>();
		for (int i : points) {
			translated.addAll(translation.get(i));
		}
		return new ArrayList<>(translated);
	}
	
	public Coord<Integer> getDimensions() {
		return new Coord<>(bins, bins);
	}
	
	@Override
	public boolean isComparable() {
		return true;
	}
	
	
	
}
