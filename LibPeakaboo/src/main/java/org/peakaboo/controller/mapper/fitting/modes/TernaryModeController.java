package org.peakaboo.controller.mapper.fitting.modes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.components.BinState;
import org.peakaboo.controller.mapper.fitting.modes.components.GroupState;
import org.peakaboo.controller.mapper.fitting.modes.components.SelectabilityState;
import org.peakaboo.controller.mapper.fitting.modes.components.TranslationState;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.display.map.modes.ternary.TernaryModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.SignalOutlierCorrectionMapFilter;

/**
 * Ternary plots represent a way of visualizing membership in one of three groups: A, B, or neither
 */
public class TernaryModeController extends SimpleModeController {

	private TranslationState translation;
	private BinState bins;
	private GroupState groups;
	private SelectabilityState selectability;
	private boolean clip;
	
	public static final String X_AXIS_LABEL = "→";
	public static final String Y_AXIS_LABEL = "↑";
	public static final String O_AXIS_LABEL = "↙";
	
	public TernaryModeController(MappingController map) {
		super(map);
		
		this.bins = new BinState(this);
		this.translation = new TranslationState(this);
		this.groups = new GroupState(this);
		this.selectability = new SelectabilityState(this);
	}
	
	@Override
	public Coord<Integer> getSize() {
		int size = this.bins.getCount();
		return new Coord<>(size, size);
	}

	@Override
	public String longTitle() {
		String axis1Title = getDatasetTitle(forSide(1));
		String axis2Title = getDatasetTitle(forSide(2));
		String axis3Title = getDatasetTitle(forSide(3));
		return "Ternary Plot of " + 
				X_AXIS_LABEL + " (" + axis1Title + "), " +
				Y_AXIS_LABEL + " (" + axis2Title + ") & " +
				O_AXIS_LABEL + " (" + axis3Title + ")";
	}

	@Override
	public MapModeData getData() {

		/*
		 * The basic idea here is to take each point in the spatial map and plot it
		 * somewhere in this ternary plot. For each point, we determine how much of its
		 * total signal originates from elements belonging to the x-axis group and to
		 * the y-axis group and plot it accordinly.
		 * 
		 * Nothing will ever be plotted beyond the y=1-x line (where 1 is 100%) since a
		 * point's signal cannot originate 60% from iron *and* 60% from zinc.
		 */
		
		int bincount = bins.getCount();
		
		// get transition series for our axes
		List<ITransitionSeries> xTS = forSide(1);
		List<ITransitionSeries> yTS = forSide(2);
		List<ITransitionSeries> oTS = forSide(3);
		
		// sum all of the maps for the given transition series for each side
		Spectrum xData = super.sumGivenMaps(xTS);
		Spectrum yData = super.sumGivenMaps(yTS);
		Spectrum allData = super.sumVisibleMaps();
		
		
		//initialize translation lookup data
		translation.initialize(bincount*bincount);
		
		//final output spectrum and 2D viewer
		GridPerspective<Float> grid = new GridPerspective<>(bincount, bincount, 0f);
		Spectrum ternaryplot = new ISpectrum(bincount*bincount);
		
		// For each point in the spatial data, get the total intensity and %
		// contribution from our two groups
		for (int i = 0; i < xData.size(); i++) {
			float total = allData.get(i);
			float xSignal = xData.get(i);
			float ySignal = yData.get(i);
			
			//calculate a bin along each axis
			int xbin = calculateBin(xSignal, total, bincount);
			int ybin = calculateBin(ySignal, total, bincount);
			
			//place a count in that bin and an entry in the translation data
			int bindex = grid.getIndexFromXY(xbin, ybin);
			if (bindex == -1 || bindex > bincount*bincount) {
				//index was out of bounds
				Map<String, Number> valueMap = new HashMap<>();
				valueMap.put("xbin", xbin);
				valueMap.put("ybin", ybin);
				String values = valueMap.entrySet().stream().map(e -> "\t" + e.getKey() + ": " + e.getValue().toString()).reduce((a, b) -> a + "\n" + b).get();
				throw new IndexOutOfBoundsException("index " + bindex + "is not within the expected range of 0 to " + bincount*bincount + "\n" + values);
			}
			translation.add(bindex, i);
			ternaryplot.set(bindex, ternaryplot.get(bindex)+1);
			
		}
		
		//generate list of unselectable points
		selectability.clear();
		Coord<Integer> dataSize = getSize();
		for (int x = 0; x < dataSize.x; x++) {
			for (int y = 0; y < dataSize.y; y++) {
				if (dataSize.x - x <= y) {
					selectability.set(x, y, false);
				}
			}
		}
		
		//clip outlying intense points if selected
		if (clip) {
			SignalOutlierCorrectionMapFilter filter = new SignalOutlierCorrectionMapFilter();
			filter.initialize();
			AreaMap filterContainer = new AreaMap(ternaryplot, Collections.emptyList(), new Coord<>(100, 100), null);
			filterContainer = filter.filter(filterContainer);
			ternaryplot = (Spectrum) filterContainer.getData();
		}
		
		TernaryModeData data = new TernaryModeData(bincount);
		data.data = ternaryplot;
		data.xCornerTitle = getDatasetTitle(xTS);
		data.yCornerTitle = getDatasetTitle(yTS);
		data.oCornerTitle = getDatasetTitle(oTS);
		data.xMaxCounts = 100;
		data.yMaxCounts = 100;
		data.unselectables = selectability.unselectables();
		
		return data;
		
	}
	
	private float calculateMax(Spectrum data) {
		List<Float> sorted = data.stream().collect(Collectors.toList());
		sorted.sort(Float::compare);
		int index999 = (int)(sorted.size() * 0.999f);
		//max value is 99.9th percentile in histogram
		float max = sorted.get(index999);
		if (max == 0) { max = 1; }
		return max;
	}
	
	private int calculateBin(float signal, float max, int bincount) {
		float percent = signal / max;
		if (percent > 1f) { percent = 1f; }
		int bin = (int) Math.floor(bincount * percent);
		if (bin == bincount) { bin = bincount - 1; }
		return bin;
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
	
	@Override
	public List<Integer> filterSelection(List<Integer> points) {
		return selectability.filter(points);
	}
	

	///// Cliping Outliers /////
	public boolean isClip() {
		return clip;
	}

	public void setClip(boolean clip) {
		this.clip = clip;
		updateListeners();
	}

	///// Grouping delegators /////
	public List<ITransitionSeries> forSide(int side) { return groups.getVisibleMembers(side); }
	public int getSide(ITransitionSeries ts) { return groups.getGroup(ts); }
	public void setSide(ITransitionSeries ts, int side) { groups.setGroup(ts, side); }
	
	///// Binning delegators /////
	public int getBins() { return bins.getCount(); }
	public void setBins(int bins) { this.bins.setCount(bins); }
	
}
