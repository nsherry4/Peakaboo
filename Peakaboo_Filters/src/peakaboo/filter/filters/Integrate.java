package peakaboo.filter.filters;


import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractFilter;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;


public class Integrate extends AbstractFilter
{

	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return Noise.integ(data);
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Integral (Area Under Curve)";
	}


	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.MATHEMATICAL;
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
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean showFilter()
	{
		return true;
	}

}
