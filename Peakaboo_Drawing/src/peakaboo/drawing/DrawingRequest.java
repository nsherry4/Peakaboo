package peakaboo.drawing;


import java.io.Serializable;

import peakaboo.drawing.plot.ViewTransform;

/**
 * 
 * This drawing request contains those parameters which are common to all kinds of drawing reuqests,
 * so that we don't duplicate logic/bugs across several drawing request objects.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

// more like a struct than a class
public class DrawingRequest implements Serializable, Cloneable
{

	/**
	 * The dimensions of the actual data being drawn
	 */
	public int	dataHeight, dataWidth;

	/**
	 * The dimensions of the image to be drawn
	 */
	public double	imageWidth, imageHeight;

	/**
	 * Indicates if this plot is to be drawn to a scalar surface or a raster surface. Some drawing methods may
	 * use a different approach for raster and scalar graphics, which can impact speed and quality of the
	 * drawing.
	 */
	public boolean	drawToVectorSurface;

	/**
	 * Maximum intensity of any data point in the drawing. This provides a way to accurately scale the data.
	 * Often if this is not provided, the maximum point in a given data set will be used as a maximum
	 * intensity.
	 */
	public double	maxYIntensity;

	
	/**
	 * How this data should be transformed before being drawn
	 */
	public ViewTransform	viewTransform;
	
	
	/**
	 * The base unit size or scale for a single data point
	 */
	public double	unitSize;
	
	@Override
	public DrawingRequest clone()
	{
		try {
			return (DrawingRequest) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}



}
