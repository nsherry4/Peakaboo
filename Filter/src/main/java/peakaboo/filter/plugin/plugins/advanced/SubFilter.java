package peakaboo.filter.plugin.plugins.advanced;

import java.util.List;
import java.util.stream.Collectors;

import autodialog.model.Parameter;
import autodialog.model.SelectionParameter;
import autodialog.model.style.CoreStyle;
import autodialog.model.style.SimpleStyle;
import autodialog.model.style.editors.IntegerStyle;
import peakaboo.filter.model.AbstractFilter;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterLoader;
import peakaboo.filter.model.FilterType;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

public class SubFilter extends AbstractFilter
{

	private Parameter<Integer> begin;
	private Parameter<Integer> end;
	private SelectionParameter<Filter> filter;
		
	@Override
	public void initialize()
	{
		List<Filter> filters = FilterLoader.getPluginSet().newInstances().stream().filter(f -> f.pluginEnabled() && f.canFilterSubset()).collect(Collectors.toList());
		filters.add(0, new Identity());
		
		for (Filter f : filters)
		{
			f.initialize();
		}
		
		
		begin = new Parameter<>("Start Index", new IntegerStyle(), 0, this::validate);
		end = new Parameter<>("Stop Index", new IntegerStyle(), 10, this::validate);
		filter = new SelectionParameter<>("Filter", new SimpleStyle<>("sub-filter", CoreStyle.LIST), filters.get(0));
		filter.setPossibleValues(filters);
		
		addParameter(begin, end, filter);
		
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, boolean cache)
	{
		
		int start = begin.getValue();
		int stop = end.getValue();
		
		if (start >= data.size()) start = data.size()-1;
		if (stop >= data.size()) stop = data.size()-1;
		
		Spectrum result = new ISpectrum(data);
		ReadOnlySpectrum subspectrum = data.subSpectrum(start, stop);
		
		subspectrum = filter.getValue().filter(subspectrum, cache);
		
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
	public PlotPainter getPainter()
	{
		
		if (filter.getValue().getPainter() == null) return null;
		
		return new PlotPainter() {

			@Override
			public void drawElement(PainterData p)
			{			
				p.context.save();
				
					float pointWidth = p.plotSize.x / p.dr.dataWidth;
					p.context.translate(pointWidth*begin.getValue(), 0f);
					filter.getValue().getPainter().draw(p);
					
				p.context.restore();
			}
		};
		
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
	
}
