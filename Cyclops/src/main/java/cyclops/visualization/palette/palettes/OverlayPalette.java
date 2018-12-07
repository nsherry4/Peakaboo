package cyclops.visualization.palette.palettes;


import java.util.List;

import cyclops.visualization.palette.PaletteColour;
import cyclops.visualization.palette.Spectrums;


public class OverlayPalette extends AbstractPalette
{
	
	private List<PaletteColour> spectrum;

	public OverlayPalette()
	{
		this.spectrum = Spectrums.MonochromeScale();
	}
	
	public OverlayPalette(PaletteColour c)
	{
		this.spectrum = Spectrums.MonochromeScale(c);
	}
	
	public OverlayPalette(int steps, PaletteColour c)
	{
		this.spectrum = Spectrums.MonochromeScale(steps, c);
	}

	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{
		double percentage = intensity / maximum;
		int index = (int)(spectrum.size() * percentage);

		//bounds check
		if (index >= spectrum.size()) index = spectrum.size() - 1;
		if (index < 0) index = 0;
		
		return spectrum.get(index);
	}

}
