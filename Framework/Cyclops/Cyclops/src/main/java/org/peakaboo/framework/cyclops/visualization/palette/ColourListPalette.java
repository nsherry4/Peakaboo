package org.peakaboo.framework.cyclops.visualization.palette;

import java.util.List;

/**
 * Accepts a list of evenly spaced {@link PaletteColour}s and provides a
 */
public class ColourListPalette implements Palette {

	private List<PaletteColour> spectrum;
	private int[] argbSpectrum;
	private boolean hasNegatives;
	
	public ColourListPalette(List<PaletteColour> palette, boolean hasNegatives) {
		this.spectrum = palette;
		this.hasNegatives = hasNegatives;
		argbSpectrum = new int[palette.size()];

		// Build the argb int index
		int index = 0;
		for (var c : palette) {
			argbSpectrum[index++] = c.getARGB();
		}
	}

	private int calcIndex(double intensity, double maximum) {
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

		return index;
	}

	@Override
	public PaletteColour getFillColour(double intensity, double maximum) {
		return spectrum.get(calcIndex(intensity, maximum));
	}

	@Override
	public int getARGBFillColour(double intensity, double maximum) {
		return argbSpectrum[calcIndex(intensity, maximum)];
	}

}
