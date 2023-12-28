package org.peakaboo.controller.plotter.filtering;

import java.util.Map;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.eventful.cache.EventfulCache;

public class FilteringModel {

	// Data related to applying filters. Filters manipulate the dataset
	FilterSet filters = new FilterSet();
	
	EventfulCache<SpectrumView> filteredPlot;
	EventfulCache<Map<Filter, SpectrumView>> filterDeltas;

}
