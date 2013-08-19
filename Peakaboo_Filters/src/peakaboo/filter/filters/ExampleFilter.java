package peakaboo.filter.filters;


import peakaboo.filter.model.AbstractFilter;
import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.Spectrum;

public class ExampleFilter extends AbstractFilter {

	Parameter<Integer> param;
	
	public ExampleFilter() {
		super();
	}
	
	
	@Override
	public void initialize()
	{
		param = new Parameter<>("Example Parameter", new IntegerEditor(), 1);
		addParameter(param);
	}
	
	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache) {
		return data;
	}

	@Override
	public String getFilterDescription() {
		return "This filter is an example of how to make filters";
	}

	@Override
	public String getFilterName() {
		return "Example Filter";
	}

	@Override
	public FilterType getFilterType() {
		
		FilterType type;
		
		type = FilterType.BACKGROUND;
		type = FilterType.NOISE;
		
		return type;
	}

	@Override
	public PlotPainter getPainter() {
		
		
		return new SpectrumPainter(this.previewCache) {

			@Override
			public void drawElement(PainterData p)
			{
				traceData(p);
				p.context.setSource(255,0,0);
				p.context.stroke();

			}
		};
		
	}

	@Override
	public boolean validateParameters() {
		return (param.getValue() > 0);
	}

	@Override
	public boolean pluginEnabled()
	{
		return false;
	}


	@Override
	public boolean canFilterSubset()
	{
		// TODO Auto-generated method stub
		return true;
	}



}
