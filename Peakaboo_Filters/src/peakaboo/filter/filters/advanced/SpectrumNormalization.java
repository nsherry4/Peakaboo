package peakaboo.filter.filters.advanced;

import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class SpectrumNormalization extends AbstractSimpleFilter
{
	
	public int	CHANNEL;
	public int	HEIGHT;

	@Override
	public void initialize()
	{
		CHANNEL = addParameter(new Parameter("Channel", ValueType.INTEGER, 1));
		HEIGHT = addParameter(new Parameter("Intensity", ValueType.REAL, 10d));	
	}
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{

		int channel = getParameter(CHANNEL).intValue()+1;
		float height = getParameter(HEIGHT).realValue();
		
		if (channel >= data.size()) return data;
		
		float ratio = data.get(channel) / height;
		if (ratio == 0f) return new Spectrum(data.size());
		return SpectrumCalculations.divideBy(data, ratio);
		
	}

	@Override
	public String getFilterDescription()
	{
		return "The " + getFilterName() + " scales each spectrum so that the intensity at a given channel is always the same.";
	}

	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Normalizer";
	}

	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.ADVANCED;
	}


	@Override
	public boolean pluginEnabled()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean validateParameters()
	{
		
		int channel = getParameter(CHANNEL).intValue();
		float height = getParameter(HEIGHT).realValue();
		
		if (channel < 1) return false;
		if (height < 1) return false;
		if (height > 1000000) return false;
		
		return true;
	}

}
