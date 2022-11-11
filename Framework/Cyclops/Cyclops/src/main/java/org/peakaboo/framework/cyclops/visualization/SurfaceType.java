package org.peakaboo.framework.cyclops.visualization;

/**
 * 
 * SurfaceType enumerates the various available Surface types
 * 
 * @author Nathaniel Sherry, 2009
 * @see Surface
 *
 */

public enum SurfaceType {
	RASTER,
	VECTOR,
	;
	
	public String title() {
		return switch(this) {
		case RASTER -> "Pixel Image (PNG)";
		case VECTOR -> "Scalable Vector Graphic (SVG)";
		};
	}
	
	public String description() {
		return switch(this) {
		case RASTER -> "A grid of coloured dots with a fixed size and level of detail";
		case VECTOR -> "Defined by points, lines, and curves, they are scalable to any size";
		};
	}
	
}
