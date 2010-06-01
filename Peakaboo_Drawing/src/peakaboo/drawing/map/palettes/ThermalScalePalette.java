package peakaboo.drawing.map.palettes;


import java.awt.Color;
import java.util.List;

import peakaboo.drawing.common.Spectrums;


public class ThermalScalePalette extends AbstractPalette
{
	
	private List<Color> spectrum;

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
	public Color getFillColour(double intensity, double maximum)
	{
		double percentage = intensity / maximum;
		int index = (int)(spectrum.size() * percentage);
		if (index == spectrum.size()) index--;
		return spectrum.get(index);
	}

}
