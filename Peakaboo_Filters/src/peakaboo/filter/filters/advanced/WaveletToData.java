package peakaboo.filter.filters.advanced;

import bolt.plugin.Plugin;
import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;


@Plugin
public class WaveletToData extends AbstractSimpleFilter
{

	private static final int AMOUNT = 0;
	
	public WaveletToData()
	{

		super();

	}
	
	@Override
	public void initialize()
	{
		addParameter(AMOUNT, new Parameter(ValueType.INTEGER, "Passes", 1));		
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return Noise.WaveletToData(data, getParameter(AMOUNT).intValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " filter converts a wavelet representation of data back into spectrum data.  This is intended to be used in conjunction with other filters (especially the 'Filter Partial Spectrum' filter) to perform custom wavelet operations.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Wavelet -> Signal";
	}


	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.ADVANCED;
	}


	@Override
	public boolean validateParameters()
	{
		
		if (getParameter(AMOUNT).intValue() < 1) return false;
		if (getParameter(AMOUNT).intValue() > 5) return false;
		
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
		return false;
	}


	
}
