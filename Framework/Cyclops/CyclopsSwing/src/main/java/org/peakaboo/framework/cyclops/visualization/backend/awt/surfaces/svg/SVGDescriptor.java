package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.svg;

import java.awt.Dimension;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.GenericDocument;
import org.apache.batik.svggen.SVGGraphics2D;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;

public class SVGDescriptor implements SurfaceDescriptor {

	@Override
	public Surface create(Coord<Integer> size) {
		return new SVGSurface(getVectorSurface(size.x, size.y), this);
	}

	@Override
	public boolean isVector() {
		return true;
	}

	@Override
	public String title() {
		return "Scalable Vector Graphic";
	}

	@Override
	public String description() {
		return "Defined by points, lines, and curves, they are scalable to any size";
	}

	@Override
	public String extension() {
		return "SVG";
	}
	
	private static SVGGraphics2D getVectorSurface(int width, int height) {

		// Get a DOMImplementation.
		GenericDOMImplementation domImpl = (GenericDOMImplementation) GenericDOMImplementation.getDOMImplementation();
		GenericDocument d = (GenericDocument) domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svg = new SVGGraphics2D(d);
		svg.setSVGCanvasSize(new Dimension(width, height));

		return svg;

	}

}
