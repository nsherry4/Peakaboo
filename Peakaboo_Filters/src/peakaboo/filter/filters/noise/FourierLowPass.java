package peakaboo.filter.filters.noise;



import bolt.plugin.Plugin;
import peakaboo.calculations.Noise;
import peakaboo.calculations.Noise.FFTStyle;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Fourier Low Pass functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

@Plugin
public final class FourierLowPass extends AbstractSimpleFilter
{

	private final int	ROLLOFF	= getNextParameterIndex();
	private final int	START	= getNextParameterIndex();
	private final int	END		= getNextParameterIndex();


	public FourierLowPass()
	{
		super();
	}

	@Override
	public void initialize()
	{
		addParameter(ROLLOFF, new Parameter(ValueType.SET_ELEMENT, "Roll-Off Type", FFTStyle.LINEAR, FFTStyle.values()));
		addParameter(START, new Parameter(ValueType.INTEGER, "Starting Wavelength (keV)", 8));
		addParameter(END, new Parameter(ValueType.INTEGER, "Ending Wavelength (keV)", 6));
	}
	

	@Override
	public boolean validateParameters()
	{

		int start, end;
		boolean isCutoff = getParameter(ROLLOFF).<FFTStyle>enumValue() == FFTStyle.CUTOFF;
		getParameter(END).enabled = (!isCutoff);

		start = getParameter(START).intValue();
		if (start > 15 || start < 1) return false;

		end = getParameter(END).intValue();
		if (end > 15 || end < 0) return false;

		if (!isCutoff && start < end) return false;

		return true;

	}


	@Override
	public String getFilterName()
	{
		return "Fourier Low-Pass";
	}


	@Override
	public FilterType getFilterType()
	{
		return FilterType.NOISE;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter applies a Fourier transformation to the spectral data, converting it into the frequency domain. Data from a high frequency range (noise) is filtered out, while lower frequencies (peaks, background) are passed through.";
	}



	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		
		data = Noise.FFTLowPassFilter(
			data,
			getParameter(ROLLOFF).<FFTStyle>enumValue(),
			getParameter(START).intValue(),
			getParameter(END).intValue()
		);

		return data;
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
