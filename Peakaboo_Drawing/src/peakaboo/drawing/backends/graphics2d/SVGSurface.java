package peakaboo.drawing.backends.graphics2d;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.svggen.SVGGraphics2D;

import peakaboo.drawing.backends.SaveableSurface;
import peakaboo.drawing.backends.Surface;

class SVGSurface extends AbstractGraphicsSurface implements SaveableSurface 
{

	private SVGGraphics2D svgGraphics;
	
	public SVGSurface(SVGGraphics2D g)
	{
		super(g);
		svgGraphics = g;
	}

	public void write(OutputStream out) throws IOException
	{
		svgGraphics.stream(new OutputStreamWriter(out));
		
	}

	public Surface getNewContextForSurface()
	{
		return new SVGSurface((SVGGraphics2D)svgGraphics.create());
	}

}
