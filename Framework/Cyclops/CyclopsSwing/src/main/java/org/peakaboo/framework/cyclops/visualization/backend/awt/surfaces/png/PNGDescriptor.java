package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.png;

import java.awt.image.BufferedImage;

import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.descriptor.AbstractSurfaceDescriptor;

public class PNGDescriptor extends AbstractSurfaceDescriptor {
	
	public PNGDescriptor() {
		super("Raster Image", "Pixel-perfect image with a fixed size and level of detail", "PNG");
	}

	@Override
	public boolean isVector() {
		return false;
	}

	@Override
	public Surface create(Coord<Integer> size) {
		return new PNGSurface(new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB), this);
	}

}
