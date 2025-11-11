package org.peakaboo.framework.cyclops.visualization.drawing.map.painters.axis;

import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.accent.Pair;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;



public class CornersAxisPainter extends AxisPainter {

	protected String textLoXLoY, textHiXLoY, textLoXHiY, textHiXHiY;
	protected static Coord<Float> coordPadding = new Coord<>(3.0f, 3.0f);
	protected PaletteColour colour;
	
	public CornersAxisPainter(PaletteColour colour, String textLoXLoY, String textHiXLoY, String textLoXHiY, String textHiXHiY) {
		super();

		this.colour = colour;
		this.textLoXLoY = textLoXLoY;
		this.textHiXLoY = textHiXLoY;
		this.textLoXHiY = textLoXHiY;
		this.textHiXHiY = textHiXHiY;

	}

	@Override
	public Pair<Float, Float> getAxisSizeX(PainterData p) {
		return new Pair<>(0f, 0f);
	}


	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p) {
		p.context.save();
		p.context.setFontSize(getCoordFontSize(p));
		float height = p.context.getFontHeight();
		p.context.restore();
		return new Pair<>(height, height);
	}


	@Override
	public void drawElement(PainterData p) {

		float mapLoX, mapLoY, mapHiX, mapHiY;
		if (p.dr.screenOrientation) {
			mapLoX = axesData.xPositionBounds.start;
			mapLoY = axesData.yPositionBounds.start;
			mapHiX = axesData.xPositionBounds.end;
			mapHiY = axesData.yPositionBounds.end;
		} else {
			mapLoX = axesData.xPositionBounds.start;
			mapHiY = axesData.yPositionBounds.start; 
			mapHiX = axesData.xPositionBounds.end;
			mapLoY = axesData.yPositionBounds.end;
		}
			
		drawCoordinatePair(p, textLoXLoY, mapLoX, mapLoY, false, false);
		drawCoordinatePair(p, textHiXLoY, mapHiX, mapLoY, true, false);

		drawCoordinatePair(p, textLoXHiY, mapLoX, mapHiY, false, true);
		drawCoordinatePair(p, textHiXHiY, mapHiX, mapHiY, true, true);
	}


	private void drawCoordinatePair(PainterData p, String text, float x, float y, boolean rightAlign, boolean topAlign) {
		p.context.save();
		float textX;
		float textY;
		p.context.setSource(colour);
		p.context.setFontSize(getCoordFontSize(p));
		textX = x - (rightAlign ? p.context.getTextWidth(text) + coordPadding.x : -coordPadding.x);
		textY = y - coordPadding.y + (topAlign ? p.context.getFontHeight() : 0);
		if (text == null) { text = ""; }
		p.context.writeText(text, textX, textY);
		p.context.restore();
	}


	private float getCoordFontSize(PainterData p) {
		return p.context.getFontSize() - 2;
	}


}