package org.peakaboo.mapping;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.plotter.data.DataController;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.transition.DummyTransitionSeries;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.mapping.rawmap.RawMap;
import org.peakaboo.mapping.rawmap.RawMapSet;

import cyclops.ISpectrum;
import cyclops.Range;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.util.Mutable;
import plural.Plural;
import plural.executor.ExecutorSet;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.streams.StreamExecutor;
import plural.streams.StreamExecutorSet;

/**
 * This class contains logic for generating maps for a {@link AbstractDataSet}, so that functionality does not have to be duplicated across various implementations
 * @author Nathaniel Sherry, 2010
 *
 */

public class Mapping
{

	/**
	 * Generates a map based on the given inputs. Returns a {@link StreamExecutor} which can execute this task asynchronously and return the result
	 * @param dataset the {@link DataSet} providing access to data
	 * @param filters the {@link FilterSet} containing all filters needing to be applied to this data
	 * @param fittings the {@link FittingSet} containing all fittings needing to be turned into maps
	 * @param type the way in which a fitting should be mapped to a 2D map. (eg height, area, ...)
	 * @return a {@link StreamExecutor} which will return a {@link RawMapSet}
	 */
	public static StreamExecutor<RawMapSet> mapTask(
			DataSet dataset, 
			FilterSet filters, 
			FittingSet fittings, 
			CurveFitter fitter, 
			FittingSolver solver
		) {
		
		List<ITransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		RawMapSet maps = new RawMapSet(transitionSeries, dataset.getScanData().scanCount());
		
		//Math.max(1, dataset.getScanData().scanCount())
		StreamExecutor<RawMapSet> streamer = new StreamExecutor<>("Applying Filters & Fittings");
		streamer.setTask(new Range(0, dataset.getScanData().scanCount()-1), stream -> {
			
			long t1 = System.currentTimeMillis();
			
			stream.forEach(index -> {
				
				ReadOnlySpectrum data = dataset.getScanData().get(index);
				if (data == null) return;
				
				data = filters.applyFiltersUnsynchronized(data);
				
				FittingResultSet frs = solver.solve(data, fittings, fitter);
				
				for (FittingResult result : frs.getFits()) {
					maps.putIntensityInMapAtPoint(result.getFitSum(), result.getTransitionSeries(), index);
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
		int scancount = ds.getScanData().scanCount();
		Spectrum map = new ISpectrum(scancount);
		EachIndexExecutor maptask = new PluralEachIndexExecutor(scancount, index -> {
			map.set(index, ds.getScanData().get(index).get(channel));
		});
		maptask.setName("Examining Spectra");
		
		
		//timer
		Mutable<Long> t1 = new Mutable<>();
		Mutable<Long> t2 = new Mutable<>();
		Runnable timer_pre = () -> {
			//pre-task
			t1.set(System.currentTimeMillis());
		};
		Runnable timer_post = () -> {
			t2.set(System.currentTimeMillis());
			long seconds = (t2.get() - t1.get()) / 1000;
			PeakabooLog.get().log(Level.INFO, "Generated a QuickMap in " + seconds + " seconds");
		};
		
		
		//executor
		return Plural.build("Generating Quick Map", maptask, timer_pre, (v) -> {
			timer_post.run();
			
			//build the RawMapSet now that the map Spectrum has been populated
			RawMap rawmap = new RawMap(new DummyTransitionSeries("Channel " + channel), map);
			RawMapSet rawmaps = new RawMapSet(Collections.singletonList(rawmap), ds.getScanData().scanCount(), true);
			return rawmaps;
		});

		
	}
	
	
}
