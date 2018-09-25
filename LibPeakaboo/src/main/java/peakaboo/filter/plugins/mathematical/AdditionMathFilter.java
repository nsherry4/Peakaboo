package peakaboo.filter.plugins.mathematical;

import cyclops.ReadOnlySpectrum;
import cyclops.SpectrumCalculations;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.RealStyle;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;



public class AdditionMathFilter extends AbstractSimpleFilter
{

	private Parameter<Float> amount;

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize()
	{
		amount = new Parameter<>("Amount to Add", new RealStyle(), 1.0f);
		addParameter(amount);
	}
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		return SpectrumCalculations.subtractFromList(data, 0.0f-amount.getValue().floatValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " filter adds a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Add";
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

	
	@Override
	public String pluginUUID() {
		return "76b1f0cc-a825-44ec-aeac-d4b1bc38382a";
	}
	
	
}
