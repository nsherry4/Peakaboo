package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.logging.Level;

import org.peakaboo.framework.accent.numeric.Bounds;
import org.peakaboo.framework.accent.Pair;
import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;


public class TickMarkAxisPainter extends AxisPainter
{
	
	public TickFormatter yRightValueBounds, xBottomValueBounds, xTopValueBounds, yLeftValueBounds;
	private PaletteColour colour;
	
	public TickMarkAxisPainter(PaletteColour colour, TickFormatter rightValueBounds, TickFormatter bottomValueBounds,
			TickFormatter topValueBounds, TickFormatter leftValueBounds)
	{

		super();
		
		this.yRightValueBounds = rightValueBounds;
		this.yLeftValueBounds = leftValueBounds;
		this.xBottomValueBounds = bottomValueBounds;
		this.xTopValueBounds = topValueBounds;
		this.colour = colour;
				

	}

	@Override
	public void drawElement(PainterData p)
	{
		try {
		
		drawTopXAxis(p, xTopValueBounds);
		drawBottomXAxis(p, xBottomValueBounds);
		drawLeftYAxis(p, yLeftValueBounds);
		drawRightYAxis(p, yRightValueBounds);
		
		} catch (Exception e) {
			CyclopsLog.get().log(Level.WARNING, "Failed to draw element", e);
		}
		
	}
	
	
	public void drawBottomXAxis(PainterData p, TickFormatter tick) {
	
		if (tick == null) return;
		if (tick.isEmpty()) return;
	
		p.context.save();
				
			p.context.setSource(this.colour);
			p.context.useMonoFont();
		
			Pair<Float, Float> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			float tickLength = getTickLength(p.dr, tick);
			float textHeight = getTickFontHeight(p.context);
			float textAscent = p.context.getFontAscent();
			
			float axisYStart = axesData.yPositionBounds.end - getAxisSizeY(p).second;
			float axisXStart = axesData.xPositionBounds.start + otherAxisSize.first;
			float textBaseline = axisYStart + tickLength + textHeight;
		

			
			var axisLength = axisSize(axesData.xPositionBounds, otherAxisSize);
			for (var mark : tick.getTickMarks(p, axisLength, false)) {
				if (mark.minor()) {
					continue;
				}
				float percentAlongAxis = mark.position();
				if (percentAlongAxis < 0f || percentAlongAxis > 1f) {
					continue;
				}
				String tickText = mark.value();
			
				float position = (float) Math.ceil(axisXStart + (axisLength * percentAlongAxis));
				drawTickLine(p.context, position, axisYStart, position, axisYStart + tickLength);
		
				float tickWidth = p.context.getTextWidth(tickText);
				if (tick.isTextRotated()) {
					float tx = position;
					float ty = textBaseline - textAscent;
					float px = -tickWidth - tickLength * 0.5f;
					float py = textAscent/2f;
					p.context.save();
					p.context.translate(tx, ty);
					p.context.rotate(-1.57f);					
					p.context.writeText(tickText, px, py);
					p.context.restore();
				} else {
					p.context.writeText(tickText, position - (tickWidth / 2.0f), textBaseline);
				}				
			}
			
		p.context.restore();
			
		
	}

	
	public void drawTopXAxis(PainterData p, TickFormatter tick) {

		if (tick == null) return;
		if (tick.isEmpty()) return;
		
		p.context.save();
			
			p.context.setSource(this.colour);
			p.context.useMonoFont();
			
			Pair<Float, Float> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			float tickLength = getTickLength(p.dr, tick);
			float textAscent = p.context.getFontAscent();

			float axisYEnd = axesData.yPositionBounds.start + getAxisSizeY(p).first;
			float axisXStart = axesData.xPositionBounds.start + otherAxisSize.first;
			float textBaseline = axisYEnd - tickLength*1.5f;
			
			
			
			var axisLength = axisSize(axesData.xPositionBounds, otherAxisSize);
			for (var mark : tick.getTickMarks(p, axisLength, false)) {
				if (mark.minor()) {
					continue;
				}
				float percentAlongAxis = mark.position();
				if (percentAlongAxis < 0f || percentAlongAxis > 1f) {
					continue;
				}
				String tickText = mark.value();
				
				float position = axisXStart + axisLength * percentAlongAxis;
				drawTickLine(p.context, position, axisYEnd, position, axisYEnd - tickLength);
				
				float tickWidth = p.context.getTextWidth(tickText);
				
				if (tick.isTextRotated()) {
					float tx = position;
					float ty = textBaseline;
					float px = 0;
					float py = textAscent/2f;
					p.context.save();
					p.context.translate(tx, ty);
					p.context.rotate(-1.57f);					
					p.context.writeText(tickText, px, py);
					p.context.restore();
				} else {
					p.context.writeText(tickText, position - (tickWidth / 2.0f), textBaseline);
				}
				
			}
			
			
		p.context.restore();
		
	}
	

