package peakaboo.filter.filters.advanced;


import bolt.plugin.Plugin;
import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

@Plugin
public class DataToWavelet extends AbstractSimpleFilter
{

	private static final int AMOUNT = 0;
	
	public DataToWavelet()
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
		return Noise.DataToWavelet(data, getParameter(AMOUNT).intValue());
	}


	@Override
	public String getPluginDescription()
	{
		return "The " + getPluginName() + " filter converts spectrum data into a wavelet representation. This is intended to be used in conjunction with other filters (especially the 'Filter Partial Spectrum' filter) to perform custom wavelet operations.";
	}


	@Override
	public String getPluginName()
	{
		return "Signal -> Wavelet";
	}


	@Override
	public FilterType getFilterType()
	{
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
