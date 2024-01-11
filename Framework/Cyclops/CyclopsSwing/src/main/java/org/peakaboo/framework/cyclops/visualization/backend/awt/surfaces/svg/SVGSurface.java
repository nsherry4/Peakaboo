package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.svg;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.svggen.SVGGraphics2D;
import org.peakaboo.framework.cyclops.visualization.ExportableSurface;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.AbstractGraphicsSurface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;

class SVGSurface extends AbstractGraphicsSurface implements ExportableSurface {

	private SVGGraphics2D svgGraphics;
	
	public SVGSurface(SVGGraphics2D g, SurfaceDescriptor descriptor) {
		super(g, descriptor);
		svgGraphics = g;
	}

	@Override
	public void write(OutputStream out) throws IOException
	{
		svgGraphics.stream(new OutputStreamWriter(out));
		
	}

	@Override
	public Surface getNewContextForSurface() {
		return new SVGSurface((SVGGraphics2D)svgGraphics.create(), descriptor);
	}

	@Override
	public boolean isVectorSurface() {
		return true;
	}


}
