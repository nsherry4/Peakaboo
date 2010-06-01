package peakaboo.filters;

import java.util.List;

import peakaboo.common.Version;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.drawing.plot.painters.SpectrumPainter;
import peakaboo.filters.Parameter.ValueType;

public class ExampleFilter extends AbstractFilter {

	//integer index for parameters
	private final int PARAM1 = 1;
	
	public ExampleFilter() {
		//add a parameter with name "example parameter" and default value 1
		parameters.add(PARAM1, new Parameter<Integer>(ValueType.INTEGER, "Example Parameter", 1));
	}
	
	@Override
	public List<Double> filterApplyTo(List<Double> data, boolean cache) {
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
				p.context.setSource(1.0, 0.0, 0.0);
				p.context.stroke();

			}
		};
		
	}

	@Override
	public boolean validateParameters() {
		return (this.<Integer>getParameterValue(PARAM1) > 0);
	}

	@Override
	public boolean showFilter()
	{
		return !Version.release;
	}

}
