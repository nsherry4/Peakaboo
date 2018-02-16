package peakaboo.controller.plotter.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eventful.Eventful;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterLoader;
import peakaboo.filter.model.FilterSet;
import peakaboo.filter.model.FilteringModel;
import peakaboo.filter.model.plugin.FilterPlugin;
import scitypes.ReadOnlySpectrum;


public class FilteringController extends Eventful implements IFilteringController
{

	IPlotController	plot;
	FilteringModel	filteringModel;
	
	public FilteringController(IPlotController plotController)
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
		filteringModel.filters.clear();
		plot.history().setUndoPoint("Clear Filters");
		filteredDataInvalidated();
	}


	
	@Override
	public void addFilter(Filter f)
	{
		filteringModel.filters.add(f);
		plot.history().setUndoPoint("Add Filter");
		filteredDataInvalidated();
	}

	@Override
	public void removeFilter(int index)
	{
		filteringModel.filters.remove(index);
		plot.history().setUndoPoint("Remove Filter");
		filteredDataInvalidated();
	}

	@Override
	public boolean filterSetContains(Filter f)
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
		filteringModel.filters.get(index).setEnabled(enabled);
		plot.history().setUndoPoint("Enable Filter");
		filteredDataInvalidated();
	}

	@Override
	public boolean getFilterEnabled(int index)
	{
		return filteringModel.filters.get(index).isEnabled();
	}

	@Override
	public void moveFilterUp(int index)
	{
		filteringModel.filters.moveFilterUp(index);
		plot.history().setUndoPoint("Move Filter Up");
		filteredDataInvalidated();
	}

	@Override
	public void moveFilterDown(int index)
	{
		filteringModel.filters.moveFilterDown(index);
		plot.history().setUndoPoint("Move Filter Down");
		filteredDataInvalidated();
	}

	@Override
	public Filter getActiveFilter(int index)
	{
		return filteringModel.filters.get(index);
	}

	@Override
	public int filterIndex(Filter f)
	{
		return filteringModel.filters.indexOf(f);
	}


	@Override
	public void calculateFilteredData(ReadOnlySpectrum data)
	{
		filteringModel.filteredPlot = filteringModel.filters.applyFilters(data, true);
		updateListeners();
	}

	@Override
	public void filteredDataInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		filteringModel.filteredPlot = null;

		plot.fitting().fittingDataInvalidated();
		updateListeners();

	}

	@Override
	public FilterSet getActiveFilters()
	{
		return filteringModel.filters;
	}

	@Override
	public ReadOnlySpectrum getFilteredPlot()
	{
		return filteringModel.filteredPlot;
	}


	
}
