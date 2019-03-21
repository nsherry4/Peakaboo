package org.peakaboo.display.map.modes;


import java.util.List;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.Spectrums;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;


public class OverlayPalette extends AbstractPalette
{
	
	private List<PaletteColour> spectrum;
	private double lowCutoff = 0;

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

	
	
	public double getLowCutoff() {
		return lowCutoff;
	}

	public void setLowCutoff(double lowCutoff) {
		this.lowCutoff = lowCutoff;
	}

	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{
		if (intensity < lowCutoff * maximum) {
			intensity = 0;
		}
		double percentage = intensity / maximum;
		int index = (int)(spectrum.size() * percentage);

		//bounds check
		if (index >= spectrum.size()) index = spectrum.size() - 1;
		if (index < 0) index = 0;
		
		return spectrum.get(index);
	}

}
