package peakaboo.controller.plotter.filtering;

import java.util.Map;

import cyclops.ReadOnlySpectrum;
import eventful.EventfulCache;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;

public class FilteringModel {

	// Data related to applying filters. Filters manipulate the dataset
	FilterSet filters = new FilterSet();
	
	EventfulCache<ReadOnlySpectrum> filteredPlot;
	EventfulCache<Map<Filter, ReadOnlySpectrum>> filterDeltas;

}
