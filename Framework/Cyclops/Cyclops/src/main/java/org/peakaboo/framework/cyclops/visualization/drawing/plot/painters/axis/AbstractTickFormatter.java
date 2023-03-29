package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

public abstract class AbstractTickFormatter implements TickFormatter {

	private boolean log = false;
	private boolean pad = false;
	// Rotates text 90 degrees so that it is running perpendicular to the axis instead of parallel
	private boolean textRotate = false;
	// Percentage value where 1 = 100%. Used to scale the size of tickmarks, with 1 being 100%
	private float tickScale = 1f;
	

	@Override
	public AbstractTickFormatter withLog(boolean log) {
		this.log = log;
		return this;
	}
	
	@Override
	public boolean isLog() {
		return log;
	}
	
	/**
	 * Accepts a float between 0 and 1, where 1 represents full size (100%)
	 */
	@Override
	public AbstractTickFormatter withTickSize(float percent) {
		this.tickScale = percent;
		return this;
	}

	@Override
	public float getTickSize() {
		return tickScale;
	}

	
	
	@Override
	public AbstractTickFormatter withRotate(boolean rotate) {
		this.textRotate = rotate;
		return this;
	}
	
	@Override
	public boolean isTextRotated() {
		return textRotate;
	}
	
	
	@Override
	public boolean isPadded() {
		return pad;
	}
	
	@Override
	public AbstractTickFormatter withPad(boolean pad) {
		this.pad = pad;
		return this;
	}
	


}
