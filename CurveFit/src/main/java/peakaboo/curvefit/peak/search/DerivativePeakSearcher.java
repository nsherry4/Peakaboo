package peakaboo.curvefit.peak.search;

import java.util.ArrayList;
import java.util.List;

import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class DerivativePeakSearcher implements PeakSearcher {

	@Override
	public List<Integer> search(ReadOnlySpectrum data) {
		
		//aggressive smoothing to get just the most significant peaks
		ReadOnlySpectrum smoothed = new ISpectrum(data);
		for (int i = 0; i < 2; i++) {
			smoothed = smooth(smoothed, 10);
		}
		
				
		//first and second derivatives.
		Spectrum d1 = SpectrumCalculations.derivative(smoothed);
		Spectrum d2 = SpectrumCalculations.derivative(d1);

		//Any detected peak for which the second derivative doesn't 
		//exceed 0.1% of the max-abs of the second derivative spectrum
		//will be discarted
		float d2threshold = SpectrumCalculations.abs(d2).max() * 0.001f;
		
				
		//First derivative crossing from positive to negative indicates a peak top
		List<Integer> channels = new ArrayList<>();
		int nonnegative = 0;
		for (int i = 1; i < data.size(); i++) {
			float value = d1.get(i);
			
			if (value >= 0) {
				nonnegative++;
			} else  {
				if (nonnegative > 10 && d2.get(i) < -d2threshold) {
					channels.add(i);
				}
				nonnegative = 0;
			}

		}
		
		channels.sort((a, b) -> {
			return Float.compare(data.get(b), data.get(a));
		});
		
		return channels;
		
	}
	
	private Spectrum smooth(ReadOnlySpectrum data, int windowSpan) {
		
		Spectrum smoothed = new ISpectrum(data.size());
		
		int start, stop;
		float sum;
		for (int i = 0; i < data.size(); i++) {

			// exact same as in last loop
			start = Math.max(0,  i - windowSpan);
			stop = Math.min(data.size()-1, i + windowSpan + 1);
			
			sum = 0;
			for (int p = start; p < stop; p++) {
				sum += data.get(p);
			}

			smoothed.set(i, sum);

		}


		return smoothed;
	}
	
}
