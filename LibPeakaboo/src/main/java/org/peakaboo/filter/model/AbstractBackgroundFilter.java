package org.peakaboo.filter.model;

import java.util.Optional;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.BooleanStyle;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.autodialog.model.style.editors.SeparatorStyle;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;


public abstract class AbstractBackgroundFilter extends AbstractFilter {

	protected Parameter<Integer> percent;
	protected Parameter<Boolean> preview;
	protected Parameter<Integer> stopindex;
	protected Parameter<Integer> startindex;
	protected Parameter<Boolean> partial;

	
	protected AbstractBackgroundFilter() {
		
		percent = new Parameter<>("Percent to Remove", new IntegerStyle(), 90, this::validate);
		preview = new Parameter<>("Preview Only", new BooleanStyle(), Boolean.FALSE, this::validate);
		
		Parameter<?> sep1 = new Parameter<>("separator-1", new SeparatorStyle(), 0);
		
		partial = new Parameter<>("Apply to Subset", new BooleanStyle(), Boolean.FALSE, this::validate);
		startindex = new Parameter<>("Start Index", new IntegerStyle(), 0, this::validate);
		stopindex = new Parameter<>("Stop Index", new IntegerStyle(), 0, this::validate);
		partial.getValueHook().addListener(enabled -> {
			startindex.setEnabled(enabled);
			stopindex.setEnabled(enabled);
		});
		
		
		Parameter<?> sep2 = new Parameter<>("separator-2", new SeparatorStyle(), 0);
		
		
		startindex.setEnabled(false);
		stopindex.setEnabled(false);
		
		addParameter(percent, preview, sep1, partial, startindex, stopindex, sep2);
		
	}
	
	@Override
	public FilterDescriptor getFilterDescriptor() {
		return FilterDescriptor.BACKGROUND;
	}
	
	private boolean validate(Parameter<?> p) {

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		if (percent.getValue() > 100 || percent.getValue() < 0) return false;
		
		if (startindex.getValue() < 0) return false;
		if (stopindex.getValue() < startindex.getValue()) return false;
		
		return true;
		
	}
	
	
	protected abstract SpectrumView getBackground(SpectrumView data, Optional<FilterContext> ctx, int percent);
	
	private final SpectrumView getImplBackground(SpectrumView data, Optional<FilterContext> ctx) {
		if (data == null) {
			return null;
		}
		
		int start = startindex.getValue();
		int stop = stopindex.getValue();
		if (stop >= data.size()) stop = data.size() - 1;
		if (start >= data.size()) start = data.size() - 1;
		
		boolean usePartial = partial.getValue();
		
		if (usePartial) {
			
			SpectrumView partialData = data.subSpectrum(start, stop);
			Spectrum result = new ArraySpectrum(data.size(), 0f);
			partialData = getBackground(partialData, ctx, percent.getValue());
			
			for (int i = 0; i < partialData.size(); i++)
			{
				result.set(i + start, partialData.get(i));
			}
			
			return result;
			
		} else {
			return getBackground(data, ctx, percent.getValue());
		}
		
	}
	
	@Override
	protected final SpectrumView filterApplyTo(SpectrumView data, Optional<FilterContext> ctx) {
		SpectrumView background = getImplBackground(data, ctx);
		return SpectrumCalculations.subtractLists(data, background, 0f);
	}
	
	@Override
	public boolean isPreviewOnly() {
		return preview.getValue();
	}

	
}
