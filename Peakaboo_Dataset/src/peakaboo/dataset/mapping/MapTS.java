package peakaboo.dataset.mapping;

import java.util.List;

import fava.signatures.FnEach;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.fileio.DataSource;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * This class contains logic for generating maps for a {@link DataSetProvider}, so that functionality does not have to be duplicated across various implementations
 * @author Nathaniel Sherry, 2010
 *
 */

public class MapTS
{

	/**
	 * Generates a map based on the given inputs. Returns a {@link ExecutorSet} which can execute this task asynchronously and return the result
	 * @param dataSource the {@link DataSource} providing access to data
	 * @param filters the {@link FilterSet} containing all filters needing to be applied to this data
	 * @param fittings the {@link FittingSet} containing all fittings needing to be turned into maps
	 * @param type the way in which a fitting should be mapped to a 2D map. (eg height, area, ...)
	 * @return a {@link ExecutorSet} which will return a {@link MapResultSet}
	 */
	public static ExecutorSet<MapResultSet> calculateMap(final DataSource dataSource, final FilterSet filters, final FittingSet fittings, final FittingTransform type)
	{

		final ExecutorSet<MapResultSet> tasklist;

		// ======================================================================
		// LOGIC FOR FILTERS AND FITTING
		// Original => Filtered => Fittings => Stored-In-Map
		// ======================================================================
		//final List<List<Double>> filteredData;

		final List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		final MapResultSet maps = new MapResultSet(transitionSeries, dataSource.getScanCount());
		
		final FnEach<Integer> t_filter = new FnEach<Integer>() {

			public void f(Integer ordinal)
			{
				
				Spectrum original = dataSource.getScanAtIndex(ordinal);
				
				if (original == null) return;
				
				Spectrum data = filters.filterDataUnsynchronized(new Spectrum(dataSource.getScanAtIndex(ordinal)), false);
				//filteredDataSet.set(ordinal, data);
				
				FittingResultSet frs = fittings.calculateFittings(data);
				// fittingResults.set(ordinal, frs);

				for (FittingResult result : frs.fits)
				{
					maps.putIntensityInMapAtPoint(
						type == FittingTransform.AREA ? SpectrumCalculations.sumValuesInList(result.fit) : SpectrumCalculations.max(result.fit),
						result.transitionSeries,
						ordinal);
				}

				return;
				

			}

		};


		final EachIndexExecutor executor = new PluralEachIndexExecutor(dataSource.getScanCount(), t_filter);;
		
		tasklist = new ExecutorSet<MapResultSet>("Generating Data for Map") {

			@Override
			public MapResultSet doMaps()
			{

				

				// ================================
				// PROCESS FILTERS, FITTINGS
				// ================================
				

					
				// process these scans in parallel
				
				executor.executeBlocking();
				
				if (isAborted()) return null;
							
				return maps;
			}

		};
		
		tasklist.addExecutor(executor, "Apply Filters and Fittings");
		//tasklist.addTask(executor.getPlural(), );
		

		// tasklist.addTask(t_scanToMaps);

		return tasklist;
	}
	
}
