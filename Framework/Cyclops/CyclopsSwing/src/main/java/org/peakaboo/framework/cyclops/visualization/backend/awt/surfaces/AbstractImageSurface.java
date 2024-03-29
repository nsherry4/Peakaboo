package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces;

import java.awt.image.BufferedImage;

import org.peakaboo.framework.cyclops.visualization.ExportableSurface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;


public abstract class AbstractImageSurface extends AbstractGraphicsSurface implements ExportableSurface {

	protected BufferedImage image;
	
	protected AbstractImageSurface(BufferedImage image, SurfaceDescriptor descriptor) {
		super(image.createGraphics(), descriptor);
		this.image = image;
	}

	public boolean isVectorSurface() {
		return false;
	}

}
