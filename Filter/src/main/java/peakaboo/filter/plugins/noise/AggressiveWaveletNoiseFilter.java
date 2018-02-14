package peakaboo.filter.plugins.noise;


import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import peakaboo.calculations.Noise;
import peakaboo.common.Version;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ReadOnlySpectrum;

/**
 * 
 * This class is a filter exposing the Wavelet Noise Filter functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */


public final class AggressiveWaveletNoiseFilter extends AbstractSimpleFilter
{

	private Parameter<Integer> passes;
	
	public AggressiveWaveletNoiseFilter()
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
		passes = new Parameter<>("Passes to Remove", new IntegerStyle(), 1, this::validate);
		addParameter(passes);
	}

	@Override
	public String getFilterName()
	{

		return "Aggressive Wavelet Low-Pass";
	}


	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
	}


	private boolean validate(Parameter<?> p)
	{
		// remove largest, least significant passes from the wavelet transform data
		// probably a bad idea to do more than 3 passes, but less than 1 is senseless
		if (passes.getValue() > 3 || passes.getValue() < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " filter attempts to reduce high-frequency noise by performing a Wavelet transformation on the spectrum. This breaks the data down into sections each representing a different frequency range. The high-frequency regions are then completely removed, and a reverse transform is applied.";
	}



	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{		
		return Noise.FWTAgressiveLowPassFilter(data, passes.getValue());
	}

	@Override
	public boolean pluginEnabled()
	{
		return !Version.release;
	}
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}


}
