package peakaboo.filters.filters;



import peakaboo.calculations.Noise;
import peakaboo.calculations.Noise.FFTStyle;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Fourier Low Pass functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class FourierLowPass extends AbstractFilter
{

	private final int	ROLLOFF	= 0;
	private final int	START	= 1;
	private final int	END		= 2;


	public FourierLowPass()
	{
		super();
		parameters.put(ROLLOFF, new Parameter<FFTStyle>(ValueType.SET_ELEMENT, "Roll-Off Type", FFTStyle.LINEAR, FFTStyle.values()));
		parameters.put(START, new Parameter<Integer>(ValueType.INTEGER, "Starting Wavelength (keV)", 8));
		parameters.put(END, new Parameter<Integer>(ValueType.INTEGER, "Ending Wavelength (keV)", 6));

	}


	@Override
	public boolean validateParameters()
	{

		int start, end;
		boolean isCutoff = this.<FFTStyle>getParameterValue(ROLLOFF) == FFTStyle.CUTOFF;
		parameters.get(END).enabled = (!isCutoff);

		start = this.<Integer>getParameterValue(START);
		if (start > 15 || start < 1) return false;

		end = this.<Integer>getParameterValue(END);
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
				+ " Filter transforms the spectral data with a Fourier Transformation into a frequency domain. Data from a high frequency range (noise) is filtered out, while lower frequencies (peaks, background) are passed through.";
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
		
		data = Noise.FFTLowPassFilter(
			data,
			this.<FFTStyle>getParameterValue(ROLLOFF),
			this.<Integer>getParameterValue(START),
			this.<Integer>getParameterValue(END)
		);

		return data;
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}


}
