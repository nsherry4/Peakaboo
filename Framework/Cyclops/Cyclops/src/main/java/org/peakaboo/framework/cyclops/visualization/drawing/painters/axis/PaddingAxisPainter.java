package org.peakaboo.framework.cyclops.visualization.drawing.painters.axis;

import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;

public class PaddingAxisPainter extends AxisPainter {

	private float top, right, bottom, left;
	
	public PaddingAxisPainter(float pad) {
		this(pad, pad, pad, pad);
	}
	
	public PaddingAxisPainter(float top, float right, float bottom, float left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
	
	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p) {
		return new Pair<Float, Float>(top, bottom);
	}

	@Override
	public Pair<Float, Float> getAxisSizeX(PainterData p) {
		return new Pair<Float, Float>(left, right);
	}

	@Override
	public void drawElement(PainterData p) {
		//NOOP
	}

}
