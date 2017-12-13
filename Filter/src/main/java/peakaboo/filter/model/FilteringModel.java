package peakaboo.filter.model;

import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;


public class FilteringModel
{

	// Data related to applying filters. Filters manipulate the dataset
	public FilterSet			filters;
	public ReadOnlySpectrum		filteredPlot;
	
	public FilteringModel()
	{
		filters = new FilterSet();
		filteredPlot = null;
	}
	
}
