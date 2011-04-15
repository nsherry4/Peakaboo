package peakaboo.controller.plotter.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eventful.Eventful;

import peakaboo.controller.plotter.PlotController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.FilterSet;
import scitypes.Spectrum;


public class FilteringController extends Eventful implements IFilteringController
{

	PlotController 	plot;
	FilteringModel	filteringModel;
	
	public FilteringController(PlotController plotController)
	{
		this.plot = plotController;
		filteringModel = new FilteringModel();
	}
	
	public FilteringModel getFilteringMode()
	{
		return filteringModel;
	}
	
	public void clearFilters()
	{
		filteringModel.filters.clearFilters();
		plot.undoController.setUndoPoint("Clear Filters");
		filteredDataInvalidated();
	}

	public List<String> getAvailableFiltersByName()
	{
		List<String> filterNames = new ArrayList<String>();

		for (AbstractFilter filter : filteringModel.filters.getAvailableFilters())
		{
			filterNames.add(filter.getPluginName());
		}

		Collections.sort(filterNames);

		return filterNames;
	}

	public List<AbstractFilter> getAvailableFilters()
	{
		return filteringModel.filters.getAvailableFilters();
	}

	public void addFilter(String name)
	{

		for (AbstractFilter f : filteringModel.filters.getAvailableFilters())
		{
			if (f.getPluginName().equals(name))
			{

				try
				{
					// this will call filterschanged, so we don't need to
					// manually update the listeners
					addFilter(f.getClass().newInstance());
					break;
				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}

			}
		}

	}

	public void addFilter(AbstractFilter f)
	{
		filteringModel.filters.addFilter(f);
		plot.undoController.setUndoPoint("Add Filter");
		filteredDataInvalidated();
	}

	public void removeFilter(int index)
	{
		filteringModel.filters.removeFilter(index);
		plot.undoController.setUndoPoint("Remove Filter");
		filteredDataInvalidated();
	}

	public boolean filterSetContains(AbstractFilter f)
	{
		return filteringModel.filters.contains(f);
	}

	public int getFilterCount()
	{
		return filteringModel.filters.size();
	}

	public void setFilterEnabled(int index, boolean enabled)
	{
		filteringModel.filters.setFilterEnabled(index, enabled);
		plot.undoController.setUndoPoint("Enable Filter");
		filteredDataInvalidated();
	}

	public boolean getFilterEnabled(int index)
	{
		return filteringModel.filters.getFilterEnabled(index);
	}

	public void moveFilterUp(int index)
	{
		filteringModel.filters.moveFilterUp(index);
		plot.undoController.setUndoPoint("Move Filter Up");
		filteredDataInvalidated();
	}

	public void moveFilterDown(int index)
	{
		filteringModel.filters.moveFilterDown(index);
		plot.undoController.setUndoPoint("Move Filter Down");
		filteredDataInvalidated();
	}

	public AbstractFilter getActiveFilter(int index)
	{
		return filteringModel.filters.getFilter(index);
	}

	public int filterIndex(AbstractFilter f)
	{
		return filteringModel.filters.indexOf(f);
	}

	
	public void calculateFilteredData(Spectrum data)
	{
		filteringModel.filteredPlot = filteringModel.filters.filterData(data, true);
		updateListeners();
	}
	
	public void filteredDataInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		filteringModel.filteredPlot = null;
		plot.dataController.invalidateFilteredData();

		plot.fittingController.fittingDataInvalidated();
		updateListeners();

	}

	public FilterSet getActiveFilters()
	{
		return filteringModel.filters;
	}

	public Spectrum getFilteredPlot()
	{
		return filteringModel.filteredPlot;
	}
	
}
