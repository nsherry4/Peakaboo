package cyclops.visualization.palette.palettes;

import cyclops.visualization.palette.PaletteColour;

/**
 * A Palette is used to control the colour scheme used to fill in a Map.
 * 
 * @author Nathaniel Sherry, 2009
 * @see scidraw.drawing.map.MapDrawing Map
 * @see Color
 * 
 */

public abstract class AbstractPalette
{

	/**
	 * Returns a Colour object if this palette defines a Colour for the given intensity, or null if it does
	 * not.
	 * 
	 * @param intensity
	 *            an intensity between 0 and maximum for a portion of the map
	 * @param maximum
	 *            the maximum intensity for this map
	 * @return a Colour, or null
	 */
	public abstract PaletteColour getFillColour(double intensity, double maximum);


}
