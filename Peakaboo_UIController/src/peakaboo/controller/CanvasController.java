package peakaboo.controller;


import java.io.IOException;
import java.io.OutputStream;

import scidraw.drawing.backends.DrawingSurfaceFactory;
import scidraw.drawing.backends.SaveableSurface;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.backends.SurfaceType;
import eventful.EventfulType;

/**
 * 
 * This is an abstract class for controllers which draw something using a supported graphics backend. The given graphics context will be wrapped in a related {@link Surface} to be drawn to
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class CanvasController extends EventfulType<String>
{


	protected Surface	toyContext;


	public CanvasController(Object toyContext)
	{
		this.toyContext = DrawingSurfaceFactory.createScreenSurface(toyContext);
	}


	public void draw(Object drawContext)
	{
		drawBackend(DrawingSurfaceFactory.createScreenSurface(drawContext), false);
	}


	public void writePNG(OutputStream out) throws IOException
	{
		write(SurfaceType.RASTER, out);
	}


	public void writeSVG(OutputStream out) throws IOException
	{
		write(SurfaceType.VECTOR, out);
	}


	public void writePDF(OutputStream out) throws IOException
	{
		write(SurfaceType.PDF, out);
	}


	private void write(SurfaceType type, OutputStream out) throws IOException
	{

		boolean vector = false;
		if (type == SurfaceType.PDF || type == SurfaceType.VECTOR) vector = true;

		SaveableSurface b = DrawingSurfaceFactory.createSaveableSurface(type, (int) getUsedWidth(),
				(int) getUsedHeight());
		drawBackend(b, vector);
		b.write(out);
	}


	public abstract void setOutputIsPDF(boolean isPDF);


	protected abstract void drawBackend(Surface backend, boolean vector);


	public abstract float getUsedWidth();


	public abstract float getUsedHeight();

}
