package org.peakaboo.filter.plugins.background;

import java.util.Optional;

import org.peakaboo.filter.model.AbstractBackgroundFilter;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class ExponentialComptonBackgroundFilter extends AbstractBackgroundFilter {

	private Parameter<Integer> pAttackStart, pAttackEnd, pDecayStart, pDecayEnd;
	
	@Override
	public String getFilterName() {
		return "Exponential Compton";
	}

	@Override
	public String getFilterDescription() {
		return "Fits the Compton scattering curve between attack-end and decay-start with an exponential tail on either side";
	}

	@Override
	public void initialize() {
		pAttackStart = new Parameter<>("Attack Start", new IntegerSpinnerStyle(), 500, this::validateCompton);
		pAttackEnd = new Parameter<>("Attack End", new IntegerSpinnerStyle(), 800, this::validateCompton);
		pDecayStart = new Parameter<>("Decay Start", new IntegerSpinnerStyle(), 1000, this::validateCompton);
		pDecayEnd = new Parameter<>("Decay End", new IntegerSpinnerStyle(), 1200, this::validateCompton);
		
		this.addParameter(pAttackStart, pAttackEnd, pDecayStart, pDecayEnd);
	}

	private boolean validateCompton(Parameter<?> p) {
		if (pAttackStart.getValue() >= pAttackEnd.getValue()) return false;
		if (pAttackEnd.getValue() >= pDecayStart.getValue()) return false;
		if (pDecayStart.getValue() >= pDecayEnd.getValue()) return false;
		return true;
	}
	
	@Override
	public boolean canFilterSubset() {
		return false;
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
	public String getFilterUUID() {
		return "5f9b89c0-1d6d-4911-93e1-8c073ad0a84f";
	}

	@Override
	protected ReadOnlySpectrum getBackground(ReadOnlySpectrum data, Optional<FilterContext> ctx, int percent) {
		int attackStart = pAttackStart.getValue();
		int attackEnd = pAttackEnd.getValue();
		int decayStart = pDecayStart.getValue();
		int decayEnd = pDecayEnd.getValue();
		
		Spectrum output = new ISpectrum(data);
		for (int i = 0; i < data.size(); i++) {
			//before curve
			if (i < attackStart) { output.set(i, 0); }

			//during attack
			if (i >= attackStart && i <= attackEnd) {
				float max = data.get(attackEnd);
				float dist = (float)(i-attackStart) / (float)(attackEnd-attackStart);
				
				//y = L*e^(g*x) is linear in log-lin
				float value = (float) (max * Math.pow(Math.exp(Math.log(max)), dist));
				//this function always starts w/ a value of 'max', and our plotting is all 
				//done with log1p anyways, so this works out perfectly.
				value = (float) Math.log(value);
				value -= Math.log(max); 
				value = (float) Math.exp(value);
				value -= 1;
				value *= max/(max-1);
				output.set(i, value);
			}
			
			//during sustain
			if (i > attackEnd && i < decayStart) {
				output.set(i, data.get(i));
			}
			
			//during decay
			if (i >= decayStart && i <= decayEnd) {
				float max = data.get(decayStart);
				//decayEnd-i because we want to mirror/flip the curve on the back end 
				float dist = (float)(decayEnd-i) / (float)(decayEnd-decayStart);

				//y = L*e^(g*x) is linear in log-lin
				float value = (float) (max * Math.pow(Math.exp(Math.log(max)), dist));
				//this function always starts w/ a value of 'max', and our plotting is all 
				//done with log1p anyways, so this works out perfectly.
				value = (float) Math.log(value);
				value -= Math.log(max); 
				value = (float) Math.exp(value);
				value -= 1;
				value *= max/(max-1);
				output.set(i, value);
			}
			
			//after curve
			if (i > decayEnd) { output.set(i, 0); }
		}
		
		SpectrumCalculations.multiplyBy_inplace(output, percent/100f);
		
		return output;
		
	}
	
	

}
