package peakaboo.filter.plugin.plugins.mathematical;



import autodialog.model.Parameter;
import autodialog.model.style.editors.RealStyle;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ReadOnlySpectrum;
import scitypes.SpectrumCalculations;


public class Multiply extends AbstractSimpleFilter
{

	private Parameter<Float> amount;
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize()
	{
		amount = new Parameter<>("Multiply By", new RealStyle(), 1.0f);
		addParameter(amount);
		
	}
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		return SpectrumCalculations.multiplyBy(data, amount.getValue().floatValue());
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
	public boolean pluginEnabled()
	{
		return true;
	}
	
	
	@Override
	public boolean canFilterSubset()
	{
		return true;
	}

}
