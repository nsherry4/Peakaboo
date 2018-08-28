package peakaboo.filter.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
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

	private List<Filter>	filters;


	public FilterSet()
	{

		filters = new ArrayList<>();
	}


	public synchronized void add(Filter filter)
	{
		filters.add(filter);
	}


	public synchronized void add(Filter filter, int index)
	{
		filters.add(index, filter);
	}


	public synchronized Filter get(int index)
	{
		return filters.get(index);
	}


	public synchronized void remove(int index)
	{
		if (index >= filters.size()) return;
		if (index < 0) return;
		filters.remove(index);
	}


	public synchronized void remove(Filter filter)
	{
		filters.remove(filter);
	}


	public synchronized void clear()
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


	public synchronized ReadOnlySpectrum applyFilters(ReadOnlySpectrum data) {
		return applyFilters(data, false);
	}
	
	public synchronized ReadOnlySpectrum applyFilters(ReadOnlySpectrum data, boolean filtersShouldCache)
	{

		return applyFiltersUnsynchronized(data, filtersShouldCache);
	}


	public ReadOnlySpectrum applyFiltersUnsynchronized(ReadOnlySpectrum data) {
		return applyFiltersUnsynchronized(data, false);
	}
	
	public ReadOnlySpectrum applyFiltersUnsynchronized(ReadOnlySpectrum data, boolean filtersShouldCache)
	{

		for (Filter f : filters) {
			if (f != null && f.isEnabled()) {
				data = f.filter(data, filtersShouldCache);
			}
		}
		
		//Replace Inf/NaN with 0
		data = correctNonFinite(data);

		return data;
	}
	
	//Scan the Spectrum for Infinity and NaN values, and replace them with 0 if found
	private ReadOnlySpectrum correctNonFinite(ReadOnlySpectrum data) {
		//Scan the results for Infinity and NaN values, and replace them with 0 if found
		Spectrum corrected = null;
		for (int i = 0; i < data.size(); i++) {
			float v = data.get(i);
			if (Float.isInfinite(v) || Float.isNaN(v)) {
				//only incur the copy penalty if needed
				if (corrected == null) {
					corrected = new ISpectrum(data);
				}
				corrected.set(i, 0);
			}
		}
		if (corrected != null) {
			PeakabooLog.get().log(Level.WARNING, "Filtered data contained NaN or Infinity");
			data = corrected;
		}
		return data;
	}

	public Iterator<Filter> iterator()
	{
		// TODO Auto-generated method stub
		return filters.iterator();
	}



	public synchronized List<Filter> getFilters() {
		return new ArrayList<>(filters);
	}


}
