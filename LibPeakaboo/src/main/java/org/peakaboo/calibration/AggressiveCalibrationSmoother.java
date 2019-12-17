package org.peakaboo.calibration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;

/*
 * Smooths a profile very aggressively. This is useful for smoothing 
 * interpolated values to make sure they're representative.
 */
public class AggressiveCalibrationSmoother implements CalibrationProcessor {

	private int passes = 1;
	public AggressiveCalibrationSmoother(int passes) {
		this.passes = passes;
	}
	
	@Override
	public void process(CalibrationReference reference, Map<ITransitionSeries, Float> calibrations) {
		smooth(calibrations, TransitionShell.K);
		smooth(calibrations, TransitionShell.L);
		smooth(calibrations, TransitionShell.M);
	}

	private void smooth(Map<ITransitionSeries, Float> calibrations, TransitionShell tst) {
		List<ITransitionSeries> tss = calibrations
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
			ITransitionSeries ts = new PrimaryTransitionSeries(Element.values()[ordinal], tst);

			//Update the value with the current TS or carry forward the last one
			if (calibrations.containsKey(ts)) {
				value = calibrations.get(ts);
			}
			
			int index = ordinal - lowest.ordinal();
			values.set(index, value);
		}
		
		//filter
		Filter filter = new WeightedAverageNoiseFilter();
		filter.initialize();
		ReadOnlySpectrum results = values;
		for (int i = 0; i < this.passes; i++) {
			results = filter.filter(results);
		}
		
		
		//unpack
		for (ITransitionSeries ts : tss) {
			int index = ts.getElement().ordinal() - lowest.ordinal();
			calibrations.put(ts, results.get(index));
		}
				
	}
	

}
