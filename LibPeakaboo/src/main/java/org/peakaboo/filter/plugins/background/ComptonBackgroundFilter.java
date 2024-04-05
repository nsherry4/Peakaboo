package org.peakaboo.filter.plugins.background;

import java.util.Optional;

import org.peakaboo.filter.model.AbstractBackgroundFilter;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class ComptonBackgroundFilter extends AbstractBackgroundFilter {

	private Parameter<Integer> pBgStart, pBgEnd, pBlurStrength;
	
	@Override
	public String getFilterName() {
		return "Custom Compton";
	}

	@Override
	public String getFilterDescription() {
		return "Fits the Compton scattering curve between start and end channels";
	}

	@Override
	public void initialize() {
		pBgStart = new Parameter<>("Background Start", new IntegerSpinnerStyle(), 800, this::validateCompton);
		pBgEnd = new Parameter<>("Background End", new IntegerSpinnerStyle(), 1200, this::validateCompton);
		pBlurStrength = new Parameter<>("Blur Strength", new IntegerSpinnerStyle(), 5, this::validateCompton);
		
		percent.setValue(30);
		
		this.addParameter(pBgStart, pBgEnd, pBlurStrength);
	}

	private boolean validateCompton(Parameter<?> p) {
		if (pBgStart.getValue() >= pBgEnd.getValue()) return false;
		if (pBlurStrength.getValue() > 10) return false;
		if (pBlurStrength.getValue() < 1) return false;
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
		return "65b8181a-f3da-4109-9abb-89eb846ffac4";
	}

	@Override
	protected SpectrumView getBackground(SpectrumView data, Optional<FilterContext> ctx, int percent) {
		int bgStart = pBgStart.getValue();
		int bgEnd = pBgEnd.getValue();
		
		Spectrum output = new ArraySpectrum(data.size());
		for (int i = 0; i < data.size(); i++) {
			
			//during sustain
			if (i > bgStart && i < bgEnd) {
				output.set(i, data.get(i));
			}
			
		}
		
		int blurStrength = pBlurStrength.getValue();
		
		int kernelSize = 50 + blurStrength * 10;
		float[] blurKernel = SpectrumCalculations.kernel_gaussian(kernelSize, kernelSize / 4f, 5 + blurStrength);
		output = SpectrumCalculations.convolve_repeated(output, blurKernel, 3);
		
		SpectrumCalculations.multiplyBy_inplace(output, percent/100f);
		
		return output;
		
	}
	
	

}
