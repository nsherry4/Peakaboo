package peakaboo.filters.filters;



import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class Multiply extends AbstractFilter
{

	private static final int AMOUNT = 0;
	
	public Multiply()
	{

		super();
		parameters.put(AMOUNT, new Parameter<Double>(ValueType.REAL, "Multiply By", 1.0));

	}
	
	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return SpectrumCalculations.multiplyBy(data, this.<Double>getParameterValue(AMOUNT).floatValue());
	}


	@Override
	public String getFilterDescription()
	{
		return "The" + getFilterName() + " filter multiplies all points on a spectrum by a constant value.";
	}


	@Override
	public String getFilterName()
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
	public boolean showFilter()
	{
		return false;
	}
	
}
