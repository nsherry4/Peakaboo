package peakaboo.sdk.examples.filter;


import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;
import peakaboo.filter.model.AbstractFilter;
import peakaboo.filter.model.FilterType;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.ReadOnlySpectrum;

public class ExampleFilter extends AbstractFilter {

	Parameter<Integer> param;
	
	public ExampleFilter() {
		super();
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public String pluginUUID() {
		return "4c980528-4ac3-443a-9bc7-89080049f280";
	}
	
	
	@Override
	public void initialize()
	{
		param = new Parameter<>("Example Parameter", new IntegerSpinnerStyle(), 1, p -> p.getValue() > 0);
		addParameter(param);
	}
	
	@Override
	public ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, boolean cache) {
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
