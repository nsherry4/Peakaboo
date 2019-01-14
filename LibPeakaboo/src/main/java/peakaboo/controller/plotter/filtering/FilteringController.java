package peakaboo.controller.plotter.filtering;

import java.util.Map;
import java.util.logging.Level;

import cyclops.ReadOnlySpectrum;
import eventful.Eventful;
import eventful.EventfulCache;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.plotter.PlotController;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;


public class FilteringController extends Eventful
{

	PlotController	plot;
	FilteringModel	filteringModel;
	
	public FilteringController(PlotController plotController)
	{
		this.plot = plotController;
		filteringModel = new FilteringModel();
		
		filteringModel.filteredPlot = new EventfulCache<>(() -> {
			if (plot.currentScan() == null) {
				return null;
			}
			return filteringModel.filters.applyFilters(plot.currentScan(), true);
		});
		
		filteringModel.filterDeltas = new EventfulCache<>(() -> { 
			return filteringModel.filters.calculateDeltas(plot.currentScan());
		});
				
		filteringModel.filteredPlot.addListener(this::updateListeners);
		filteringModel.filterDeltas.addUpstreamDependency(filteringModel.filteredPlot);
		//Don't bother with the filterDeltas listener, these two things will only ever be invalidated together.
		//filteringModel.filterDeltas.addListener(this::updateListeners);
	}

	public FilteringModel getFilteringModel()
	{
		return filteringModel;
	}

	public void clearFilters()
	{
		filteringModel.filters.clear();
		plot.history().setUndoPoint("Clear Filters");
		filteredDataInvalidated();
	}


	
	public void addFilter(Filter f)
	{
		filteringModel.filters.add(f);
		plot.history().setUndoPoint("Add Filter");
		filteredDataInvalidated();
	}

	public void moveFilter(int from, int to) {
		//we'll be removing the item from the list, so if the 
		//destination is greater than the source, decrement it 
		//to make up the difference
		if (to > from) { to--; }
		
		Filter filter = filteringModel.filters.get(from);
		filteringModel.filters.remove(from);
		filteringModel.filters.add(filter, to);
		plot.history().setUndoPoint("Move Filter");
		filteredDataInvalidated();
	}
	
	public void removeFilter(int index)
	{
		filteringModel.filters.remove(index);
		plot.history().setUndoPoint("Remove Filter");
		filteredDataInvalidated();
	}


	public int getFilterCount()
	{
		return filteringModel.filters.size();
	}

	public void setFilterEnabled(int index, boolean enabled)
	{
		filteringModel.filters.get(index).setEnabled(enabled);
		plot.history().setUndoPoint("Enable Filter");
		filteredDataInvalidated();
	}

	public boolean getFilterEnabled(int index)
	{
		return filteringModel.filters.get(index).isEnabled();
	}

	public void moveFilterUp(int index)
	{
		filteringModel.filters.moveFilterUp(index);
		plot.history().setUndoPoint("Move Filter Up");
		filteredDataInvalidated();
	}

	public void moveFilterDown(int index)
	{
		filteringModel.filters.moveFilterDown(index);
		plot.history().setUndoPoint("Move Filter Down");
		filteredDataInvalidated();
	}

	public Filter getActiveFilter(int index)
	{
		return filteringModel.filters.get(index);
	}

	public int filterIndex(Filter f)
	{
		return filteringModel.filters.indexOf(f);
	}


	public void filteredDataInvalidated()
	{
		PeakabooLog.get().log(Level.FINE, "Filter Data Invalidated");
		// Clear cached values, since they now have to be recalculated
		filteringModel.filteredPlot.invalidate();
	}

	public FilterSet getActiveFilters()
	{
		return filteringModel.filters;
	}

	public ReadOnlySpectrum getFilteredPlot() {
		return filteringModel.filteredPlot.getValue();
	}
	
	public EventfulCache<ReadOnlySpectrum> getFilteredPlotCache() {
		return filteringModel.filteredPlot;
	}
	
	public Map<Filter, ReadOnlySpectrum> getFilterDeltas() {
		return filteringModel.filterDeltas.getValue();
	}


	
}
