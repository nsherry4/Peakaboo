package peakaboo.drawing.backends.graphics2d;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import peakaboo.drawing.backends.SaveableSurface;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.backends.SurfaceType;

/**
 * @author Nathaniel Sherry, 2009
 * 
 *         This factory contains the logic for creating a Surface object based on the Graphics2D implementations
 * 
 * @see Surface
 * @see Graphics2D
 * 
 */

// import org.freedesktop.cairo.Surface;
public class Graphics2DDrawingSurfaceFactory
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
			return new ScreenSurface((Graphics2D) backendSource);
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

		switch (type) {

			case RASTER:

				return new ImageSurface(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
				

			case VECTOR:
				return new SVGSurface(getScalarSurface(width, height));

			case PDF:
				return new PDFSurface(getScalarSurface(width, height));


		}

		return null;

	}
	
	public static SaveableSurface createSaveableSurfaceFromG2DBufferedImage(BufferedImage image)
	{
		return new ImageSurface(image);
	}

	private static SVGGraphics2D getScalarSurface(int width, int height)
	{

		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		Document d = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svg = new SVGGraphics2D(d);

		svg.setSVGCanvasSize(new Dimension(width, height));

		return svg;

	}

}
