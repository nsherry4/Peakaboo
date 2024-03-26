package org.peakaboo.framework.cyclops.visualization;

import java.util.List;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;



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

	int getWidth();
	int getHeight();
	default int getSize() {
		return getWidth() * getHeight();
	}
	
	void setPixelValue(int x, int y, PaletteColour c);
	void setPixelValue(int offset, PaletteColour c);
	void setPixelARGB(int offset, int c);

	void clear();
	
	PaletteColour getPixelValue(int x, int y);
	PaletteColour getPixelValue(int index);
	List<PaletteColour> getPixelValues();
	
	int getPixelARGB(int index);
	int getPixelARGB(int x, int y);
	List<Integer> getPixelsARGB(); 
	
}
