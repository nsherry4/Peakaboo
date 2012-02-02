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

	private final int	ROLLOFF	= 0;
	private final int	START	= 1;
	private final int	END		= 2;


	public FourierLowPass()
	{
		super();
	}

	@Override
	public void initialize()
	{
		addParameter(END, new Parameter(ValueType.INTEGER, "Ending Wavelength (keV)", 6));
		addParameter(START, new Parameter(ValueType.INTEGER, "Starting Wavelength (keV)", 8));
		addParameter(ROLLOFF, new Parameter(ValueType.SET_ELEMENT, "Roll-Off Type", FFTStyle.LINEAR, FFTStyle.values()));
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
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " filter transforms the spectral data with a Fourier Transformation into a frequency domain. Data from a high frequency range (noise) is filtered out, while lower frequencies (peaks, background) are passed through.";
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
