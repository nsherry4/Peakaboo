package org.peakaboo.framework.cyclops.visualization.descriptor;

import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;

public interface SurfaceDescriptor {

	public Surface create(Coord<Integer> size);
	public boolean isVector();
	public String title();
	public String description();
	public String extension();
	
}
