package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.AbstractImageSurface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;

class PNGSurface extends AbstractImageSurface {

	PNGSurface(BufferedImage image, SurfaceDescriptor descriptor) {
		super(image, descriptor);
	}

	public void write(OutputStream out) throws IOException {
		graphics.dispose();
		ImageIO.write(image, "png", out);
	}

	public Surface getNewContextForSurface() {
		return new PNGSurface(image, descriptor);
	}

	
}
