package peakaboo.controller.plotter.filtering;

import java.util.List;

import eventful.IEventful;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;
import peakaboo.filter.model.FilteringModel;
import scitypes.ReadOnlySpectrum;


public interface IFilteringController extends IEventful
{

	List<String> getAvailableFiltersByName();
	int getFilterCount();
	boolean filterSetContains(Filter f);
	int filterIndex(Filter f);
	List<Filter> getAvailableFilters();
	
	void addFilter(String filterName);
	void addFilter(Filter f);
	void clearFilters();
	void moveFilterDown(int index);
	void moveFilterUp(int index);
	void removeFilter(int index);
	
	boolean getFilterEnabled(int index);
	void setFilterEnabled(int index, boolean enabled);

	void filteredDataInvalidated();
	void calculateFilteredData(ReadOnlySpectrum data);
	
	Filter getActiveFilter(int index);
	FilterSet getActiveFilters();
	
	ReadOnlySpectrum getFilteredPlot();
	FilteringModel getFilteringMode();


}
