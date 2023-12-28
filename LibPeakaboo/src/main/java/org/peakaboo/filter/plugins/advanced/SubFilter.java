package org.peakaboo.filter.plugins.advanced;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.model.classinfo.SimpleClassInfo;
import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class SubFilter extends AbstractFilter {

	private Parameter<Integer> begin;
	private Parameter<Integer> end;
	private SelectionParameter<Filter> filter;
		
	@Override
	public void initialize() {
		List<Filter> filters = FilterRegistry.system().newInstances().stream()
				.filter(f -> f.pluginEnabled() && f.canFilterSubset())
				.collect(Collectors.toList());
		filters.add(0, new IdentityFilter());
		
		for (Filter f : filters) {
			f.initialize();
		}
		
		
		begin = new Parameter<>("Start Index", new IntegerStyle(), 0, this::validate);
		end = new Parameter<>("Stop Index", new IntegerStyle(), 10, this::validate);
		
		var filterClassInfo = new SimpleClassInfo<Filter>(
				Filter.class, 
				f -> f.save().serialize(), 
				s -> FilterRegistry.fromSaved(s).orElse(null));
		
		filter = new SelectionParameter<>("Filter", new SimpleStyle<>("sub-filter", CoreStyle.LIST), filters.get(0), filterClassInfo);
		filter.setPossibleValues(filters);
		
		addParameter(begin, end, filter);
		
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	
	@Override
	protected SpectrumView filterApplyTo(SpectrumView data, Optional<FilterContext> ctx) {
		
		int start = begin.getValue();
		int stop = end.getValue();
		
		if (start >= data.size()) start = data.size()-1;
		if (stop >= data.size()) stop = data.size()-1;
		
		Spectrum result = new ArraySpectrum(data);
		SpectrumView subspectrum = data.subSpectrum(start, stop);
		
		subspectrum = filter.getValue().filter(subspectrum, ctx);
		
		for (int i = start; i <= stop; i++)
		{
			result.set(i, subspectrum.get(i-start));
		}
		
		return result;
	}

	@Override
	public String getFilterDescription() {
		return "The " + getFilterName() + " filter allows the application of another filter to a portion of a spectrum.";
	}

	@Override
	public String getFilterName() {
		return "Filter Partial Spectrum";
	}

	@Override
	public FilterDescriptor getFilterDescriptor() {
		// When we're choosing this filter from a list, we won't have a subfilter chosen
		// yet, so we show it in the ADVANCED category. When using this filter to modify
		// data, we want to show the user what the subfilter is doing, so we return it's
		// value directly
		if (filter == null || filter.getValue() == null) {
			return FilterDescriptor.ADVANCED;
		} else {
			return filter.getValue().getFilterDescriptor();
		}
	}

	private boolean validate(Parameter<?> p) {
		
		int start = begin.getValue();
		int stop = end.getValue();
		
		if (start < 0) return false;
		if (stop < 0) return false;
		if (stop < start) return false;
		
		return true;

	}
	
	
	@Override
	public boolean canFilterSubset() {
		return false;
	}
	
	@Override
	public String getFilterUUID() {
		return "f44086fd-7b30-4ad9-a86f-761ed6a601c8";
	}
	
}
