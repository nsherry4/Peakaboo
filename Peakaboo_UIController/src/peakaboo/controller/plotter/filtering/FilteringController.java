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

	@Override
	public FilteringModel getFilteringMode()
	{
		return filteringModel;
	}

	@Override
	public void clearFilters()
	{
		filteringModel.filters.clearFilters();
		plot.undoController.setUndoPoint("Clear Filters");
		filteredDataInvalidated();
	}

	@Override
	public List<String> getAvailableFiltersByName()
	{
		List<String> filterNames = new ArrayList<String>();

		for (AbstractFilter filter : filteringModel.filters.getAvailableFilters())
		{
			filterNames.add(filter.getFilterName());
		}

		Collections.sort(filterNames);

		return filterNames;
	}

	@Override
	public List<AbstractFilter> getAvailableFilters()
	{
		return filteringModel.filters.getAvailableFilters();
	}

	@Override
	public void addFilter(String name)
	{

		for (AbstractFilter f : filteringModel.filters.getAvailableFilters())
		{
			if (f.getFilterName().equals(name))
			{

				try
				{
					// this will call filterschanged, so we don't need to
					// manually update the listeners
					AbstractFilter filter = f.getClass().newInstance();
					filter.initialize();
					addFilter(filter);
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

	@Override
	public void addFilter(AbstractFilter f)
	{
		filteringModel.filters.addFilter(f);
		plot.undoController.setUndoPoint("Add Filter");
		filteredDataInvalidated();
	}

	@Override
	public void removeFilter(int index)
	{
		filteringModel.filters.removeFilter(index);
		plot.undoController.setUndoPoint("Remove Filter");
		filteredDataInvalidated();
	}

	@Override
	public boolean filterSetContains(AbstractFilter f)
	{
		return filteringModel.filters.contains(f);
	}

	@Override
	public int getFilterCount()
	{
		return filteringModel.filters.size();
	}

	@Override
	public void setFilterEnabled(int index, boolean enabled)
	{
		filteringModel.filters.setFilterEnabled(index, enabled);
		plot.undoController.setUndoPoint("Enable Filter");
		filteredDataInvalidated();
	}

	@Override
	public boolean getFilterEnabled(int index)
	{
		return filteringModel.filters.getFilterEnabled(index);
	}

	@Override
	public void moveFilterUp(int index)
	{
		filteringModel.filters.moveFilterUp(index);
		plot.undoController.setUndoPoint("Move Filter Up");
		filteredDataInvalidated();
	}

	@Override
	public void moveFilterDown(int index)
	{
		filteringModel.filters.moveFilterDown(index);
		plot.undoController.setUndoPoint("Move Filter Down");
		filteredDataInvalidated();
	}

	@Override
	public AbstractFilter getActiveFilter(int index)
	{
		return filteringModel.filters.getFilter(index);
	}

	@Override
	public int filterIndex(AbstractFilter f)
	{
		return filteringModel.filters.indexOf(f);
	}


	@Override
	public void calculateFilteredData(Spectrum data)
	{
		filteringModel.filteredPlot = filteringModel.filters.filterData(data, true);
		updateListeners();
	}

	@Override
	public void filteredDataInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		filteringModel.filteredPlot = null;
		plot.dataController.invalidateFilteredData();

		plot.fittingController.fittingDataInvalidated();
		updateListeners();

	}

	@Override
	public FilterSet getActiveFilters()
	{
		return filteringModel.filters;
	}

	@Override
	public Spectrum getFilteredPlot()
	{
		return filteringModel.filteredPlot;
	}
	
}
