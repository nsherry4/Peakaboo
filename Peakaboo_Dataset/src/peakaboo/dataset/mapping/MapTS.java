package peakaboo.dataset.mapping;

import java.util.List;

import peakaboo.calculations.SpectrumCalculations;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.datatypes.tasks.executor.implementations.TicketingUITaskExecutor;
import peakaboo.fileio.xrf.DataSource;
import peakaboo.filters.FilterSet;
import peakaboo.mapping.results.MapResultSet;


public class MapTS
{

	public static TaskList<MapResultSet> calculateMap(final DataSource dataSource, final FilterSet filters, final FittingSet fittings)
	{

		final TaskList<MapResultSet> tasklist;

		// ======================================================================
		// LOGIC FOR FILTERS AND FITTING
		// Original => Filtered => Fittings => Stored-In-Map
		// ======================================================================
		//final List<List<Double>> filteredData;

		final List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		final MapResultSet maps = new MapResultSet(transitionSeries, dataSource.getExpectedScanCount());
		
		final Task t_filter = new Task("Apply Filters and Fittings") {

			@Override
			public boolean work(int ordinal)
			{

				Spectrum original = dataSource.getScanAtIndex(ordinal);
				
				if (original == null) return true;
				
				Spectrum data = filters.filterDataUnsynchronized(dataSource.getScanAtIndex(ordinal), false);
				//filteredDataSet.set(ordinal, data);
				
				FittingResultSet frs = fittings.calculateFittings(data);
				// fittingResults.set(ordinal, frs);

				for (FittingResult result : frs.fits)
				{
					maps.putIntensityInMapAtPoint(
						SpectrumCalculations.sumValuesInList(result.fit),
						result.transitionSeries,
						ordinal);
				}

				return true;
				

			}

		};


		tasklist = new TaskList<MapResultSet>("Generating Data for Map") {

			@Override
			public MapResultSet doTasks()
			{

				TicketingUITaskExecutor executor;

				// ================================
				// PROCESS FILTERS, FITTINGS
				// ================================
				

					
				// process these scans in parallel
				executor = new TicketingUITaskExecutor(dataSource.getScanCount(), t_filter, this);
				executor.executeBlocking();
				
				if (isAborted()) return null;


				// return intensities;
				// return ListCalculations.subtractFromList(intensities, 0.0, 0.0);
								
				return maps;
			}

		};

		tasklist.addTask(t_filter);

		// tasklist.addTask(t_scanToMaps);

		return tasklist;
	}
	
}
