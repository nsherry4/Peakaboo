package org.peakaboo.filter.plugins.noise;

import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class WeightedAverageNoiseFilter extends AbstractFilter {

	private Parameter<Integer> reach;


	public WeightedAverageNoiseFilter() {
		super();
	}
	

	@Override
	public void initialize() {
		reach = new Parameter<>("Averaging Reach (2n+1)", new IntegerStyle(), 4, this::validate);
		addParameter(reach);
	}
	
	@Override
	public String getFilterName() {
		return "Weighted Averaging";
	}



	@Override
	public FilterType getFilterType() {
		return FilterType.NOISE;
	}


	private boolean validate(Parameter<?> p) {
		// has to at least have a 3-point, but cannot exceed a 10*2+1=21-point moving average
		if (reach.getValue() > 10 || reach.getValue() < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription() {
		return "The "
				+ getFilterName()
				+ " filter refines the values of each point in a scan by sampling it and the points around it, and replacing it with an exponentially weighted average of the sampled points. While not as sophisticated as the Savitsky-Golay filter, it does provide more flexibility.";
	}


	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, DataSet dataset) {
		data = weightedMovingAverage(data, reach.getValue());
		return data;
	}

	
	@Override
	public boolean canFilterSubset() {
		return true;
	}


	@Override
	public boolean pluginEnabled() {
		return true;
	}


	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public String pluginUUID() {
		return "24231bee-5442-493b-b95d-1592a7d70bfd";
	}
	
	/**
	 * 
	 * Performs a center-weighted moving average over a data set
	 * 
	 * @param data
	 *            the data to be averaged
	 * @param windowSpan
	 *            the distance from the centrepoint to an edge of the set of numbers being averaged
	 * @return a moving-average smoothed data set
	 */
	public static Spectrum weightedMovingAverage(ReadOnlySpectrum data, int windowSpan) {

		/*
		 * for a windowSpan n, the center-point's weight will be 2^n. 
		 * Other points will be 2^(n-d) where d is distance from center.
		 * This will continue until at the outsides of the window, n=d,
		 * and the weight is 2^0=1
		 */
		
		int size = windowSpan*2+1;
		float[] weights = new float[size];
		for (int i = 0; i < windowSpan; i++) {
			weights[i] = (float) Math.pow(2, i);
			weights[size-1 - i] = (float) Math.pow(2, i);
		}
		weights[windowSpan] = (float) Math.pow(2, windowSpan);
			
				
		Spectrum smoothed = new ISpectrum(data.size());
		
		int start, stop;
		float totalWeight;
		int pos;
		float sum;
		for (int i = 0; i < data.size(); i++) {

			// exact same as in last loop
			start = Math.max(0,  i - windowSpan);
			stop = Math.min(data.size()-1, i + windowSpan);
			
			totalWeight = 0;
			pos = 0;
			sum = 0;
			for (int p = start; p <= stop; p++) {
				totalWeight += weights[pos];
				sum += (data.get(p) * weights[pos]);
				pos++;
			}

			if (totalWeight == 0) {
				smoothed.set(i, 0);
			} else {
				smoothed.set(i, sum / totalWeight);
			}

		}


		return smoothed;

	}
	

}
