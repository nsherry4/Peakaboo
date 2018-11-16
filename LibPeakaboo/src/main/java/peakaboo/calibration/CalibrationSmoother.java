package peakaboo.calibration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;
import peakaboo.filter.model.Filter;
import peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;

public class CalibrationSmoother implements CalibrationProcessor {

	@Override
	public void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations) {
		smooth(calibrations, TransitionShell.K);
		smooth(calibrations, TransitionShell.L);
		smooth(calibrations, TransitionShell.M);
	}

	private void smooth(Map<TransitionSeries, Float> calibrations, TransitionShell tst) {
		List<TransitionSeries> tss = calibrations
				.keySet()
				.stream()
				.filter(ts -> ts.getShell() == tst)
				.sorted((a, b) -> Integer.compare(a.getElement().ordinal(), b.getElement().ordinal()))
				.collect(Collectors.toList());
		if (tss.size() < 2) { return; }
		
		Element lowest = tss.get(0).getElement();
		Element highest = tss.get(tss.size()-1).getElement();
		
		//pack - here we use the ordinal values to make sure we don't leave 
		//0s in any missing/masked transition series which will bring down
		//the averaging
		Spectrum values = new ISpectrum(highest.ordinal() - lowest.ordinal() + 1);
		float value = 0;
		for (int ordinal = lowest.ordinal(); ordinal <= highest.ordinal(); ordinal++) {
			TransitionSeries ts = new TransitionSeries(Element.values()[ordinal], tst);
						
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
			int index = ts.getElement().ordinal() - lowest.ordinal();
			calibrations.put(ts, results.get(index));
		}
				
	}
	

}
