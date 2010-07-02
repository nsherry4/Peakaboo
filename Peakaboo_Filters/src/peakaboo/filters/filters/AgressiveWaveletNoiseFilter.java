package peakaboo.filters.filters;


import peakaboo.calculations.Noise;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Wavelet Noise Filter functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public final class AgressiveWaveletNoiseFilter extends AbstractFilter
{

	private final int	PASSES	= 0;

	public AgressiveWaveletNoiseFilter()
	{

		super();
		parameters.add(PASSES, new Parameter<Integer>(ValueType.INTEGER, "Passes to Remove", 1));

	}


	@Override
	public String getFilterName()
	{

		return "Agressive Wavelet Low-Pass";
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
		passes = this.<Integer>getParameterValue(PASSES);
		if (passes > 3 || passes < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " Filter attempts to reduce high-frequency noise by performing a Wavelet transformation on the spectrum. This breaks the data down into sections each representing a different frequency range. The high-frequency regions are then completely removed, and a reverse transform is applied.";
	}


	@Override
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{		
		Spectrum result;
		int passes = this.<Integer>getParameterValue(PASSES);

		result = Noise.FWTAgressiveLowPassFilter(data, passes);

		return result;
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}

}
