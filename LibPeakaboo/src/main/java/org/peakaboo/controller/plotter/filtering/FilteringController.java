package org.peakaboo.controller.plotter.filtering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.eventful.EventfulBeacon;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;


public class FilteringController extends EventfulBeacon
{

	PlotController	plot;
	FilteringModel	filteringModel;
	
	public FilteringController(PlotController plotController)
	{
		this.plot = plotController;
		filteringModel = new FilteringModel();
		
		filteringModel.filteredPlot = new EventfulNullableCache<>(() -> {
			if (plot.currentScan() == null) {
				return null;
			}
			return filteringModel.filters.applyFilters(plot.currentScan(), plot.getFilterContext());
		});
		
		filteringModel.filterDeltas = new EventfulNullableCache<>(() -> { 
			return filteringModel.filters.calculateDeltas(plot.currentScan(), plot.getFilterContext());
		});
				
		filteringModel.filteredOtherPlots = new EventfulNullableCache<>(() -> {
			
			Map<String, SpectrumView> filteredOthers = new HashMap<>();
			plot.currentOtherScans().forEach((key, spectrum) -> {
				var filtered = filteringModel.filters.applyFilters(spectrum, plot.getFilterContext());
				filteredOthers.put(key, filtered);
			});
			return filteredOthers;
		});

		filteringModel.filteredPlot.addListener(this::updateListeners);
		filteringModel.filterDeltas.dependsOn(filteringModel.filteredPlot);
		filteringModel.filteredOtherPlots.dependsOn(filteringModel.filteredPlot);

	}

	public FilteringModel getFilteringModel()
	{
		return filteringModel;
	}

	public void clearFilters()
	{
		filteringModel.filters.clear();
		plot.history().setUndoPoint("Clear Filters", /*distinctChange =*/ true);
		filteredDataInvalidated();
	}


	
	public void addFilter(Filter f)
	{
		filteringModel.filters.add(f);
		plot.history().setUndoPoint("Add Filter", /*distinctChange =*/ true);
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
		plot.history().setUndoPoint("Move Filter", /*distinctChange =*/ true);
		filteredDataInvalidated();
	}
	
	public void removeFilter(int index)
	{
		filteringModel.filters.remove(index);
		plot.history().setUndoPoint("Remove Filter", /*distinctChange =*/ true);
		filteredDataInvalidated();
	}


	public int getFilterCount()
	{
		return filteringModel.filters.size();
	}

	public void setFilterEnabled(int index, boolean enabled)
	{
		filteringModel.filters.get(index).setEnabled(enabled);
		plot.history().setUndoPoint("Enable Filter", /*distinctChange =*/ true);
		filteredDataInvalidated();
	}

	public boolean getFilterEnabled(int index)
	{
		return filteringModel.filters.get(index).isEnabled();
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

	public SpectrumView getFilteredPlot() {
		return filteringModel.filteredPlot.getValue();
	}
	
	public EventfulCache<SpectrumView> getFilteredPlotCache() {
		return filteringModel.filteredPlot;
	}
	
	public Map<Filter, SpectrumView> getFilterDeltas() {
		return filteringModel.filterDeltas.getValue();
	}
	
	public Map<String, SpectrumView> getFilteredOtherPlots() {
		return filteringModel.filteredOtherPlots.getValue();
	}

	public List<SavedPlugin> save() {
		return filteringModel.filters.getFilters().stream().map(Filter::save).toList();
	}

	public void load(List<SavedPlugin> saved) {
		var filters = filteringModel.filters;
		filters.clear();
		for (var s : saved) {
			var optFilter = FilterRegistry.system().fromSaved(s);
			if (optFilter.isPresent()) {
				filters.add(optFilter.get());
			} else {
				PeakabooLog.get().warning("Failed to load plugin '" + s.uuid + "'");
			}
		}
	}

	/**
	 * Ideally this could be monitored by the controller itself, but the
	 * {@link Group} returned by {@link Filter}s is ephemeral and cannot properly
	 * track listeners
	 */
	public void filtersUpdated() {
		plot.history().setUndoPoint("Filter Settings", false);
	}


	
}
