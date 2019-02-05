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
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.mapping.rawmap.RawMap;
import org.peakaboo.mapping.rawmap.RawMapSet;

import cyclops.ISpectrum;
import cyclops.Range;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import plural.Plural;
import plural.executor.ExecutorSet;
import plural.streams.StreamExecutor;

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
	
		DataSet ds = data.getDataSet();
		
		
		return Plural.build("Quick Map", "Generating Map", (execset, exec) -> {
			exec.setWorkUnits(ds.getScanData().scanCount());
						
			Spectrum map = new ISpectrum(ds.getScanData().scanCount());
			int index = 0;
			int count = 0;
			for (ReadOnlySpectrum s : ds.getScanData()) {
				map.set(index++, s.get(channel));
				
				//abort check
				if (execset.isAbortRequested()) {
					execset.aborted();
					break;
				}
				
				//show progress
				count++;
				if (count >= 100) {
					exec.workUnitCompleted(count);
					count = 0;
				}
			}
			
			RawMap rawmap = new RawMap(PeakTable.SYSTEM.get(Element.Fe, TransitionShell.K), map);
			RawMapSet rawmaps = new RawMapSet(Collections.singletonList(rawmap), ds.getScanData().scanCount(), true);
			return rawmaps;
			
		});
		

		
		
		
	}
	
	
}
