package org.peakaboo.filter.plugins.mathematical;


import org.peakaboo.filter.model.AbstractSimpleFilter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;


public class SubtractionMathFilter extends AbstractSimpleFilter
{

	private Parameter<Float> amount;
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize()
	{
		amount = new Parameter<>("Amount to Subtract", new RealStyle(), 1.0f);
		addParameter(amount);
	}
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		return SpectrumCalculations.subtractFromList(data, amount.getValue().floatValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " filter subtracts a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Subtract";
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
		return "06557ce2-5587-4e73-abdb-f2d5dbb16f81";
	}
	
	
}
