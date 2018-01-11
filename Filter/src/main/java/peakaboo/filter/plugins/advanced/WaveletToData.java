package peakaboo.filter.plugins.advanced;

import autodialog.model.Parameter;
import autodialog.model.style.editors.IntegerStyle;
import peakaboo.calculations.Noise;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ReadOnlySpectrum;



public class WaveletToData extends AbstractSimpleFilter
{

	private Parameter<Integer> amount;
	
	public WaveletToData()
	{
		super();
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize()
	{
		amount = new Parameter<>("Passes", new IntegerStyle(), 1, this::validate);
		addParameter(amount);
	}
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		return Noise.WaveletToData(data, amount.getValue());
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


	private boolean validate(Parameter<?> p)
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
