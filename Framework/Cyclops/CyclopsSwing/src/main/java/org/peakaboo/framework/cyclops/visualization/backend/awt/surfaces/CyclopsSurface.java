package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces;

import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.png.PNGDescriptor;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.svg.SVGDescriptor;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceExporterRegistry;

public class CyclopsSurface {

	private static boolean inited = false;
	public static synchronized void init() {
		inited = true;
		SurfaceExporterRegistry.registerExporter(new PNGDescriptor());
		SurfaceExporterRegistry.registerExporter(new SVGDescriptor());
	}
	
}
