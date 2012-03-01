package peakaboo.filter.filters.mathematical;



import bolt.plugin.Plugin;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

@Plugin
public class Multiply extends AbstractSimpleFilter
{

	private final int AMOUNT = getNextParameterIndex();
	
	@Override
	public void initialize()
	{
		addParameter(AMOUNT, new Parameter(ValueType.REAL, "Multiply By", 1.0));
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return SpectrumCalculations.multiplyBy(data, getParameter(AMOUNT).realValue());
	}


	@Override
	public String getFilterDescription()
	{
		return "The " + getFilterName() + " filter multiplies all points on a spectrum by a constant value.";
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
	public boolean validateParameters()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	
	@Override
	public boolean canFilterSubset()
	{
		return true;
	}

	@Override
	public boolean showSaveLoad()
	{
		return false;
	}
	
}
