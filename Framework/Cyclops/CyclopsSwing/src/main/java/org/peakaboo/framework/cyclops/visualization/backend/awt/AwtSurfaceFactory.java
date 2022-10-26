package org.peakaboo.framework.cyclops.visualization.backend.awt;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.GenericDocument;
import org.apache.batik.svggen.SVGGraphics2D;
import org.peakaboo.framework.cyclops.visualization.SaveableSurface;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.SurfaceType;



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
public class AwtSurfaceFactory
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
		}

		return null;

	}
	

	private static SVGGraphics2D getScalarSurface(int width, int height)
	{

		// Get a DOMImplementation.
		GenericDOMImplementation domImpl = (GenericDOMImplementation) GenericDOMImplementation.getDOMImplementation();

		GenericDocument d = (GenericDocument) domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svg = new SVGGraphics2D(d);

		svg.setSVGCanvasSize(new Dimension(width, height));

		return svg;

	}

}
