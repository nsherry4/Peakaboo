package peakaboo.filter.controller;

import java.util.List;

import peakaboo.filter.model.AbstractFilter;
import peakaboo.filter.model.FilterSet;
import peakaboo.filter.model.FilteringModel;
import scitypes.Spectrum;
import eventful.IEventful;


public interface IFilteringController extends IEventful
{

	List<String> getAvailableFiltersByName();
	int getFilterCount();
	boolean filterSetContains(AbstractFilter f);
	int filterIndex(AbstractFilter f);
	List<AbstractFilter> getAvailableFilters();
	
	void addFilter(String name);
	void addFilter(AbstractFilter f);
	void clearFilters();
	void moveFilterDown(int index);
	void moveFilterUp(int index);
	void removeFilter(int index);
	
	boolean getFilterEnabled(int index);
	void setFilterEnabled(int index, boolean enabled);

	void filteredDataInvalidated();
	void calculateFilteredData(Spectrum data);
	
	AbstractFilter getActiveFilter(int index);
	FilterSet getActiveFilters();
	
	Spectrum getFilteredPlot();
	FilteringModel getFilteringMode();


}
