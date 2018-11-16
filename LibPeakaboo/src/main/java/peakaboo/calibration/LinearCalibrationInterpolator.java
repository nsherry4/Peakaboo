package peakaboo.calibration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class LinearCalibrationInterpolator implements CalibrationProcessor {

	@Override
	public void process(CalibrationReference reference, CalibrationProfile profile) {
		interpolate(profile, TransitionShell.K);
		interpolate(profile, TransitionShell.L);
		interpolate(profile, TransitionShell.M);
	}
	
	private void interpolate(CalibrationProfile profile, TransitionShell tst) {
		List<TransitionSeries> knowns = profile.calibrations
				.keySet()
				.stream()
				.filter(ts -> ts.getShell() == tst)
				.sorted((a, b) -> Integer.compare(a.getElement().ordinal(), b.getElement().ordinal()))
				.collect(Collectors.toList());
		if (knowns.size() < 2) { return; }
		
		TransitionSeries previous = null;
		for (TransitionSeries known : knowns) {
			if (previous == null) {
				previous = known;
				continue;
			}
						
			//all missing entries between previous and known
			for (int i = previous.getElement().ordinal()+1; i < known.getElement().ordinal(); i++) {
				System.out.println(Element.values()[i]);
				TransitionSeries inter = new TransitionSeries(Element.values()[i], tst);		
				profile.calibrations.put(inter, interpolate(profile.calibrations, inter, previous, known));
				profile.interpolated.add(inter);
			}
			
			previous = known;
		}
		
		
	}
	
	private float interpolate(Map<TransitionSeries, Float> calibrations, TransitionSeries current, TransitionSeries previous, TransitionSeries next) {
				
		float pv = calibrations.get(previous);
		float nv = calibrations.get(next);
		float delta = nv - pv;
		
		float po = previous.getElement().ordinal();
		float no = next.getElement().ordinal();
		float co = current.getElement().ordinal();
		
		float percent = curve((co-po) / (no-po));
		float value = pv + (delta * percent);
		
		return value;
		
		
	}

	protected float curve(float percent) {
		return percent;
	}
	
	@Override
	public void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations) {
		//Not Used
	}
	
	
}
