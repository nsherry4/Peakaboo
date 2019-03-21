package org.peakaboo.filter.plugins.advanced;

import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterPluginManager;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.filter.model.SerializedFilter;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.model.classinfo.ClassInfo;
import org.peakaboo.framework.autodialog.model.classinfo.SimpleClassInfo;
import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.yaml.snakeyaml.Yaml;

import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;

public class SubFilter extends AbstractFilter
{

	private Parameter<Integer> begin;
	private Parameter<Integer> end;
	private SelectionParameter<Filter> filter;
		
	@Override
	public void initialize()
	{
		List<Filter> filters = FilterPluginManager.SYSTEM.newInstances().stream().filter(f -> f.pluginEnabled() && f.canFilterSubset()).collect(Collectors.toList());
		filters.add(0, new IdentityFilter());
		
		for (Filter f : filters)
		{
			f.initialize();
		}
		
		
		begin = new Parameter<>("Start Index", new IntegerStyle(), 0, this::validate);
		end = new Parameter<>("Stop Index", new IntegerStyle(), 10, this::validate);
		
		Yaml yaml = new Yaml();
		
		ClassInfo<Filter> filterClassInfo = new SimpleClassInfo<Filter>(Filter.class, f -> yaml.dump(new SerializedFilter(f)), s -> ((SerializedFilter)yaml.load(s)).getFilter());
		
		filter = new SelectionParameter<>("Filter", new SimpleStyle<>("sub-filter", CoreStyle.LIST), filters.get(0), filterClassInfo);
		filter.setPossibleValues(filters);
		
		addParameter(begin, end, filter);
		
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		
		int start = begin.getValue();
		int stop = end.getValue();
		
		if (start >= data.size()) start = data.size()-1;
		if (stop >= data.size()) stop = data.size()-1;
		
		Spectrum result = new ISpectrum(data);
		ReadOnlySpectrum subspectrum = data.subSpectrum(start, stop);
		
		subspectrum = filter.getValue().filter(subspectrum);
		
		for (int i = start; i <= stop; i++)
		{
			result.set(i, subspectrum.get(i-start));
		}
		
		return result;
	}

	@Override
	public String getFilterDescription()
	{
		return "The " + getFilterName() + " filter allows the application of another filter to a portion of a spectrum.";
	}

	@Override
	public String getFilterName()
	{
		return "Filter Partial Spectrum";
	}

	@Override
	public FilterType getFilterType()
	{
		return FilterType.ADVANCED;
	}

	@Override
	public boolean pluginEnabled()
	{
		return true;
	}

	private boolean validate(Parameter<?> p)
	{
		
		int start = begin.getValue();
		int stop = end.getValue();
		
		if (start < 0) return false;
		if (stop < 0) return false;
		if (stop < start) return false;
		
		return true;

	}
	
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}
	
	@Override
	public String pluginUUID() {
		return "f44086fd-7b30-4ad9-a86f-761ed6a601c8";
	}
	
}
