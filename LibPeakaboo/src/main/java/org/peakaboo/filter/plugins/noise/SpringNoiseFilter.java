package org.peakaboo.filter.plugins.noise;


import java.util.Optional;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterContext;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.filter.plugins.mathematical.DerivativeMathFilter;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * 
 * This class is a filter exposing the Moving Average functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry
 */


public final class SpringNoiseFilter extends AbstractFilter {

	private Parameter<Integer> iterations;
	private Parameter<Float> multiplier;
	private Parameter<Float> falloff;

	public SpringNoiseFilter() {
		super();
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize() {
		iterations = new Parameter<>("Iterations", new IntegerStyle(), 20, this::validate);
		multiplier = new Parameter<>("Linear Force Multiplier", new RealStyle(), 20.0f, this::validate);
		falloff = new Parameter<>("Exponential Force Falloff Rate", new RealStyle(), 2.0f, this::validate);
		
		addParameter(iterations, multiplier, falloff);
		
	}


	@Override
	public String getFilterName() {
		return "Spring Smoothing";
	}



	@Override
	public FilterDescriptor getFilterDescriptor() {
		return FilterDescriptor.SMOOTHING;
	}


	private boolean validate(Parameter<?> p) {

		float mult, power;
		int iters;

		
		mult = multiplier.getValue();
		if (mult > 100 || mult < 0.1) return false;
		
		power = falloff.getValue();
		if (power > 10 || power <= 0.0) return false;
		
		iters = iterations.getValue();
		if (iters > 50 || iters < 1) return false;
		

		return true;
	}


	@Override
	public String getFilterDescription() {
		return "The "
				+ getFilterName()
				+ " filter operates on the assumption that weak signal should be smoothed more than strong signal. It treats each adjacent pair of points as if they were connected by a spring. With each iteration, a tension force draws neighbouring points closer together. The Force Multiplier controls how strongly a pair of elements are pulled together, and the Force Falloff Rate controls how aggressively stronger signal is anchored in place, unmoved by spring forces. This prevents peak shapes from being distorted by the smoothing algorithm.";
	}


	@Override
	public ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx) {
		data = springFilter(
				data, 
				multiplier.getValue().floatValue(), 
				falloff.getValue().floatValue(), 
				iterations.getValue()
			);
		return data;
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	
	@Override
	public String pluginUUID() {
		return "e4a0065a-d42d-4a69-b021-b3df051d292d";
	}
	
	@Override
	public boolean canFilterSubset() {
		return true;
	}

	

	/**
	 * The Spring filter is designed to smooth weaker data while preserving the structure of stronger signals.
	 * The Spring filter Filter operates on the assumption that weak signal should be smoothed more than strong signal.
	 * It treats each pair of points as if they were connected by a spring. With each iteration, a tension force draws
	 * neighbouring points closer together. The Force Multiplier controls how strongly the two elements are pulled 
	 * together, and the Force Falloff Rate controls how aggressively stronger signal is anchored in place, unmoved 
	 * by spring forces. This prevents peaks from being distorted by the smoothing algorithm.
	 * @param data the {@link Spectrum} to smooth
	 * @param forceMultiplier the linear force multiplier value
	 * @param falloffExp the exponential force falloff value
	 * @param iterations the number of iterations to perform the smoothing
	 * @return the smoothed data
	 */
	public static Spectrum springFilter(ReadOnlySpectrum data, float forceMultiplier, float falloffExp, int iterations) {
		Spectrum result = new ISpectrum(data);
		
		for (int i = 0; i < iterations; i++)
		{
			springFilterIteration(result, forceMultiplier, falloffExp);
		}
		
		return result;

	}
	
	private static void springFilterIteration(Spectrum data, float forceMultiplier, float falloffExp) {

	
		float[] deltas = DerivativeMathFilter.deriv(data).backingArray();
		//Work with the float array directly
		float[] array = data.backingArray();
		int size = array.length;
		
		//calculate the forces for each point
		//forces represent how much pull a points neighbours are exerting on it.
		//the further away its neighbours are, the more the "spring" has streched, and
		//the stronger the force will be.
		//Then, we want to make sure that peaks aren't distorted, so we reduce the force
		//as the signal gets stronger. This fits with the assumption that weaker signal will be
		//noisier.
		float dist, force, delta=0, lastdelta=0;
		for (int i = 0; i < size; i++)
		{
			
			delta = deltas[i];
			
			if (i == 0) 			force = -delta * 0.5f; //first
			else if (i == size - 1) force = delta * 0.5f; //last
			else 					force = (lastdelta + (-delta)) * 0.25f; //middle 

			lastdelta = delta;
			
			
			//if dist dips below 0 and we use a falloff exp like 1.6, we'd like to get a sensible answer, rather than NaN
			dist = Math.abs(array[i]);
			dist = (float) Math.pow(dist, falloffExp);
			if (dist < 1) dist = 1f;
			
			if (force < 0) {
				force = Math.max(force,  (force / dist) * forceMultiplier);
			} else {
				force = Math.min(force, (force / dist) * forceMultiplier);
			}
			
			array[i] = array[i] - force;
			
		}
		
	}

	
}
