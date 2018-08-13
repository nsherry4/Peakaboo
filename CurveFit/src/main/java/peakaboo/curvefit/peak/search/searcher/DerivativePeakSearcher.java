package peakaboo.curvefit.peak.search.searcher;

import java.util.ArrayList;
import java.util.List;

import net.sciencestudio.autodialog.model.Value;
import peakaboo.filter.model.Filter;
import peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class DerivativePeakSearcher implements PeakSearcher {

	@Override
	public List<Integer> search(ReadOnlySpectrum data) {
		
		Filter filter = new WeightedAverageNoiseFilter();
		filter.initialize();
		Value<Integer> width = (Value<Integer>) filter.getParameters().get(0);
		width.setValue(new Integer(10));
		
		//aggressive smoothing to get just the most significant peaks
		ReadOnlySpectrum smoothed = new ISpectrum(data);
		
		for (int i = 0; i < 3; i++) {
			smoothed = filter.filter(smoothed, false);
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
		int negative = 0;
		boolean gap = false;
		int bestChannel = 0;
		float bestValue = 0;
		
		
		for (int i = 0; i < data.size(); i++) {
			float value = d2.get(i);
			
			if (value >= 0) {
				
				//If we had been tracking a possible peak, and it looks good
				if (gap && negative >= 10 && bestValue <= -d2threshold) {
					channels.add(bestChannel);
					gap = false;
				}
				
				nonnegative++;
				//Clear any negative-related values
				negative = 0;
				bestChannel = 0;
				bestValue = 0;
				
				//If we've gone 10 channels without a negative value, mark as having had a 
				//good sized gap between peaks
				if (nonnegative >= 5) {
					gap = true;
				}
				
			} else  {
				negative++;
				nonnegative = 0;

				//Start tracking the best point incase this turns out to be good
				if (d2.get(i) < bestValue) {
					bestValue = d2.get(i);
					bestChannel = i;
				}
				
			}
			
		}
		
		//System.exit(0);
		
		channels.sort((a, b) -> {
			return Float.compare(-d2.get(b), -d2.get(a));
		});
		
		return channels;
		
	}
	
	
}
