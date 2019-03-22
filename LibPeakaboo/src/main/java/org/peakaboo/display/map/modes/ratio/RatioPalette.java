package org.peakaboo.display.map.modes.ratio;



import java.util.List;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.Spectrums;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;



public class RatioPalette extends AbstractPalette
{

	private List<PaletteColour>	spectrum;


	public RatioPalette()
	{
		this.spectrum = Spectrums.RatioThermalScale();
	}


	public RatioPalette(boolean isMonochrome)
	{
		if (isMonochrome)
		{
			this.spectrum = Spectrums.RatioMonochromeScale();
		}
		else
		{
			this.spectrum = Spectrums.RatioThermalScale();
		}
	}


	public RatioPalette(int steps, boolean isMonochrome)
	{
		if (isMonochrome)
		{
			this.spectrum = Spectrums.RatioMonochromeScale(steps);
		}
		else
		{
			this.spectrum = Spectrums.RatioThermalScale(steps);
		}
	}


	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{	
		double percentage = (intensity + maximum) / (2 * maximum);
		int index = (int) (spectrum.size() * percentage);

		//bounds check
		if (index >= spectrum.size()) index = spectrum.size() - 1;
		if (index < 0) index = 0;
		
		return spectrum.get(index);
	}

}