	public void drawLeftYAxis(PainterData p, TickFormatter tick) {
		
		if (tick == null) return;
		if (tick.isEmpty()) return;	
		
		p.context.save();
			
			p.context.setSource(this.colour);
			p.context.useMonoFont();
		
			Pair<Float, Float> otherAxisSize = getAxisSizeY(p);
	
			float axisStart = axesData.xPositionBounds.start;
			float axisWidth = getAxisSizeX(p).first;
			float tickLength = getTickLength(p.dr, tick);
			
			//TODO backwards? instead of figuring out what positions to draw at and then asking for a number, we should
			// be figuring out what numbers we want to draw and then translating them into drawing coords
			// Maybe the tick mark formatter should do that?
			var axisLength = axisSize(axesData.yPositionBounds, otherAxisSize);
			for (var mark : tick.getTickMarks(p, axisLength, false)) {
				if (mark.minor()) {
					continue;
				}
				float percentAlongAxis = 1f - mark.position();
				if (percentAlongAxis < 0f || percentAlongAxis > 1f) {
					continue;
				}
				String tickText = mark.value();
				
				Bounds<Float> drawRangeY = new Bounds<>(
						axesData.yPositionBounds.start + otherAxisSize.first, 
						axesData.yPositionBounds.end - otherAxisSize.second
					);
				float drawHeight = drawRangeY.end - drawRangeY.start;
				float yPosition = (float) Math.floor(drawRangeY.start + percentAlongAxis * drawHeight);

				
				if (yPosition - p.context.getFontAscent() / 2.0 > 0) {
					drawTickLine(p.context, axisStart + axisWidth - tickLength, yPosition, axisStart + axisWidth, yPosition);
					float textWidth = p.context.getTextWidth(tickText);

					if (!tick.isTextRotated()) {
						float tx = axisStart + axisWidth - tickLength*1.5f;
						float ty = yPosition;
						float px = -textWidth/2f;
						float py = 0;
						p.context.save();
						p.context.translate(tx, ty);
						p.context.rotate(-1.57f);					
						p.context.writeText(tickText, px, py);
						p.context.restore();
					} else {
						float xTextPos = axisStart + axisWidth - tickLength*1.5f - textWidth;
						float yTextPos = yPosition + p.context.getFontAscent() / 2.0f;
						p.context.writeText(tickText, xTextPos, yTextPos);
					}
							
				}
			
			}
			
			
		p.context.restore();
	
	}


	public void drawRightYAxis(PainterData p, TickFormatter tick) {
		
		if (tick == null) return;
		if (tick.isEmpty()) return;
		
		
		p.context.save();
			
			p.context.setSource(this.colour);
			p.context.useMonoFont();
			
			Pair<Float, Float> otherAxisSize = getAxisSizeY(p);
	
			float axisWidth = getAxisSizeX(p).first;
			float axisStart = axesData.xPositionBounds.end - axisWidth;
			float tickLength = getTickLength(p.dr, tick);
			
			
			var axisLength = axisSize(axesData.yPositionBounds, otherAxisSize);
			for (var mark : tick.getTickMarks(p, axisLength, false)) {
				if (mark.minor()) {
					continue;
				}
				float percentAlongAxis = 1f - mark.position();
				if (percentAlongAxis < 0f || percentAlongAxis > 1f) {
					continue;
				}
				String tickText = mark.value();
							
				Bounds<Float> drawRangeY = new Bounds<>(
						axesData.yPositionBounds.start + otherAxisSize.first, 
						axesData.yPositionBounds.end - otherAxisSize.second
					);
				float drawHeight = drawRangeY.end - drawRangeY.start;
				float yPosition = (float) Math.floor(drawRangeY.start + percentAlongAxis * drawHeight);
				
				if (yPosition - p.context.getFontAscent() / 2.0 > 0) {
					drawTickLine(p.context, axisStart, yPosition, axisStart + tickLength, yPosition);
					float textWidth = p.context.getTextWidth(tickText);
					
					if (!tick.isTextRotated()) {
						float tx = axisStart + axisWidth - tickLength*2.5f;
						float ty = yPosition;
						float px = -textWidth/2f;
						float py = 0;
						p.context.save();
						p.context.translate(tx, ty);
						p.context.rotate(1.57f);					
						p.context.writeText(tickText, px, py);
						p.context.restore();
					} else {
						p.context.writeText(tickText, axisStart + tickLength*1.5f, yPosition + p.context.getFontAscent() / 2.0f);
					}
				}
				
			}
			
		
		p.context.restore();
	}

	
	@Override
	public Pair<Float, Float> getAxisSizeX(PainterData p) {		
		return new Pair<>(  getSingleAxisSize(p, yLeftValueBounds), getSingleAxisSize(p, yRightValueBounds)  );
	}

	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p) {
		return new Pair<>(  getSingleAxisSize(p, xTopValueBounds), getSingleAxisSize(p, xBottomValueBounds)  );
	}
	
	
	public static void drawTickLine(Surface context, float startx, float starty, float endx, float endy) {
		context.setAntialias(false);
		context.moveTo(startx,  starty);
		context.lineTo(endx, endy);
		context.stroke();
		context.setAntialias(true);
	}
	
	private float getSingleAxisSize(PainterData p, TickFormatter tick) {
		
		p.context.save();
		
		float textWidth;
		if (tick != null){
			var maxsize = tick.maxTextSize(p);
			if (!tick.isTextRotated()) {
				textWidth = getTickLength(p.dr, tick) + maxsize.height();
			} else {
				textWidth = maxsize.width();
				textWidth += getTickLength(p.dr, tick) * 2.5;
			}
			
		} else {
			textWidth = 0;
		}
		
		p.context.restore();
		
		return textWidth;
	}
		
	
	
	private float getTickLength(DrawingRequest dr, TickFormatter tick)
	{
		float baseSize = getBaseUnitSize(dr);
		float scale = 5;
		if (tick != null) {
			scale *= tick.getTickSize();
		}
		return baseSize * scale;
	}


	private static float getTickFontHeight(Surface context)
	{
		return context.getFontHeight();
	}

	
	private static float axisSize(Bounds<Float> axisBounds, Pair<Float, Float> otherAxisSize) {
		return axisBounds.end - axisBounds.start - otherAxisSize.first - otherAxisSize.second;
	}

	

	
}
