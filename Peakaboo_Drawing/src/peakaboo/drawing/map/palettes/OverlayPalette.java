package peakaboo.drawing.map.palettes;


import java.awt.Color;
import java.util.List;

import peakaboo.drawing.common.Spectrums;


public class OverlayPalette extends AbstractPalette
{
	
	private List<Color> spectrum;

	public OverlayPalette()
	{
		this.spectrum = Spectrums.MonochromeScale();
	}
	
	public OverlayPalette(Color c)
	{
		this.spectrum = Spectrums.MonochromeScale(c);
	}
	
	public OverlayPalette(int steps, Color c)
	{
		this.spectrum = Spectrums.MonochromeScale(steps, c);
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
