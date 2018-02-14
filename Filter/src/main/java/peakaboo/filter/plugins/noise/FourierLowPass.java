package peakaboo.filter.plugins.noise;



import java.util.Arrays;

import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.SelectionParameter;
import net.sciencestudio.autodialog.model.style.editors.DropDownStyle;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import peakaboo.calculations.Noise;
import peakaboo.calculations.Noise.FFTStyle;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ReadOnlySpectrum;

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
	private SelectionParameter<FFTStyle> rolloff;


	public FourierLowPass()
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
		rolloff = new SelectionParameter<>("Roll-Off Type", new DropDownStyle<>(), FFTStyle.LINEAR);
		rolloff.setPossibleValues(Arrays.asList(FFTStyle.values()));
		startWavelength = new Parameter<>("Starting Wavelength (keV)", new IntegerStyle(), 8, this::validate);
		endWavelength = new Parameter<>("Ending Wavelength (keV)", new IntegerStyle(), 6, this::validate);
		
		addParameter(rolloff, startWavelength, endWavelength);
	}
	

	private boolean validate(Parameter<?> p)
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
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
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
