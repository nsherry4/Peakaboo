package org.peakaboo.framework.cyclops.visualization.drawing;


import java.io.Serializable;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.log.CyclopsLog;


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
	 * The dimensions of the original data after interpolation
	 */
	public int	uninterpolatedHeight, uninterpolatedWidth;
	
	/**
	 * The dimensions of the image to be drawn
	 */
	public float	imageWidth, imageHeight;

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
	public float	maxYIntensity;

	
	/**
	 * How this data should be transformed before being drawn
	 */
	public ViewTransform	viewTransform;
	
	
	/**
	 * The base unit size or scale for a single data point
	 */
	public float	unitSize;

	/**
	 * Indicates if the data should be presented in screen-orientation (0,0 at top left), or in cartesian orientation (0,0 at bottom left)
	 */
	public boolean screenOrientation;
	
	
	
	
	public DrawingRequest()
	{
		dataHeight = 1;
		dataWidth = 2048;

		imageHeight = 1;
		imageWidth = 1;

		drawToVectorSurface = true;

		maxYIntensity = -1;

		viewTransform = ViewTransform.LINEAR;
		unitSize = 10.0f;
		
		screenOrientation = true;
	}
	
	
	@Override
	public DrawingRequest clone()
	{
		try {
			return (DrawingRequest) super.clone();
		} catch (CloneNotSupportedException e) {
			CyclopsLog.get().log(Level.SEVERE, "Failed to clone Drawing Request", e);
			return null;
		}
	}

	
	//For JYAML Serialization Purposes -- Needs this to handle enums
	public String getViewTransform()
	{
		return viewTransform.name();
	}

	
	public void setViewTransform(String viewTransform)
	{
		this.viewTransform = ViewTransform.valueOf(viewTransform);
	}


}
