package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.graphics;

import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;

public class ScreenDescriptor implements SurfaceDescriptor {

	@Override
	public Surface create(Coord<Integer> size) {
		throw new UnsupportedOperationException("Create ScreenSurfaces directly");
	}

	@Override
	public boolean isVector() {
		return false;
	}

	@Override
	public String title() {
		return "Screen";
	}

	@Override
	public String description() {
		return "Swing Graphics2D Output";
	}

	@Override
	public String extension() {
		return "---";
	}

}
