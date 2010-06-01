package peakaboo.drawing.backends;

import java.awt.Color;



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

	public Object getImageSource();


	public void setPixelValue(int x, int y, Color c);


	public void setPixelValue(int offset, Color c);

}
