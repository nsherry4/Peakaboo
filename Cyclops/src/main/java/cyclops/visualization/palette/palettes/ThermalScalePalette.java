package cyclops.visualization.palette.palettes;


import java.util.List;

import cyclops.visualization.palette.PaletteColour;
import cyclops.visualization.palette.Spectrums;



public class ThermalScalePalette extends AbstractPalette
{
	
	private List<PaletteColour> spectrum;
	private boolean hasNegatives;

	public ThermalScalePalette()
	{
		this.spectrum = Spectrums.ThermalScale();
	}
	
	public ThermalScalePalette(boolean isMonochrome)
	{
		if (isMonochrome)
		{
			this.spectrum = Spectrums.MonochromeScale();
		} else {
			this.spectrum = Spectrums.ThermalScale();
		}
		
		this.hasNegatives = false;
		
	}
	
	public ThermalScalePalette(boolean isMonochrome, boolean hasNegatives)
	{
		if (isMonochrome)
		{
			this.spectrum = Spectrums.MonochromeScale();
		} else {
			this.spectrum = Spectrums.ThermalScale();
		}
		
		this.hasNegatives = hasNegatives;
		
	}
	
	public ThermalScalePalette(int steps, boolean isMonochrome)
	{
		if (isMonochrome)
		{
			this.spectrum = Spectrums.MonochromeScale(steps);
		} else {
			this.spectrum = Spectrums.ThermalScale(steps);
		}
	}

	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{
		
		double percentage;
		if (hasNegatives) {
			percentage = (intensity + maximum) / (2 * maximum);
		} else {
			percentage = intensity / maximum;
		}
		
		int index = (int)(spectrum.size() * percentage);
		if (index >= spectrum.size()) index = spectrum.size() - 1;
		if (index < 0) index = 0;
		
		
		return spectrum.get(index);
	}

}
