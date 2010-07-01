package peakaboo.drawing.map.palettes;


import java.awt.Color;
import java.util.List;

import peakaboo.drawing.common.Spectrums;


public class ThermalScalePalette extends AbstractPalette
{
	
	private List<Color> spectrum;
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
	public Color getFillColour(double intensity, double maximum)
	{
		double percentage;
		if (hasNegatives) {
			percentage = (intensity + maximum) / (2 * maximum);
		} else {
			percentage = intensity / maximum;
		}
		
		int index = (int)(spectrum.size() * percentage);
		if (index == spectrum.size()) index--;
		return spectrum.get(index);
	}

}
