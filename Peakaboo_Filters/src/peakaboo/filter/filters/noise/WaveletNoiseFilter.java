package peakaboo.filter.filters.noise;



import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import scitypes.Spectrum;



/**
 * This class is a filter exposing the Wavelet Noise Filter functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class WaveletNoiseFilter extends AbstractSimpleFilter
{

	private Parameter<Integer> passes;


	public WaveletNoiseFilter()
	{
		super();

	}

	@Override
	public void initialize()
	{
		passes = new Parameter<>("Passes to Transform", new IntegerEditor(), 1);
		addParameter(passes);
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
		int passCount;

		// remove largest, least significant passes from the wavelet transform
		// data
		// probably a bad idea to do more than 3 passes, but less than 1 is
		// senseless
		passCount = passes.getValue();
		if (passCount > 8 || passCount < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to reduce high-frequency noise by performing a Wavelet transformation on the spectrum. This breaks the data down into sections each representing a different frequency range. The high-frequency regions are then smoothed, and a reverse transform is applied.";
	}


	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		Spectrum result;
		int passCount= passes.getValue();

		result = Noise.FWTLowPassFilter(data, passCount);

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
