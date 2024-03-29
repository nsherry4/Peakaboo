package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces;

import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.png.PNGDescriptor;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.svg.SVGDescriptor;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceExporterRegistry;

public class CyclopsSurface {

	private CyclopsSurface() {}
	
	private static boolean initted = false;
	public static synchronized void init() {
		if (initted) return;
		initted = true;
		SurfaceExporterRegistry.registerExporter(new PNGDescriptor());
		SurfaceExporterRegistry.registerExporter(new SVGDescriptor());
	}
	
}
