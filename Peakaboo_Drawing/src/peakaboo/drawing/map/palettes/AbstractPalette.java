package peakaboo.drawing.map.palettes;

import java.awt.Color;


/**
 * A Palette is used to control the colour scheme used to fill in a Map.
 * 
 * @author Nathaniel Sherry, 2009
 * @see peakaboo.drawing.map.Map Map
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
	public abstract Color getFillColour(double intensity, double maximum);


}
