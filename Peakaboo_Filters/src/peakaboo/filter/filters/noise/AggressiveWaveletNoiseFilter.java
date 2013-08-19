package peakaboo.filter.filters.noise;


import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import peakaboo.calculations.Noise;
import peakaboo.common.Version;
import peakaboo.filter.filters.AbstractSimpleFilter;
import scitypes.Spectrum;

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
	public void initialize()
	{
		passes = new Parameter<>("Passes to Remove", new IntegerEditor(), 1);
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


	@Override
	public boolean validateParameters()
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
	protected Spectrum filterApplyTo(Spectrum data)
	{		
		Spectrum result;

		result = Noise.FWTAgressiveLowPassFilter(data, passes.getValue());

		return result;
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
