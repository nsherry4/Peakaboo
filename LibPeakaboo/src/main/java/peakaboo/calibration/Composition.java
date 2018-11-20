package peakaboo.calibration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class Composition {

	private Map<Element, Float> concentrations;
	private NumberFormat format = new DecimalFormat("0.0");;
	private CalibrationProfile profile;
	
	public Composition(Map<Element, Float> concentrations, CalibrationProfile profile) {
		this.concentrations = concentrations;
		this.profile = profile;
	}
	
	public List<Element> elementsByZ() {
		List<Element> sorted = new ArrayList<>(concentrations.keySet());
		sorted.sort((e1, e2) -> Integer.compare(e1.atomicNumber(), e2.atomicNumber()));
		return sorted;
	}
	
	public List<Element> elementsByConcentration() {
		List<Element> sorted = new ArrayList<>(concentrations.keySet());
		sorted.sort((e1, e2) -> -Float.compare(get(e1), get(e2)));
		return sorted;
	}
	
	public float get(Element e) {
		if (concentrations.containsKey(e)) {
			return concentrations.get(e);
		}
		return 0f;
	}
	
	public String getPercent(Element e) {
		float ppm = get(e);
		return format.format(ppm/10000) + "%";
	}
	
	public boolean contains(Element e) {
		return concentrations.containsKey(e);
	}
	
	
	
	public CalibrationProfile getProfile() {
		return profile;
	}

	public static Composition calculate(List<ITransitionSeries> tss, CalibrationProfile profile, Function<ITransitionSeries, Float> intensityFunction) {

		//find best TransitionSeries per element to measure
		Map<Element, ITransitionSeries> elements = new LinkedHashMap<>();
		for (TransitionShell type : new TransitionShell[] {TransitionShell.M, TransitionShell.L, TransitionShell.K}) {
			for (ITransitionSeries ts : tss) {
				if (ts.getShell() != type) { continue; }
				elements.put(ts.getElement(), ts);
			}
		}
		
		//calculate calibrated intensities per element and sum total intensity
		float sum = 0;
		Map<Element, Float> intensities = new LinkedHashMap<>();
		for (Element element : elements.keySet()) {
			ITransitionSeries ts = elements.get(element);
			float intensity = intensityFunction.apply(ts);
			
			intensities.put(ts.getElement(), intensity);
			sum += intensity;
		}
		
		//TODO: How to handle uncalibrated elements?
		Map<Element, Float> ppm = new LinkedHashMap<>();
		List<Element> sorted = new ArrayList<>(intensities.keySet());
		sorted.sort((e1, e2) -> Integer.compare(e1.atomicNumber(), e2.atomicNumber()));
		
		for (Element element : sorted) {
			ppm.put(element, intensities.get(element) / sum * 1e6f);
		}
		return new Composition(ppm, profile);
	}
	
}
