package peakaboo.controller.plotter;

import java.util.List;

import peakaboo.datatypes.eventful.IEventful;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.AbstractFilter.FilterType;


public interface FilterController extends IEventful
{

	public List<String> getAvailableFiltersByName();
	public void addFilter(String name);
	public void addFilter(AbstractFilter f);
	public void filteredDataInvalidated();
	public void clearFilters();
	public int filterIndex(AbstractFilter f);
	public List<AbstractFilter> getAvailableFilters();
	public boolean filterSetContains(AbstractFilter f);
	public AbstractFilter getFilter(int index);
	public int getFilterCount();
	public boolean getFilterEnabled(int index);
	public void moveFilterDown(int index);
	public void moveFilterUp(int index);
	public void removeFilter(int index);
	public void setFilterEnabled(int index, boolean enabled);

}
