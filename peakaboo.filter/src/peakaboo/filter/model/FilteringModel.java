package peakaboo.filter.model;

import scitypes.Spectrum;


public class FilteringModel
{

	// Data related to applying filters. Filters manipulate the dataset
	public FilterSet			filters;
	public Spectrum				filteredPlot;
	
	public FilteringModel()
	{
		filters = new FilterSet();
		filteredPlot = null;
	}
	
}
