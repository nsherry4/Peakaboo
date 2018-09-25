package cyclops.visualization;

import cyclops.visualization.palette.PaletteColour;



/**
 * 
 * A Buffer is a kind of Surface which defines its image as a pixel map. It allows for direct manipulation of
 * the pixel values which define the image, making it a much faster method of writing pixel data to a Surface
 * 
 * @author Nathaniel Sherry, 2009
 * @see Surface
 */

public interface Buffer extends Surface
{

	Object getImageSource();


	void setPixelValue(int x, int y, PaletteColour c);


	void setPixelValue(int offset, PaletteColour c);

}
