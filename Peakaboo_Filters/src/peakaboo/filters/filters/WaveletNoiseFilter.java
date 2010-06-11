package peakaboo.filters.filters;



import java.util.List;
import peakaboo.calculations.Noise;
import peakaboo.datatypes.Spectrum;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;



/**
 * This class is a filter exposing the Wavelet Noise Filter functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class WaveletNoiseFilter extends AbstractFilter
{

	private final int	PASSES	= 0;


	public WaveletNoiseFilter()
	{
		super();
		parameters.add(PASSES, new Parameter<Integer>(ValueType.INTEGER, "Passes to Transform", 1));
	}


	@Override
	public String getFilterName()
	{
		return "Wavelet Low-Pass";
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

		// remove largest, least significant passes from the wavelet transform
		// data
		// probably a bad idea to do more than 3 passes, but less than 1 is
		// senseless
		passes = this.<Integer> getParameterValue(PASSES);
		if (passes > 8 || passes < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " Filter attempts to reduce high-frequency noise by performing a Wavelet transformation on the spectrum. This breaks the data down into sections each representing a different frequency range. The high-frequency regions are then attenuated, and a reverse transform is applied.";
	}


	@Override
	public PlotPainter getPainter()
	{
		return null;
	}


	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		Spectrum result;
		int passes = this.<Integer> getParameterValue(PASSES);

		result = Noise.FWTLowPassFilter(data, passes);

		return result;
	}
	
	@Override
	public boolean showFilter()
	{
		return true;
	}

}
