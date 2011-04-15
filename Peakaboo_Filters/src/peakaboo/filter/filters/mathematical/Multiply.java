package peakaboo.filter.filters.mathematical;



import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class Multiply extends AbstractFilter
{

	private static final int AMOUNT = 0;
	
	public Multiply()
	{
		super();
	}
	
	
	@Override
	public void initialize()
	{
		addParameter(AMOUNT, new Parameter(ValueType.REAL, "Multiply By", 1.0));
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return SpectrumCalculations.multiplyBy(data, getParameter(AMOUNT).realValue());
	}


	@Override
	public String getPluginDescription()
	{
		return "The " + getPluginName() + " filter multiplies all points on a spectrum by a constant value.";
	}


	@Override
	public String getPluginName()
	{
		// TODO Auto-generated method stub
		return "Multiply";
	}


	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.MATHEMATICAL;
	}


	@Override
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean validateParameters()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean pluginEnabled()
	{
		return false;
	}
	
	
	@Override
	public boolean canFilterSubset()
	{
		return true;
	}
	
}
