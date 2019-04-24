package org.peakaboo.framework.cyclops.visualization.palette.palettes;

import java.util.List;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class ColourListPalette extends AbstractPalette {

	private List<PaletteColour> spectrum;
	private boolean hasNegatives;
	
	public ColourListPalette(List<PaletteColour> palette, boolean hasNegatives) {
		this.spectrum = palette;
		this.hasNegatives = hasNegatives;
	}
	
	@Override
	public PaletteColour getFillColour(double intensity, double maximum) {
		
		double percentage;
		if (hasNegatives) {
			percentage = (intensity + maximum) / (2 * maximum);
		} else {
			percentage = intensity / maximum;
		}
		int index = (int)(spectrum.size() * percentage);
		
		//bounds check
		if (index >= spectrum.size()) index = spectrum.size() - 1;
		if (index < 0) index = 0;
		
		
		return spectrum.get(index);
	}

}
