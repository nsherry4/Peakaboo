package org.peakaboo.curvefit.peak.search.searcher;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class DerivativePeakSearcher implements PeakSearcher {

	@Override
	public List<Integer> search(SpectrumView data) {

		//smooth it
		Filter filter = new WeightedAverageNoiseFilter();
		filter.initialize();
		SpectrumView filtered = data;
		filtered = filter.filter(filtered);
		filtered = filter.filter(filtered);
		filtered = filter.filter(filtered);
		filtered = filter.filter(filtered);
		filtered = filter.filter(filtered);
		filtered = filter.filter(filtered);

		
		float max = filtered.max();
		float threshold = 0.000001f * max;
		
		//differentiate it
		Spectrum delta = SpectrumCalculations.derivative(filtered);
		
		//bop it
		List<Integer> inflections = new ArrayList<>();
		for (int i = 1; i < data.size(); i++) {
			float prev = delta.get(i-1);
			float next = delta.get(i);
			
			if (prev > 0 && next <= 0 && prev-next > threshold) {				
				inflections.add(i);
			}
		}
		
		//refine it
		filtered = filter.filter(data);
		List<Integer> refined = new ArrayList<>();
		for (int i : inflections) {
			while (true) {
				if (i > 0 && filtered.get(i-1) > filtered.get(i)) {
					i--;
					continue;
				}
				if (i < filtered.size()-1 && filtered.get(i+1) > filtered.get(i)) {
					i++;
					continue;
				}
				break;
			}
			refined.add(i);
		}
		
		
		return refined;
	}

}
