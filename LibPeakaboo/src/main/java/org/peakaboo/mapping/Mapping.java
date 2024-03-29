package org.peakaboo.mapping;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Settings;
import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver.FittingSolverContext;
import org.peakaboo.curvefit.peak.transition.DummyTransitionSeries;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.Filter.FilterContext;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.executor.eachindex.EachIndexExecutor;
import org.peakaboo.framework.plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.mapping.rawmap.RawMap;
import org.peakaboo.mapping.rawmap.RawMapSet;

/**
 * This class contains logic for generating maps for a {@link AbstractDataSet}, so that functionality does not have to be duplicated across various implementations
 * @author Nathaniel Sherry, 2010
 *
 */

public class Mapping {
	
	private Mapping() {
		//Not Constructable
	}

	/**
	 * Generates a map based on the given inputs. Returns a {@link StreamExecutor} which can execute this task asynchronously and return the result
	 * @param dataset the {@link DataSet} providing access to data
	 * @param filters the {@link FilterSet} containing all filters needing to be applied to this data
	 * @param fittings the {@link FittingSet} containing all fittings needing to be turned into maps
	 * @param type the way in which a fitting should be mapped to a 2D map. (eg height, area, ...)
	 * @return a {@link StreamExecutor} which will return a {@link RawMapSet}
	 */
	public static StreamExecutor<RawMapSet> mapTask(
			FilterSet filters, 
			CurveFitter fitter, 
			FittingSolver solver,
			FilterContext ctx
		) {

		DataSet dataset = ctx.dataset();
		FittingSetView fittings = ctx.fittings();
		
		List<ITransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();

		

		
		int mapsize = dataset.getScanData().scanCount();
		//Handle non-contiguous datasets
		boolean noncontiguous = !dataset.getDataSource().isRectangular() && dataset.getDataSource().getDataSize().isPresent();
		Coord<Integer> dimensions = dataset.getDataSize().getDataDimensions();
		
		GridPerspective<Integer> grid = new GridPerspective<>(dimensions.x, dimensions.y, 0);
		if (noncontiguous) {
			//the dataset is non-contiguous, but provides dimensions and a way to get a coord per index
			mapsize = dimensions.x * dimensions.y;
		}
		RawMapSet maps = new RawMapSet(transitionSeries, mapsize, !noncontiguous);
		
		// Update on progress every 1%, but no more frequently than every 10 scans
		int count = dataset.getScanData().scanCount();
		int interval = (int)Math.max(10, Math.ceil(count / 100f));

		StreamExecutor<RawMapSet> streamer = new StreamExecutor<>("Applying Filters & Fittings", interval);
		streamer.setTask(new Range(0, dataset.getScanData().scanCount()), stream -> {
			
			long t1 = System.currentTimeMillis();
			
			stream.forEach(index -> {
				
				SpectrumView data = dataset.getScanData().get(index);
				if (data == null) return;
				
				data = filters.applyFiltersUnsynchronized(data, ctx);
				
				FittingResultSetView frs = solver.solve(new FittingSolverContext(data, fittings, fitter));
				
				for (FittingResultView result : frs.getFits()) {
					if (noncontiguous) {
						int translated = grid.getIndexFromXY(dataset.getDataSize().getDataCoordinatesAtIndex(index));
						maps.putIntensityInMapAtPoint(result.getFitSum(), result.getTransitionSeries(), translated);	
					} else {
						maps.putIntensityInMapAtPoint(result.getFitSum(), result.getTransitionSeries(), index);
					}
				}
				
			});
			
			long t2 = System.currentTimeMillis();
			PeakabooLog.get().log(Level.INFO, "Generated Maps in " + ((t2-t1)/1000)  + " seconds");
			
			System.gc();
			return maps;
		}); 
		
		return streamer;
		
	}
	

	public static ExecutorSet<RawMapSet> quickMapTask(DataController data, int channel) {
		

		
		//worker task
		DataSet ds = data.getDataSet();
		
		DataSet dataset = data.getDataSet();
		int mapsize = dataset.getScanData().scanCount();
		boolean noncontiguous = !dataset.getDataSource().isRectangular() && dataset.getDataSource().getDataSize().isPresent();
		Coord<Integer> dimensions = dataset.getDataSize().getDataDimensions();
		GridPerspective<Integer> grid = new GridPerspective<>(dimensions.x, dimensions.y, 0);
		if (noncontiguous) {
			//the dataset is non-contiguous, but provides dimensions and a way to get a coord per index
			mapsize = dimensions.x * dimensions.y;
		}
		int finalMapsize = mapsize;
		
		Spectrum map = new ArraySpectrum(finalMapsize);
		EachIndexExecutor maptask = new PluralEachIndexExecutor(dataset.getScanData().scanCount(), index -> {
			int translated = index;
			if (noncontiguous) {
				translated = grid.getIndexFromXY(dataset.getDataSize().getDataCoordinatesAtIndex(index));
			}
			map.set(translated, ds.getScanData().get(index).get(channel));
		}, Settings.getThreadCount());
		maptask.setName("Examining Spectra");
		
		
		//timer
		Mutable<Long> t1 = new Mutable<>();
		Mutable<Long> t2 = new Mutable<>();
		Runnable timerPre = () -> t1.set(System.currentTimeMillis());
		Runnable timerPost = () -> {
			t2.set(System.currentTimeMillis());
			long seconds = (t2.get() - t1.get()) / 1000;
			PeakabooLog.get().log(Level.INFO, "Generated a QuickMap in " + seconds + " seconds");
		};
		
		
		//executor
		return Plural.build("Generating Quick Map", maptask, timerPre, v -> {
			timerPost.run();
			
			//build the RawMapSet now that the map Spectrum has been populated
			RawMap rawmap = new RawMap(new DummyTransitionSeries("Channel " + channel), map);
			return new RawMapSet(
					Collections.singletonList(rawmap), 
					finalMapsize, 
					data.getDataSet().getDataSource().isRectangular(), 
					true
				);
		});

		
	}
	
	
}
