package peakaboo.dataset.mapping;

import java.util.List;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.fileio.DataSource;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.workers.PluralEachIndex;
import plural.workers.PluralSet;
import plural.workers.executor.eachindex.implementations.PluralUIEachIndexExecutor;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class MapTS
{

	public static PluralSet<MapResultSet> calculateMap(final DataSource dataSource, final FilterSet filters, final FittingSet fittings, final FittingTransform type)
	{

		final PluralSet<MapResultSet> tasklist;

		// ======================================================================
		// LOGIC FOR FILTERS AND FITTING
		// Original => Filtered => Fittings => Stored-In-Map
		// ======================================================================
		//final List<List<Double>> filteredData;

		final List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		final MapResultSet maps = new MapResultSet(transitionSeries, dataSource.getExpectedScanCount());
		
		final PluralEachIndex t_filter = new PluralEachIndex("Apply Filters and Fittings") {

			public void f(Integer ordinal)
			{
				
				Spectrum original = dataSource.getScanAtIndex(ordinal);
				
				if (original == null) return;
				
				Spectrum data = filters.filterDataUnsynchronized(dataSource.getScanAtIndex(ordinal), false);
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


		tasklist = new PluralSet<MapResultSet>("Generating Data for Map") {

			@Override
			public MapResultSet doMaps()
			{

				PluralUIEachIndexExecutor executor;

				// ================================
				// PROCESS FILTERS, FITTINGS
				// ================================
				

					
				// process these scans in parallel
				executor = new PluralUIEachIndexExecutor(dataSource.getScanCount(), t_filter, this);
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
