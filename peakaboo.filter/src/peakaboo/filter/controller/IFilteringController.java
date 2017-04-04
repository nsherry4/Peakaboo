package peakaboo.filter.controller;

import java.util.List;

import eventful.IEventful;
import peakaboo.filter.model.AbstractFilter;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;
import peakaboo.filter.model.FilteringModel;
import scitypes.Spectrum;


public interface IFilteringController extends IEventful
{

	List<String> getAvailableFiltersByName();
	int getFilterCount();
	boolean filterSetContains(Filter f);
	int filterIndex(Filter f);
	List<Filter> getAvailableFilters();
	
	void addFilter(String name);
	void addFilter(Filter f);
	void clearFilters();
	void moveFilterDown(int index);
	void moveFilterUp(int index);
	void removeFilter(int index);
	
	boolean getFilterEnabled(int index);
	void setFilterEnabled(int index, boolean enabled);

	void filteredDataInvalidated();
	void calculateFilteredData(Spectrum data);
	
	Filter getActiveFilter(int index);
	FilterSet getActiveFilters();
	
	Spectrum getFilteredPlot();
	FilteringModel getFilteringMode();


}
