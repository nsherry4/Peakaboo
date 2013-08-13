package peakaboo.filter.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import peakaboo.filter.AbstractFilter;
import peakaboo.filter.FilterLoader;
import scitypes.Spectrum;

/**
 * 
 * This class provides a method of managing filters applied to a data set. Also provides the logic for
 * applying the filter set to a data set. is Iterable over AbstractFilters for access to filters in use
 * 
 * 
 * @author Nathaniel Sherry, 2009
 */

public class FilterSet implements Iterable<AbstractFilter>
{

	private List<AbstractFilter>	availableFilters;
	private List<AbstractFilter>	filters;


	public FilterSet()
	{

		filters = new ArrayList<AbstractFilter>();
		availableFilters = FilterLoader.getAvailableFilters();

	}


	public List<AbstractFilter> getAvailableFilters()
	{

		return availableFilters;

	}


	public synchronized void addFilter(AbstractFilter filter)
	{
		filters.add(filter);
	}


	public synchronized void addFilter(AbstractFilter filter, int index)
	{
		filters.add(index, filter);
	}


	public synchronized AbstractFilter getFilter(int index)
	{
		return filters.get(index);
	}


	public synchronized void removeFilter(int index)
	{
		if (index >= filters.size()) return;
		if (index < 0) return;
		filters.remove(index);
	}


	public synchronized void removeFilter(AbstractFilter filter)
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


	public synchronized boolean contains(AbstractFilter f)
	{
		return filters.contains(f);
	}


	public synchronized int indexOf(AbstractFilter f)
	{
		return filters.indexOf(f);
	}


	public synchronized void moveFilterUp(int index)
	{

		AbstractFilter filter = filters.get(index);
		index -= 1;
		if (index < 0) index = 0;


		filters.remove(filter);
		filters.add(index, filter);

	}


	public synchronized void moveFilterDown(int index)
	{

		AbstractFilter filter = filters.get(index);
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

		for (AbstractFilter f : filters) {
			if (f != null && f.enabled) {

				data = f.filter(data, filtersShouldCache);
				
			}
		}

		return data;
	}
	

	public Iterator<AbstractFilter> iterator()
	{
		// TODO Auto-generated method stub
		return filters.iterator();
	}



	public synchronized void setFilterEnabled(int index, boolean enabled)
	{
		filters.get(index).enabled = enabled;
	}


	public synchronized boolean getFilterEnabled(int index)
	{
		return filters.get(index).enabled;
	}


	public synchronized String getFilterName(int index)
	{
		return filters.get(index).getFilterName();
	}



}
