package org.peakaboo.controller.mapper.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.datalabel.DataScope;
import org.peakaboo.datalabel.DataTag;
import org.peakaboo.datalabel.DataTags;
import org.peakaboo.framework.accent.numeric.Bounds;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.framework.eventful.cache.CacheIterable;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;
import org.peakaboo.framework.eventful.cache.EventfulSoftCache;
import org.peakaboo.mapping.filter.Interpolation;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.MapFilterSet;
import org.peakaboo.mapping.rawmap.RawMapSet;

public class MapFilteringController extends EventfulType<MapUpdateType> {

	private MappingController controller;
	
	private MapFilterSet filters = new MapFilterSet();
	// Nominal maps are processed, raw (non-nominal) ones aren't. We keep both so
	// toggling a non-nominal map's visibility doesn't re-filter everything
	private EventfulCache<CachedMaps> nominalMaps;
	private EventfulCache<CachedMaps> rawMaps;
	
	public MapFilteringController(MappingController controller) {
		this.controller = controller;
		nominalMaps = new EventfulNullableCache<>(() -> new CachedMaps(
				controller, 
				filters, 
				controller.rawDataController.getDetectorProfile(), 
				controller.getFitting()::isTransitionSeriesNominal
			));
		rawMaps = new EventfulNullableCache<>(() -> new CachedMaps(
				controller, 
				filters, 
				new BasicDetectorProfile(), 
				ts -> true
			));
		
		controller.addListener(t -> {
			if (t == MapUpdateType.DATA || t == MapUpdateType.DATA_SIZE) {
				filteredDataInvalidated();
			}
		});
		
	}
	
	public int getFilteredDataWidth() {
		return getSummedMap().getSize().x;
	}
		
	public int getFilteredDataHeight() {
		return getSummedMap().getSize().y;
	}
	
	public boolean isPointInBounds(Coord<Integer> mapCoord)
	{
		return (mapCoord.x >= 0 && mapCoord.x < getFilteredDataWidth() && mapCoord.y >= 0 && mapCoord.y < getFilteredDataHeight());
	}
	
	
	/**
	 * Returns true if and only if the size of the filtered maps does not match the
	 * user specified size.
	 */
	private boolean filteringChangedMapSize() {
		return 
				controller.getUserDimensions().getUserDataWidth() != getFilteredDataWidth()
				||
				controller.getUserDimensions().getUserDataHeight() != getFilteredDataHeight();
	}
	
	/**
	 * Indicates that the map's pixels still line up with the input spectra
	 * @return true if the map pixels still line up with the input spectra, false otherwise
	 */
	public boolean isReplottable() {
		if (filteringChangedMapSize()) return false;
		if (! current().isReplottable()) return false;
		return true;
	}
	
	
	/**
	 * Returns the cached maps matching the maps being shown: if any visible map is
	 * non-nominal, none of them are processed and the raw maps are returned.
	 */
	private CachedMaps current() {
		return controller.getFitting().isNominal() ? nominalMaps.getValue() : rawMaps.getValue();
	}
	
	private EventfulSoftCache<AreaMap> getAreaMap(ITransitionSeries ts) {
		return current().getCachedMap(ts);
	}
	
	public Iterable<AreaMap> getAreaMaps(List<ITransitionSeries> tss) {
		return new CacheIterable<>(tss.stream().map(this::getAreaMap).collect(Collectors.toList()));
	}
	
	public List<Integer> getInvalidPoints() {
		return current().getInvalidPoints();
	}
	
	/**
	 * Returns the sum of all maps, or null, if there are no maps. NOTE: When the
	 * maps being shown are nominal, only nominal maps are included so that the
	 * sum shares their units.
	 */
	public AreaMap getSummedMap() {
		return current().getSum();
	}
	

	public boolean add(MapFilter e) {
		boolean result = filters.add(e);
		filteredDataInvalidated();
		return result;
	}

	public boolean remove(MapFilter o) {
		boolean result = filters.remove(o);
		filteredDataInvalidated();
		return result;
	}

	public void moveMapFilter(int from, int to) {
		//we'll be removing the item from the list, so if the 
		//destination is greater than the source, decrement it 
		//to make up the difference
		if (to > from) { to--; }
		
		MapFilter filter = filters.get(from);
		filters.remove(from);
		filters.add(to, filter);
		filteredDataInvalidated();
	}
	
	public void clear() {
		filters.clear();
		filteredDataInvalidated();
	}

	public MapFilter get(int index) {
		return filters.get(index);
	}

	public MapFilter remove(int index) {
		MapFilter filter = filters.remove(index);
		filteredDataInvalidated();
		return filter;
	}

	public boolean contains(MapFilter o) {
		return filters.contains(o);
	}

	public List<MapFilter> getAll() {
		return filters.getAll();
	}
	
	public List<MapFilter> getAllEnabled() {
		return filters.getAllEnabled();
	}

	public int size() {
		return filters.size();
	}

	public void setMapFilterEnabled(int index, boolean enabled) {
		filters.get(index).setEnabled(enabled);
		filteredDataInvalidated();
	}
	
