package peakaboo.filter.filters.advanced;

import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class SpectrumNormalization extends AbstractFilter
{
	
	public static int	CHANNEL = 0;
	public static int	HEIGHT = 1;

	@Override
	public void initialize()
	{
		parameters.put(CHANNEL, new Parameter(ValueType.INTEGER, "Channel", 1));
		parameters.put(HEIGHT, new Parameter(ValueType.REAL, "Intensity", 10d));
	}
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{

		int channel = parameters.get(CHANNEL).intValue()+1;
		float height = parameters.get(HEIGHT).realValue();
		
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
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean showFilter()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean validateParameters()
	{
		
		int channel = parameters.get(CHANNEL).intValue();
		float height = parameters.get(HEIGHT).realValue();
		
		if (channel < 1) return false;
		if (height < 1) return false;
		if (height > 1000000) return false;
		
		return true;
	}

}
