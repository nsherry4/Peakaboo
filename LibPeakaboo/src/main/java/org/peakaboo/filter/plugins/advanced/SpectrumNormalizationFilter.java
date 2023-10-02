package org.peakaboo.filter.plugins.advanced;

import java.util.Optional;

import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterContext;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.filter.plugins.noise.SavitskyGolayNoiseFilter;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.model.classinfo.EnumClassInfo;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.autodialog.model.style.editors.ListStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.autodialog.model.style.layouts.FramedLayoutStyle;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;


public class SpectrumNormalizationFilter extends AbstractFilter {
	
	public enum SmoothingIntensity {
		Off, Low, Medium, High
	}
	
	private Group gChannelRange;
	private Parameter<Integer> pStartChannel;
	private Parameter<Integer> pEndChannel;
	
	private Parameter<Float> pHeight;
	private SelectionParameter<String> pMode;
	private SelectionParameter<SmoothingIntensity> pSmooth;
	
	private Group gElement;
	private SelectionParameter<Element> pElement;
	private SelectionParameter<TransitionShell> pShell;
	
	
	private static final String MODE_RANGE = "Channel Range";
	private static final String MODE_MAX = "Strongest Channel";
	private static final String MODE_SUM = "All Channels";
	private static final String MODE_FIT = "Element";

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize() {
		
		pMode = new SelectionParameter<>("Mode", new ListStyle<String>(), MODE_FIT, this::validate);
		pMode.setPossibleValues(MODE_FIT, MODE_RANGE, MODE_MAX, MODE_SUM);
		addParameter(pMode);

		pHeight = new Parameter<>("Normalized Intensity", new RealStyle(), 10f, this::validate);
		addParameter(pHeight);
		
		pSmooth = new SelectionParameter<>("Noise Reduction", new ListStyle<>(), SmoothingIntensity.Low, new EnumClassInfo<>(SmoothingIntensity.class), this::validate);
		pSmooth.setPossibleValues(SmoothingIntensity.values());
		addParameter(pSmooth);
		
		pStartChannel = new Parameter<>("Start Channel", new IntegerStyle(), 1, this::validate);
		pEndChannel = new Parameter<>("End Channel", new IntegerStyle(), 10, this::validate);
		gChannelRange = new Group("Channel Range", new FramedLayoutStyle(true), pStartChannel, pEndChannel);
		addParameter(gChannelRange);
		
		
		pElement = new SelectionParameter<>("Fiting Element", new ListStyle<>(), Element.Ar, new EnumClassInfo<>(Element.class), this::validate);
		pElement.setPossibleValues(Element.values());
		pElement.setEnabled(false);
		
		pShell = new SelectionParameter<>("Fitting Shell", new ListStyle<>(), TransitionShell.K, new EnumClassInfo<>(TransitionShell.class), this::validate);
		pShell.setPossibleValues(TransitionShell.K, TransitionShell.L, TransitionShell.M);
		pShell.setEnabled(false);

		gElement = new Group("Element Selection", new FramedLayoutStyle(true), pElement, pShell);
		addParameter(gElement);
		
		validate(null);
	}
	
	@Override
	public boolean canFilterSubset() {
		return false;
	}

	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx) {	
		
		//Apply noise reduction
		SavitskyGolayNoiseFilter filter = new SavitskyGolayNoiseFilter();
		filter.initialize();
		//Parameter<Integer> filterRange = (Parameter<Integer>) filter.getParameters().get(0);
		//filterRange.setValue(10);
		ReadOnlySpectrum filteredData = data;
		for (int i = 0; i < pSmooth.getValue().ordinal()*2; i++) {
			filteredData = filter.filter(filteredData, ctx);
		}
		
		String mode = pMode.getValue();
		int startChannel = pStartChannel.getValue()-1;
		int endChannel = pEndChannel.getValue()-1;
		float desiredIntensity = pHeight.getValue().floatValue();
		
		float currentIntensity=0f;
		switch (mode) {
		case MODE_RANGE:
			if (startChannel >= filteredData.size()) startChannel = filteredData.size()-1;
			if (endChannel <= 0) endChannel = 0;
			int range = (endChannel - startChannel) + 1;
			currentIntensity = filteredData.subSpectrum(startChannel, endChannel).sum() / range;
			break;
		case MODE_MAX:
			currentIntensity = filteredData.max();
			break;
		case MODE_SUM:
			currentIntensity = filteredData.sum();
			break;
		case MODE_FIT:
			ITransitionSeries ts = PeakTable.SYSTEM.get(pElement.getValue(), pShell.getValue());
			FilterContext context = requireContext(ctx);
			
			
			float energy = ts.getStrongestTransition().energyValue;
			int channel = context.fittings.getFittingParameters().getCalibration().channelFromEnergy(energy);
			startChannel = Math.max(channel-5, 0);
			endChannel = Math.min(channel+5, data.size()-1);
			int frange = (endChannel - startChannel) + 1;
			currentIntensity = filteredData.subSpectrum(startChannel, endChannel).sum() / frange;
			
		}

		float ratio = currentIntensity / desiredIntensity;
		if (ratio == 0f) return new ISpectrum(data.size());
		return SpectrumCalculations.divideBy(data, ratio);

	}

	@Override
	public String getFilterDescription() {
		return "The " + getFilterName() + "filter scales each spectrum so that the intensity of the selected channel(s) matches the given noramlized intensity. Channel selection is one of max intensity, average intensity, or region of interest.";
	}

	@Override
	public String getFilterName() {
		return "Spectrum Normalizer";
	}

	@Override
	public FilterDescriptor getFilterDescriptor() {
		return new FilterDescriptor(FilterType.ADVANCED, "Normalized");
	}


	@Override
	public boolean pluginEnabled() {
		return true;
	}

	private boolean validate(Parameter<?> p) {
		String mode = pMode.getValue();
		int startChannel = pStartChannel.getValue();
		int endChannel = pEndChannel.getValue();
		float height = pHeight.getValue().floatValue();
				
		pStartChannel.setEnabled(MODE_RANGE.equals(mode));
		pEndChannel.setEnabled(MODE_RANGE.equals(mode));
		gChannelRange.setEnabled(MODE_RANGE.equals(mode));
		
		pElement.setEnabled(MODE_FIT.equals(mode));
		pShell.setEnabled(MODE_FIT.equals(mode));
		gElement.setEnabled(MODE_FIT.equals(mode));
		
		if (startChannel < 1) return false;
		if (endChannel < 1) return false;
		
		if (endChannel < startChannel) return false;
		
		if (height < 1) return false;
		if (height > 1000000) return false;
		
		return true;
	}

	@Override
	public String pluginUUID() {
		return "b9ec2709-e2d4-4700-9ac9-7d0f5b816f5f";
	}
	
}
