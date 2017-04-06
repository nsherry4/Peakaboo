package peakaboo.mapping;

import java.util.List;
import java.util.function.Consumer;

import peakaboo.curvefit.model.FittingResult;
import peakaboo.curvefit.model.FittingResultSet;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.dataset.AbstractDataSet;
import peakaboo.dataset.DataSet;
import peakaboo.dataset.StandardDataSet;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * This class contains logic for generating maps for a {@link AbstractDataSet}, so that functionality does not have to be duplicated across various implementations
 * @author Nathaniel Sherry, 2010
 *
 */

public class MapTS
{

	/**
	 * Generates a map based on the given inputs. Returns a {@link ExecutorSet} which can execute this task asynchronously and return the result
	 * @param datasetProvider the {@link StandardDataSet} providing access to data
	 * @param filters the {@link FilterSet} containing all filters needing to be applied to this data
	 * @param fittings the {@link FittingSet} containing all fittings needing to be turned into maps
	 * @param type the way in which a fitting should be mapped to a 2D map. (eg height, area, ...)
	 * @return a {@link ExecutorSet} which will return a {@link MapResultSet}
	 */
	public static ExecutorSet<MapResultSet> calculateMap(final DataSet datasetProvider, final FilterSet filters, final FittingSet fittings, final FittingTransform type)
	{

		final ExecutorSet<MapResultSet> tasklist;

		// ======================================================================
		// LOGIC FOR FILTERS AND FITTING
		// Original => Filtered => Fittings => Stored-In-Map
		// ======================================================================
		//final List<List<Double>> filteredData;

		final List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		final MapResultSet maps = new MapResultSet(transitionSeries, datasetProvider.size());
		
		final Consumer<Integer> t_filter = ordinal -> {
			
			Spectrum original = datasetProvider.getScanData().get(ordinal); 
			if (original == null) return;
			
			Spectrum data = filters.filterDataUnsynchronized(new Spectrum(datasetProvider.getScanData().get(ordinal)), false);
			FittingResultSet frs = fittings.calculateFittings(data);

			for (FittingResult result : frs.fits)
			{
				maps.putIntensityInMapAtPoint(
					type == FittingTransform.AREA ? SpectrumCalculations.sumValuesInList(result.fit) : SpectrumCalculations.max(result.fit),
					result.transitionSeries,
					ordinal);
			}

			return;

		};


		final EachIndexExecutor executor = new PluralEachIndexExecutor(datasetProvider.size(), t_filter);

		tasklist = new ExecutorSet<MapResultSet>("Generating Data for Map") {

			@Override
			public MapResultSet execute()
			{
				// process these scans in parallel
				executor.executeBlocking();
				if (isAborted()) return null;	
				return maps;
			}

		};
		
		tasklist.addExecutor(executor, "Apply Filters and Fittings");


		return tasklist;
	}
	
}
