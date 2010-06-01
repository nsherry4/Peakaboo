package peakaboo.drawing.backends;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * This factory contains the logic used to create a Surface from the preferred implementation
 * 
 * @author Nathaniel Sherry, 2009
 * 
 * @see Surface
 *
 */

public class DrawingSurfaceFactory
{

	/**
	 * This creates a Surface backed by the given backend source
	 * 
	 * @param backendSource
	 *            the backend to create this Surface around
	 * @return a new Surface object which will wrap the given backend source
	 * 
	 * @see Surface
	 */
	public static Surface createScreenSurface(Object backendSource)
	{
		if (backendSource instanceof Graphics) {
			return peakaboo.drawing.backends.graphics2d.Graphics2DDrawingSurfaceFactory.createScreenSurface(backendSource);
		}

		return null;
	}
	
	/**
	 * Creates a new surface of the given SurfaceType.
	 * 
	 * @param type
	 *            the type of surface to create
	 * @param width
	 *            the width of the new surface
	 * @param height
	 *            the height of the new surface
	 * @return a new Surface
	 * 
	 * @see Surface
	 * @see SurfaceType
	 */
	public static SaveableSurface createSaveableSurface(SurfaceType type, int width, int height)
	{
		return peakaboo.drawing.backends.graphics2d.Graphics2DDrawingSurfaceFactory.createSaveableSurface(type, width, height);
	}
	
	public static SaveableSurface createSaveableSurfaceFromG2DBufferedImage(BufferedImage image)
	{
		return peakaboo.drawing.backends.graphics2d.Graphics2DDrawingSurfaceFactory.createSaveableSurfaceFromG2DBufferedImage(image);
	}
	
	
}
