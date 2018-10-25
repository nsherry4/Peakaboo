package peakaboo.calibration.processor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.filter.model.Filter;
import peakaboo.filter.plugins.noise.SavitskyGolayNoiseFilter;
import peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;

public class CalibrationSmoother implements CalibrationProcessor {

	@Override
	public void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations) {
		smooth(calibrations, TransitionSeriesType.K);
		smooth(calibrations, TransitionSeriesType.L);
		smooth(calibrations, TransitionSeriesType.M);
	}

	private void smooth(Map<TransitionSeries, Float> calibrations, TransitionSeriesType tst) {
		List<TransitionSeries> tss = calibrations
				.keySet()
				.stream()
				.filter(ts -> ts.type == tst)
				.sorted((a, b) -> Integer.compare(a.element.ordinal(), b.element.ordinal()))
				.collect(Collectors.toList());
		if (tss.size() < 2) { return; }
		
		Element lowest = tss.get(0).element;
		Element highest = tss.get(tss.size()-1).element;
		
		//pack - here we use the ordinal values to make sure we don't leave 
		//0s in any missing/masked transition series which will bring down
		//the averaging
		Spectrum values = new ISpectrum(highest.ordinal() - lowest.ordinal() + 1);
		float value = 0;
		for (int ordinal = lowest.ordinal(); ordinal <= highest.ordinal(); ordinal++) {
			TransitionSeries ts = PeakTable.SYSTEM.get(Element.values()[ordinal], tst);
			
			//If we can't find it in the peak table, create it.
			//This is generally a bad idea, but we won't ever be using these 
			//dummy TransitionSeries for any real work
			if (ts == null) {
				ts = new TransitionSeries(Element.values()[ordinal], tst);
			}
			
			if (ts != null && calibrations.containsKey(ts)) {
				value = calibrations.get(ts);
			} else {
				//Nothing, value equals the last value
			}
			int index = ordinal - lowest.ordinal();
			values.set(index, value);
		}
		
		//filter
		Filter filter = new WeightedAverageNoiseFilter();
		filter.initialize();
		ReadOnlySpectrum results = filter.filter(values, false);
		
		
		//unpack
		for (TransitionSeries ts : tss) {
			int index = ts.element.ordinal() - lowest.ordinal();
			calibrations.put(ts, results.get(index));
		}
				
	}
	

}
