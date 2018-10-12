package peakaboo.mapping.calibration.processor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.mapping.calibration.CalibrationReference;

public class LinearCalibrationInterpolator implements CalibrationProcessor {

	@Override
	public void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations) {
		interpolate(calibrations, TransitionSeriesType.K);
		interpolate(calibrations, TransitionSeriesType.L);
		interpolate(calibrations, TransitionSeriesType.M);
	}
	
	private void interpolate(Map<TransitionSeries, Float> calibrations, TransitionSeriesType tst) {
		List<TransitionSeries> knowns = calibrations
				.keySet()
				.stream()
				.filter(ts -> ts.type == tst)
				.sorted((a, b) -> Integer.compare(a.element.ordinal(), b.element.ordinal()))
				.collect(Collectors.toList());
		if (knowns.size() < 2) { return; }
		
		TransitionSeries previous = null;
		for (TransitionSeries known : knowns) {
			if (previous == null) {
				previous = known;
				continue;
			}
						
			//all missing entries between previous and known
			for (int i = previous.element.ordinal()+1; i < known.element.ordinal(); i++) {
				TransitionSeries inter = PeakTable.SYSTEM.get(Element.values()[i], tst);

				//If we can't find it in the peak table, create it.
				//This is generally a bad idea, but we won't ever be using these 
				//dummy TransitionSeries for any real work
				if (inter == null) {
					inter = new TransitionSeries(Element.values()[i], tst);
				}
				
				System.out.println(inter.element.name() + " between " + previous.element.name() + " and " + known.element.name());
				System.out.println(inter.element.atomicNumber());
				System.out.println(interpolate(calibrations, inter, previous, known));
				System.out.println("------------------");
				
				calibrations.put(inter, interpolate(calibrations, inter, previous, known));
			}
			
			previous = known;
		}
		
		
	}
	
	private float interpolate(Map<TransitionSeries, Float> calibrations, TransitionSeries current, TransitionSeries previous, TransitionSeries next) {
				
		float pv = calibrations.get(previous);
		float nv = calibrations.get(next);
		
		float po = previous.element.ordinal();
		float no = next.element.ordinal();
		float co = current.element.ordinal();
		
		float percent = (co-po) / (no-po);
		float value = pv + ((nv - pv) * percent);
		
		return value;
		
		
	}
	
	
}
