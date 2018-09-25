package cyclops.visualization.backend.awt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.svggen.SVGGraphics2D;

import cyclops.visualization.SaveableSurface;
import cyclops.visualization.Surface;

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

	public boolean isVectorSurface() {
		return true;
	}

}
