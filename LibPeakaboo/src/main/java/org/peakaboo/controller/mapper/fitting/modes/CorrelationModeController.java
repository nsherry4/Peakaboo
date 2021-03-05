package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.components.BinState;
import org.peakaboo.controller.mapper.fitting.modes.components.GroupState;
import org.peakaboo.controller.mapper.fitting.modes.components.TranslationState;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.correlation.CorrelationModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.SignalOutlierCorrectionMapFilter;

public class CorrelationModeController extends SimpleModeController {

	private MappingController map;
	private GroupState groups;
	private BinState bins;
	private TranslationState translation;
	private boolean clip = false;
	
		
	public CorrelationModeController(MappingController map) {
		super(map);
		this.map = map;
		this.bins = new BinState(this);
		this.groups = new GroupState(this);
		this.translation = new TranslationState(this);
	}


	///// Grouping delegators /////
	
	public List<ITransitionSeries> forSide(final int side) { return groups.getVisibleMembers(side); }
	
	public int getSide(ITransitionSeries ts) { return this.groups.getGroup(ts); }
	
	public void setSide(ITransitionSeries ts, int side) {
		this.groups.setGroup(ts, side);
		updateListeners();
	}


	///// Binning delegators /////
	public int getBins() { return bins.getCount(); }

	public void setBins(int bins) { this.bins.setCount(bins); }


	///// Cliping /////
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
		

		//max value is 99.9th percentile in histogram
		float xMax = xSorted.get(index999);
		float yMax = ySorted.get(index999);
		if (xMax == 0) { xMax = 1; }
		if (yMax == 0) { yMax = 1; }
		
		//if it's absolute, we use the larger max to scale both histograms
		if (map.getFitting().getMapScaleMode() != MapScaleMode.RELATIVE) {
			xMax = Math.max(xMax, yMax);
			yMax = Math.max(xMax, yMax);
		}
		
		int bincount = bins.getCount();
		GridPerspective<Float> grid = new GridPerspective<>(bincount, bincount, 0f);
		Spectrum correlation = new ISpectrum(bincount*bincount);
		
		//we track which points on the original (spatial) maps each bin in the correlation map
		//comes from so that selections can be mapped back to them
		translation.initialize(bincount*bincount);
		
		for (int i = 0; i < xData.size(); i++) {

			float xpct = Math.max(0, xData.get(i)) / xMax;
			if (xMax == 0) { xpct = 0f; }
			float ypct = Math.max(0, yData.get(i)) / yMax;
			if (yMax == 0) { ypct = 0f; }
			
			if (xpct <= 0.01f && ypct <= 0.01f) {
				/*
				 * Don't measure areas where neither element exists, this just creates a large
				 * spike at 0,0 which can drown out everything else
				 */
				continue;
			}
			
			
			int xbin = (int)(xpct*bincount);
			int ybin = (int)(ypct*bincount);
			if (xbin >= bincount) { xbin = bincount-1; }
			if (ybin >= bincount) { ybin = bincount-1; }
			
			int bindex = grid.getIndexFromXY(xbin, ybin);
			if (bindex == -1 || bindex > bincount*bincount) {
				//index was out of bounds
				Map<String, Number> valueMap = new HashMap<>();
				valueMap.put("xMax", xMax);
				valueMap.put("yMax", yMax);
				valueMap.put("xpct", xpct);
				valueMap.put("ypct", ypct);
				valueMap.put("xbin", xbin);
				valueMap.put("ybin", ybin);
				String values = valueMap.entrySet().stream().map(e -> "\t" + e.getKey() + ": " + e.getValue().toString()).reduce((a, b) -> a + "\n" + b).get();
				throw new IndexOutOfBoundsException("index " + bindex + "is not within the expected range of 0 to " + bincount*bincount + "\n" + values);
			}
			translation.add(bindex, i);
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

		
		CorrelationModeData data = new CorrelationModeData(bincount);
		data.data = correlation;
		data.xAxisTitle = getDatasetTitle(xTS) + " (Intensity)";
		data.yAxisTitle = getDatasetTitle(yTS) + " (Intensity)";
		data.xMaxCounts = xMax;
		data.yMaxCounts = yMax;
		
		return data;
	}

	@Override
	public Coord<Integer> getSize() {
		int bincount = bins.getCount();
		return new Coord<>(bincount, bincount);
	}



	@Override
	public String longTitle() {
		String axis1Title = getDatasetTitle(forSide(1));
		String axis2Title = getDatasetTitle(forSide(2));
		return "Correlation of " + axis1Title + " & " + axis2Title;
	}
	

	@Override
	public boolean isSpatial() {
		return false;
	}

	@Override
	public boolean isTranslatableToSpatial() {
		return true;
	}
	
	@Override
	public List<Integer> translateSelectionToSpatial(List<Integer> points) {
		return translation.toSpatial(points);
	}	
	
}
