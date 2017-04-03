package peakaboo.filter.plugins.advanced;


import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import peakaboo.calculations.Noise;
import peakaboo.filter.plugins.AbstractSimpleFilter;
import scitypes.Spectrum;


public class DataToWavelet extends AbstractSimpleFilter
{

	private Parameter<Integer> amount;
	
	public DataToWavelet()
	{

		super();

	}
	

	@Override
	public void initialize()
	{
		amount = new Parameter<>("Passes", new IntegerEditor(), 1);
		addParameter(amount);
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return Noise.DataToWavelet(data, amount.getValue());
	}


	@Override
	public String getFilterDescription()
	{
		return "The " + getFilterName() + " filter converts spectrum data into a wavelet representation. This is intended to be used in conjunction with other filters (especially the 'Filter Partial Spectrum' filter) to perform custom wavelet operations.";
	}


	@Override
	public String getFilterName()
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
		
		if (amount.getValue() < 1) return false;
		if (amount.getValue() > 5) return false;
		
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
