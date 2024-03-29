package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.graphics;

import java.awt.Graphics2D;

import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.AbstractGraphicsSurface;


public class ScreenSurface extends AbstractGraphicsSurface
{

	public ScreenSurface(Graphics2D g) {
		super(g, new ScreenDescriptor());
	}
	
	public Surface getNewContextForSurface() {
		return new ScreenSurface((Graphics2D)graphics.create());
	}

	public boolean isVectorSurface() {
		return false;
	}

	
}
