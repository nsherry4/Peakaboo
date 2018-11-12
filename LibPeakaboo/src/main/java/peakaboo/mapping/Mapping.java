package peakaboo.mapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cyclops.Range;
import cyclops.ReadOnlySpectrum;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.dataset.DataSet;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.results.MapResultSet;
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
	 * @return a {@link StreamExecutor} which will return a {@link MapResultSet}
	 */
	public static StreamExecutor<MapResultSet> mapTask(
			DataSet dataset, 
			FilterSet filters, 
			FittingSet fittings, 
			CurveFitter fitter, 
			FittingSolver solver
		) {
		
		List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		MapResultSet maps = new MapResultSet(transitionSeries, dataset.getScanData().scanCount());
		
		//Math.max(1, dataset.getScanData().scanCount())
		StreamExecutor<MapResultSet> streamer = new StreamExecutor<>("Applying Filters & Fittings", 1);
		streamer.setTask(new Range(0, dataset.getScanData().scanCount()-1), stream -> {
			stream.forEach(index -> {
				
				ReadOnlySpectrum data = dataset.getScanData().get(index);
				if (data == null) return;
				
				data = filters.applyFiltersUnsynchronized(data);
				
				FittingResultSet frs = solver.solve(data, fittings, fitter);
				
				for (FittingResult result : frs.getFits()) {
					maps.putIntensityInMapAtPoint(result.getFit().sum(), result.getTransitionSeries(), index);
				}
				
			});
			System.gc();
			return maps;
		}); 
		
		return streamer;
		
	}
	
	public static Map<Element, Float> concentrations(List<TransitionSeries> tss, Function<TransitionSeries, Float> intensityFunction) {

		//find best TransitionSeries per element to measure
		Map<Element, TransitionSeries> elements = new LinkedHashMap<>();
		for (TransitionSeriesType type : new TransitionSeriesType[] {TransitionSeriesType.M, TransitionSeriesType.L, TransitionSeriesType.K}) {
			for (TransitionSeries ts : tss) {
				if (ts.type != type) { continue; }
				elements.put(ts.element, ts);
			}
		}
		
		//calculate calibrated intensities per element and sum total intensity
		float sum = 0;
		Map<Element, Float> intensities = new LinkedHashMap<>();
		for (Element element : elements.keySet()) {
			TransitionSeries ts = elements.get(element);
			float intensity = intensityFunction.apply(ts);
			
			intensities.put(ts.element, intensity);
			sum += intensity;
		}
		
		//TODO: How to handle uncalibrated elements?
		Map<Element, Float> ppm = new LinkedHashMap<>();
		for (Element element : intensities.keySet()) {
			ppm.put(element, intensities.get(element) / sum * 1e6f);
		}
		return ppm;
	}
	
}
