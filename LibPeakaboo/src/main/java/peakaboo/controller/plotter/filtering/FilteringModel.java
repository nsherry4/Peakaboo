package peakaboo.controller.plotter.filtering;

import java.util.Map;

import cyclops.ReadOnlySpectrum;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;


public class FilteringModel
{

	// Data related to applying filters. Filters manipulate the dataset
	public FilterSet			filters;
	public ReadOnlySpectrum		filteredPlot;
	public Map<Filter, ReadOnlySpectrum> filterDeltas;
	
	public FilteringModel()
	{
		filters = new FilterSet();
		filteredPlot = null;
		filterDeltas = null;
	}
	
}
