package peakaboo.filter.filters.advanced;

import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



public class DataToWavelet extends AbstractFilter
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
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return Noise.DataToWavelet(data, getParameter(AMOUNT).intValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " filter converts spectrum data into a wavelet representation. This is intended to be used in conjunction with other filters (especially the 'Filter Partial Spectrum' filter) to perform custom wavelet operations.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Signal -> Wavelet";
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
	public boolean validateParameters()
	{
		
		if (getParameter(AMOUNT).intValue() < 1) return false;
		if (getParameter(AMOUNT).intValue() > 5) return false;
		
		return true;
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}



	
}
