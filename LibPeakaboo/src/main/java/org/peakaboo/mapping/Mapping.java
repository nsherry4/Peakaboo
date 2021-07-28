package org.peakaboo.mapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.ROFittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.peak.transition.DummyTransitionSeries;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.FilterContext;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
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
		
		List<ITransitionSeries> transitionSeries = ctx.fittings.getVisibleTransitionSeries();

		
		int mapsize = ctx.dataset.getScanData().scanCount();
		//Handle non-contiguous datasets
		boolean noncontiguous = !ctx.dataset.getDataSource().isRectangular() && ctx.dataset.getDataSource().getDataSize().isPresent();
		Coord<Integer> dimensions = ctx.dataset.getDataSize().getDataDimensions();
		
		GridPerspective<Integer> grid = new GridPerspective<>(dimensions.x, dimensions.y, 0);
		if (noncontiguous) {
			//the dataset is non-contiguous, but provides dimensions and a way to get a coord per index
			mapsize = dimensions.x * dimensions.y;
		}
		RawMapSet maps = new RawMapSet(transitionSeries, mapsize, !noncontiguous);
		
		StreamExecutor<RawMapSet> streamer = new StreamExecutor<>("Applying Filters & Fittings");
		streamer.setTask(new Range(0, ctx.dataset.getScanData().scanCount()-1), stream -> {
			
			long t1 = System.currentTimeMillis();
			
			stream.forEach(index -> {
				
				ReadOnlySpectrum data = ctx.dataset.getScanData().get(index);
				if (data == null) return;
				
				data = filters.applyFiltersUnsynchronized(data, ctx);
				
				FittingResultSet frs = solver.solve(data, ctx.fittings, fitter);
				
				for (FittingResult result : frs.getFits()) {
					if (noncontiguous) {
						int translated = grid.getIndexFromXY(ctx.dataset.getDataSize().getDataCoordinatesAtIndex(index));
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
		
		Spectrum map = new ISpectrum(finalMapsize);
		EachIndexExecutor maptask = new PluralEachIndexExecutor(dataset.getScanData().scanCount(), index -> {
			int translated = index;
			if (noncontiguous) {
				translated = grid.getIndexFromXY(dataset.getDataSize().getDataCoordinatesAtIndex(index));
			}
			map.set(translated, ds.getScanData().get(index).get(channel));
		});
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
