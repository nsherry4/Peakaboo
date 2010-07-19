package peakaboo.filter;


import peakaboo.common.Version;
import peakaboo.filter.BackgroundRemovalFilter.backgroundParams;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.Spectrum;

public class ExampleFilter extends AbstractFilter {

	//integer index for parameters
	private final int PARAM1 = 1;
	
	public ExampleFilter() {
		//add a parameter with name "example parameter" and default value 1
		parameters.put(PARAM1, new Parameter(ValueType.INTEGER, "Example Parameter", 1));
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
		return (getParameter(PARAM1).intValue() > 0);
	}

	@Override
	public boolean showFilter()
	{
		return !Version.release;
	}

}
