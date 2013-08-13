package peakaboo.filter.filters.noise;



import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import autodialog.view.editors.ListEditor;
import peakaboo.calculations.Noise;
import peakaboo.calculations.Noise.FFTStyle;
import peakaboo.filter.AbstractSimpleFilter;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Fourier Low Pass functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class FourierLowPass extends AbstractSimpleFilter
{
	
	private Parameter<Integer> startWavelength;
	private Parameter<Integer> endWavelength;
	private Parameter<FFTStyle> rolloff;


	public FourierLowPass()
	{
		super();
	}

	@Override
	public void initialize()
	{
		rolloff = new Parameter<>("Roll-Off Type", new ListEditor<>(FFTStyle.values()), FFTStyle.LINEAR);
		startWavelength = new Parameter<>("Starting Wavelength (keV)", new IntegerEditor(), 8);
		endWavelength = new Parameter<>("Ending Wavelength (keV)", new IntegerEditor(), 6);
		
		addParameter(rolloff, startWavelength, endWavelength);
	}
	

	@Override
	public boolean validateParameters()
	{

		int start, end;
		boolean isCutoff = rolloff.getValue() == FFTStyle.CUTOFF;
		endWavelength.setEnabled(!isCutoff);

		start = startWavelength.getValue();
		if (start > 15 || start < 1) return false;

		end = endWavelength.getValue();
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
			rolloff.getValue(),
			startWavelength.getValue(),
			endWavelength.getValue()
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
