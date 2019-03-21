package org.peakaboo.framework.cyclops.visualization.backend.awt;

import java.awt.Graphics2D;

import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.SurfaceType;


class ScreenSurface extends AbstractGraphicsSurface
{

	public ScreenSurface(Graphics2D g)
	{
		super(g);
	}
	
	public Surface getNewContextForSurface()
	{
		return new ScreenSurface((Graphics2D)graphics.create());
	}

	public boolean isVectorSurface() {
		return false;
	}

	@Override
	public SurfaceType getSurfaceType() {
		return SurfaceType.RASTER;
	}
	
}
