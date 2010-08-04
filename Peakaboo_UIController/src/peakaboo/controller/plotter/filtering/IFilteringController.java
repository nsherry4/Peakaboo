package peakaboo.controller.plotter.filtering;

import java.util.List;

import eventful.IEventful;
import eventful.IEventfulType;

import peakaboo.filter.AbstractFilter;
import peakaboo.filter.FilterSet;
import scitypes.Spectrum;


public interface IFilteringController extends IEventful
{

	public List<String> getAvailableFiltersByName();
	public int getFilterCount();
	public boolean filterSetContains(AbstractFilter f);
	public int filterIndex(AbstractFilter f);
	public List<AbstractFilter> getAvailableFilters();
	
	public void addFilter(String name);
	public void addFilter(AbstractFilter f);
	public void clearFilters();
	public void moveFilterDown(int index);
	public void moveFilterUp(int index);
	public void removeFilter(int index);
	
	public boolean getFilterEnabled(int index);
	public void setFilterEnabled(int index, boolean enabled);

	public void filteredDataInvalidated();
	public void calculateFilteredData(Spectrum data);
	
	public AbstractFilter getActiveFilter(int index);
	public FilterSet getActiveFilters();
	
	public Spectrum getFilteredPlot();


}
