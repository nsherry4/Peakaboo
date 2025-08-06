package org.peakaboo.framework.cyclops.visualization.palette;

/**
 * A Palette provides a PaletteColour for any given intensity and maximum. It is
 * used to control the colour scheme used to fill in a Map.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public interface Palette {

	/**
	 * Returns a PaletteColour for the given intensity
	 * 
	 * @param intensity
	 *            an intensity between 0 and maximum for a portion of the map
	 * @param maximum
	 *            the maximum intensity for this map
	 * @return a PaletteColour
	 */
	default PaletteColour getFillColour(double intensity, double maximum) {
		return new PaletteColour(getARGBFillColour(intensity, maximum));
	}

	/**
	 * Returns an ARGB int for the given intensity
	 *
	 * @param intensity
	 *            an intensity between 0 and maximum for a portion of the map
	 * @param maximum
	 *            the maximum intensity for this map
	 * @return an ARGB int
	 */
	public int getARGBFillColour(double intensity, double maximum);

}
