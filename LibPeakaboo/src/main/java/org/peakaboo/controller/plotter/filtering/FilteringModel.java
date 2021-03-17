package org.peakaboo.controller.plotter.filtering;

import java.util.Map;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.eventful.cache.EventfulCache;

public class FilteringModel {

	// Data related to applying filters. Filters manipulate the dataset
	FilterSet filters = new FilterSet();
	
	EventfulCache<ReadOnlySpectrum> filteredPlot;
	EventfulCache<Map<Filter, ReadOnlySpectrum>> filterDeltas;

}
