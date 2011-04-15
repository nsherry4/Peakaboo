package peakaboo.filter.filters.noise;


import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Wavelet Noise Filter functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public final class AggressiveWaveletNoiseFilter extends AbstractFilter
{

	private final int	PASSES	= 0;

	public AggressiveWaveletNoiseFilter()
	{

		super();

	}

	
	@Override
	public void initialize()
	{
		addParameter(PASSES, new Parameter(ValueType.INTEGER, "Passes to Remove", 1));
	}

	@Override
	public String getPluginName()
	{

		return "Aggressive Wavelet Low-Pass";
	}


	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
	}


	@Override
	public boolean validateParameters()
	{
		int passes;

		// remove largest, least significant passes from the wavelet transform data
		// probably a bad idea to do more than 3 passes, but less than 1 is senseless
		passes = getParameter(PASSES).intValue();
		if (passes > 3 || passes < 1) return false;

		return true;
	}


	@Override
	public String getPluginDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getPluginName()
				+ " filter attempts to reduce high-frequency noise by performing a Wavelet transformation on the spectrum. This breaks the data down into sections each representing a different frequency range. The high-frequency regions are then completely removed, and a reverse transform is applied.";
	}


	@Override
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{		
		Spectrum result;
		int passes = getParameter(PASSES).intValue();

		result = Noise.FWTAgressiveLowPassFilter(data, passes);

		return result;
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