	public void filteredDataInvalidated() {
		nominalMaps.invalidate();
		rawMaps.invalidate();
		updateListeners(MapUpdateType.FILTER);
	}
	
	
	/**
	 * Returns the deduplicated {@link DataTag}s for this map: tags inherited from the
	 * plot-stage processing which generated the map data, followed by tags from the
	 * enabled map filters.
	 */
	public List<DataTag> getDataTags() {
		List<DataTag> tags = new ArrayList<>(controller.rawDataController.getSourceTags());
		tags.addAll(filters.getDataTags());
		return DataTags.unique(tags);
	}

	public String getActionDescription() {
		return DataTags.summary(getDataTags(), DataScope.MAP).orElse(null);
	}


	/**
	 * Returns true if the map data is being changed by the filters
	 */
	public boolean isFiltering() {
		return filters.getAll().stream().map(MapFilter::isEnabled).reduce(false, (a, b) -> a || b);
	}
	
	public Coord<Bounds<Number>> getRealDimensions() {
		AreaMap sum = current().getSum();
		if (sum == null) {
			return null;
		}
		return sum.getRealDimensions();
	}
	

	
}

class CachedMaps {
	
	private Map<ITransitionSeries, EventfulSoftCache<AreaMap>> maps;
	private List<Integer> invalidPoints;
	private AreaMap sum;
	private boolean replottable;
	
	/**
	 * @param profile the profile to apply to each map
	 * @param summed  which maps to include in the sum
	 */
	public CachedMaps(MappingController controller, MapFilterSet filters, DetectorProfile profile, Predicate<ITransitionSeries> summed) {

		this.maps = new ConcurrentHashMap<>();
		this.replottable = filters.isReplottable();
		
		Coord<Integer> size = controller.getUserDimensions().getDimensions();

		//get calibrated map data and generate AreaMaps
		RawMapSet rawmaps = controller.rawDataController.getMapResultSet();
		rawmaps.stream().parallel().forEach(rawmap -> {
			ITransitionSeries ts = rawmap.transitionSeries;
			EventfulSoftCache<AreaMap> mapcache = new EventfulSoftCache<>(() -> {
				//turn a rawmap into an AreaMap with the detector profile applied
				SpectrumView calibrated = rawmaps.getMap(ts).getData(profile);
				AreaMap areamap = new AreaMap(calibrated, ts.getElement(), size, controller.rawDataController.getRealDimensions());
				//interpolate points marked by the user as bad. We do this 
				//before filtering so that we don't need to worry about filters
				//"smearing" bad points, causing them to taint other points
				// TODO: This doesn't work properly on non-rectangular maps with invalid points.
				// The badpoints data is an index into the list of *valid* points, not
				// dimensional data
				areamap = Interpolation.interpolateBadPoints(areamap, controller.rawDataController.getBadIndices());
				//Then we actually apply the filters and return the result
				areamap = filters.apply(areamap);
				return areamap;
			});
			
			maps.put(ts, mapcache);
		});
		
		/*
		 * Apply the same filters to a map of invalid data points. When we apply filters
		 * to maps which have invalid points (such as a non-rectangular map) the invalid
		 * points tend to 'smear' and we need to track which other points have been
		 * influenced by these invalid points.
		 * 
		 * TODO: Filters should be aware of invalid points, both when reading the data
		 * and when generating new maps (eg deskew)
		 */
		Spectrum invalidMask = Spectrum.fromPoints(controller.rawDataController.getInvalidPoints(), rawmaps.getMaps().get(0).size());
		AreaMap invalidMap = new AreaMap(invalidMask, Collections.emptyList(), size, controller.rawDataController.getRealDimensions());
		invalidMap = filters.apply(invalidMap);
		invalidPoints = Spectrum.toPoints(invalidMap.getData());
		
		//calculate the sum total map, replacing it with an empty map if there's nothing to sum
		List<ITransitionSeries> toSum = maps.keySet().stream().filter(summed).toList();
		if (toSum.size() > 0) {
			Spectrum total = new ArraySpectrum(size.x * size.y);
			AreaMap map = null;
			Set<Element> elements = new LinkedHashSet<>();
			//we do it this way so that we only have to hold one in memory at a time
			//that way the soft references can collect it when we're done adding it in
			for (ITransitionSeries key : toSum) {
				map = maps.get(key).getValue();
				SpectrumCalculations.addLists_inplace(total, map.getData());
				elements.addAll(map.getElements());
			}
			sum = new AreaMap(total, new ArrayList<>(elements), map.getSize(), map.getRealDimensions());
		} else {
			// Keep the real dimensions, since a nominal sum can be empty while there are still maps
			sum = new AreaMap(new ArraySpectrum(size.x * size.y), Collections.emptyList(), size, controller.rawDataController.getRealDimensions());
			sum = filters.apply(sum);
		}
		
	}
	
	public AreaMap getAreaMap(ITransitionSeries ts) {
		return maps.get(ts).getValue();
	}
	
	public EventfulSoftCache<AreaMap> getCachedMap(ITransitionSeries ts) {
		return maps.get(ts);
	}
	
	public boolean isReplottable() {
		return replottable;
	}
	
	public AreaMap getSum() {
		return sum;
	}
	
	public List<Integer> getInvalidPoints() {
		return Collections.unmodifiableList(invalidPoints);
	}
	
	
}
