package peakaboo.filter.plugins.advanced;

import java.util.List;
import java.util.stream.Collectors;

import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import peakaboo.filter.FilterLoader;
import peakaboo.filter.editors.SubfilterEditor;
import peakaboo.filter.model.AbstractFilter;
import peakaboo.filter.model.Filter;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.ISpectrum;
import scitypes.Spectrum;

public class FilterPartialSpectrum extends AbstractFilter
{

	private Parameter<Integer> begin;
	private Parameter<Integer> end;
	private Parameter<Filter> filter;
	
	
	@Override
	public void initialize()
	{
		List<Filter> filters = FilterLoader.getAvailableFilters().stream().filter(f -> f.pluginEnabled() && f.canFilterSubset()).collect(Collectors.toList());
		filters.add(0, new Identity());
		
		for (Filter f : filters)
		{
			f.initialize();
		}
		
		
		begin = new Parameter<>("Start Index", new IntegerEditor(), 0);
		end = new Parameter<>("Stop Index", new IntegerEditor(), 10);
		filter = new Parameter<>("Filter", new SubfilterEditor(filters), filters.get(0));
		
		addParameter(begin, end, filter);
		
	}
	
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		
		int start = begin.getValue();
		int stop = end.getValue();
		
		if (start >= data.size()) start = data.size()-1;
		if (stop >= data.size()) stop = data.size()-1;
		
		Spectrum result = new ISpectrum(data);
		Spectrum subspectrum = data.subSpectrum(start, stop);
		
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
	public Filter.FilterType getFilterType()
	{
		return Filter.FilterType.ADVANCED;
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

	@Override
	public boolean validateParameters()
	{
		
		int start = begin.getValue();
		int stop = end.getValue();
		
		if (start < 0) return false;
		if (stop < 0) return false;
		if (stop < start) return false;
		
		return filter.getValue().validateParameters();
	}
	
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}
	
}
