package peakaboo.filter.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import peakaboo.filter.FilterLoader;
import scitypes.Spectrum;

/**
 * 
 * This class provides a method of managing filters applied to a data set. Also provides the logic for
 * applying the filter set to a data set. is Iterable over Filters for access to filters in use
 * 
 * 
 * @author Nathaniel Sherry, 2009
 */

public class FilterSet implements Iterable<Filter>
{

	private List<Filter>	availableFilters;
	private List<Filter>	filters;


	public FilterSet()
	{

		filters = new ArrayList<>();
		availableFilters = new ArrayList<>(FilterLoader.getAvailableFilters());

	}


	public List<Filter> getAvailableFilters()
	{

		return availableFilters;

	}


	public synchronized void addFilter(Filter filter)
	{
		filters.add(filter);
	}


	public synchronized void addFilter(Filter filter, int index)
	{
		filters.add(index, filter);
	}


	public synchronized Filter getFilter(int index)
	{
		return filters.get(index);
	}


	public synchronized void removeFilter(int index)
	{
		if (index >= filters.size()) return;
		if (index < 0) return;
		filters.remove(index);
	}


	public synchronized void removeFilter(Filter filter)
	{
		filters.remove(filter);
	}


	public synchronized void clearFilters()
	{
		filters.clear();
	}


	public synchronized int size()
	{
		return filters.size();
	}


	public synchronized boolean contains(Filter f)
	{
		return filters.contains(f);
	}


	public synchronized int indexOf(Filter f)
	{
		return filters.indexOf(f);
	}


	public synchronized void moveFilterUp(int index)
	{

		Filter filter = filters.get(index);
		index -= 1;
		if (index < 0) index = 0;


		filters.remove(filter);
		filters.add(index, filter);

	}


	public synchronized void moveFilterDown(int index)
	{

		Filter filter = filters.get(index);
		index += 1;
		if (index >= filters.size()) index = filters.size() - 1;

		filters.remove(filter);
		filters.add(index, filter);

	}


	public synchronized Spectrum filterData(Spectrum data, boolean filtersShouldCache)
	{

		return filterDataUnsynchronized(data, filtersShouldCache);
	}


	public Spectrum filterDataUnsynchronized(Spectrum data, boolean filtersShouldCache)
	{

		for (Filter f : filters) {
			if (f != null && f.isEnabled()) {

				data = f.filter(data, filtersShouldCache);
				
			}
		}

		return data;
	}
	

	public Iterator<Filter> iterator()
	{
		// TODO Auto-generated method stub
		return filters.iterator();
	}



	public synchronized void setFilterEnabled(int index, boolean enabled)
	{
		filters.get(index).setEnabled(enabled);
	}


	public synchronized boolean getFilterEnabled(int index)
	{
		return filters.get(index).isEnabled();
	}


	public synchronized String getFilterName(int index)
	{
		return filters.get(index).getFilterName();
	}

	public synchronized List<Filter> getFilters() {
		return new ArrayList<>(filters);
	}


}
