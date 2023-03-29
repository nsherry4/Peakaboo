package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.List;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxesData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;


public class TickMarkAxisPainter extends AxisPainter
{
	
	public TickFormatter yRightValueBounds, xBottomValueBounds, xTopValueBounds, yLeftValueBounds;
	
	public TickMarkAxisPainter(TickFormatter rightValueBounds, TickFormatter bottomValueBounds,
			TickFormatter topValueBounds, TickFormatter leftValueBounds)
	{

		super();
		
		this.yRightValueBounds = rightValueBounds;
		this.yLeftValueBounds = leftValueBounds;
		this.xBottomValueBounds = bottomValueBounds;
		this.xTopValueBounds = topValueBounds;
				

	}

	
	private boolean showTickMarks = true;
	

	
	@Override
	public void drawElement(PainterData p)
	{
		try {
		
		drawTopXAxis(p, xTopValueBounds);
		drawBottomXAxis(p, xBottomValueBounds);
		drawLeftYAxis(p, yLeftValueBounds);
		drawRightYAxis(p, yRightValueBounds);
		
		} catch (Exception e) {
			CyclopsLog.get().warning(e.getMessage());
			System.out.println(e);
		}
		
	}
	
	public void drawBottomXAxis(PainterData p, TickFormatter tick)
	{
	
		if (tick == null) return;
		if (tick.getEnd() - tick.getStart() <= 0) return;
	
		p.context.save();
				
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
		
			Pair<Float, Float> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			float tickLength = getTickLength(p.dr, tick);
			float textHeight = getTickFontHeight(p.context, p.dr);
			float textAscent = p.context.getFontAscent();
			
			float axisYStart = axesData.yPositionBounds.end - getAxisSizeY(p).second;
			float axisXStart = axesData.xPositionBounds.start + otherAxisSize.first;
			float axisWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
			float textBaseline = axisYStart + tickLength + textHeight;
		
				
			
			//how many ticks we can fit and the range of values we're drawing over
			float maxTicks = calcMaxTicksInternal(p, tick, axesData.xPositionBounds, otherAxisSize);
			
		
			
			
			float valueRange = tick.getEnd() - tick.getStart();
					
			String tickText;
			float tickWidth;
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(tick.getStart(), increment);
			int currentValue = startingValue;
			float percentAlongAxis, position;
	
			while (currentValue <= tick.getEnd())
			{
				
				percentAlongAxis = (currentValue - tick.getStart())  / valueRange;
				//Don't draw tick marks outside of the proper axis range
				if (percentAlongAxis >= 0 && percentAlongAxis <= 1) {
					position = axisXStart + (axisWidth * percentAlongAxis);
	
					AxisMarkGenerator.drawLine(p.context, position, axisYStart, position, axisYStart + tickLength);
			
					tickText = tick.format(currentValue);
					tickWidth = p.context.getTextWidth(tickText);
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
				
				currentValue += increment;
	
			}
	
		p.context.restore();
			
	
		
	}

	
	public void drawTopXAxis(PainterData p, TickFormatter tick)
	{

		if (tick == null) return;
		
		p.context.save();
			
			/*============================================
			 * X Axis
			 *==========================================*/
		
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
			
			Pair<Float, Float> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			float tickLength = getTickLength(p.dr, tick);
			float textHeight = getTickFontHeight(p.context, p.dr);
			float axisWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxisSize.first - otherAxisSize.second;		
			float axisYEnd = axesData.yPositionBounds.start + getAxisSizeY(p).first;
			float textAscent = p.context.getFontAscent();

			//width of single entry
			float maxTicks = calcMaxTicksInternal(p, tick, axesData.xPositionBounds, otherAxisSize);
			
			//the range of values we have to show 
			float valueRange = tick.getEnd() - tick.getStart();
			
			// we know how many we can fit on to the axis, what is the increment
			// size so we can be near, but not above that number of ticks
					
			String tickText;
			float tickWidth;
	
	
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(tick.getStart(), increment);
			int currentValue = startingValue;
			
			if (showTickMarks) {
	
				float percentAlongAxis, position;
				while (currentValue <= tick.getEnd())
				{
					
					percentAlongAxis = (currentValue - tick.getStart())  / valueRange;
					if (percentAlongAxis >= 0 && percentAlongAxis <= 1) {
						position = axesData.xPositionBounds.start + otherAxisSize.first + axisWidth * percentAlongAxis;
						
						AxisMarkGenerator.drawLine(p.context, position, axisYEnd, position, axisYEnd - tickLength);
						float textBaseline = axisYEnd - tickLength*1.5f;
						
						tickText = tick.format(currentValue);
						tickWidth = p.context.getTextWidth(tickText);
						
						if (tick.isTextRotated()) {
							float tx = position;
							float ty = textBaseline;
							float px = 0;//-tickWidth - tickLength * 0.5f;
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
					
					
					currentValue += increment;
				}
			}
			
		p.context.restore();
		
	}
	

	public void drawLeftYAxis(PainterData p, TickFormatter tick)
	{
		
		if (tick == null) return;
	
		p.context.save();
			
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
		
			Pair<Float, Float> otherAxisSize = getAxisSizeY(p);
	
			float axisHeight = axesData.yPositionBounds.end - axesData.yPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
			float axisStart = axesData.xPositionBounds.start;
			float axisWidth = getAxisSizeX(p).first;
			float tickLength = getTickLength(p.dr, tick);
			float maxTicks = calcMaxTicksInternal(p, tick, axesData.yPositionBounds, otherAxisSize);
			
			float valueRangeStart = tick.getStart();
			float valueRangeEnd = PlotDrawing.getDataScale(tick.getEnd(), tick.isLog(), tick.isPadded());	
			if (tick.isLog()) {
				valueRangeEnd = (float) Math.exp(valueRangeEnd);						
			}
			float valueRange = valueRangeEnd - valueRangeStart;
			
			
			List<Pair<Float, Integer>> tickLocations = AxisMarkGenerator.getAxisMarkList(maxTicks, axisHeight, 1, valueRangeStart, valueRangeEnd);
		
			float position, percentAlongAxis, textWidth, tickValue;
			int currentValue, roundedTickValue;
			String currentValueString;
			for (Pair<Float, Integer> tickData : tickLocations){
				
				percentAlongAxis = tickData.first;
				if (percentAlongAxis < 0 || percentAlongAxis > 1) { continue; }
				currentValue = tickData.second;
				
				position = axesData.yPositionBounds.start + otherAxisSize.first + axisHeight * percentAlongAxis;
				
				if (tick.isLog()) {
					tickValue = (float)Math.exp(  (1.0 - percentAlongAxis) * Math.log1p(valueRange)  ) - 1.0f;
					roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);					
				} else {
					roundedTickValue = currentValue;
				}
				
				if (position - p.context.getFontAscent() / 2.0 > 0) {
					AxisMarkGenerator.drawLine(p.context, axisStart + axisWidth - tickLength, position, axisStart + axisWidth, position);
					currentValueString = tick.format(roundedTickValue);
					textWidth = p.context.getTextWidth(currentValueString);
					

					
					if (!tick.isTextRotated()) {
						float tx = axisStart + axisWidth - tickLength*1.5f;
						float ty = position;
						float px = -textWidth/2f;
						float py = 0;
						p.context.save();
						p.context.translate(tx, ty);
						p.context.rotate(-1.57f);					
						p.context.writeText(currentValueString, px, py);
						p.context.restore();
					} else {
						float xTextPos = axisStart + axisWidth - tickLength*1.5f - textWidth;
						float yTextPos = position + p.context.getFontAscent() / 2.0f;
						p.context.writeText(currentValueString, xTextPos, yTextPos);
					}
							
				}

			}
			
		p.context.restore();
	
	}


	public void drawRightYAxis(PainterData p, TickFormatter tick)
	{
		
		if (tick == null) return;
		
		
		p.context.save();
			
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
			
			Pair<Float, Float> otherAxisSize = getAxisSizeY(p);
	
			float axisHeight = axesData.yPositionBounds.end - axesData.yPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
			float axisWidth = getAxisSizeX(p).first;
			float axisStart = axesData.xPositionBounds.end - axisWidth;
	
			
			float tickLength = getTickLength(p.dr, tick);
			float maxTicks = calcMaxTicksInternal(p, tick, axesData.yPositionBounds, otherAxisSize);
			
			float valueRangeStart = tick.getStart();
			float valueRangeEnd = PlotDrawing.getDataScale(tick.getEnd(), tick.isLog(), tick.isPadded());	
			if (tick.isLog()) {
				valueRangeEnd = (float) Math.exp(valueRangeEnd);						
			}
			float valueRange = valueRangeEnd - valueRangeStart;
			
			List<Pair<Float, Integer>> tickLocations = AxisMarkGenerator.getAxisMarkList(maxTicks, axisHeight, 1, valueRangeStart, valueRangeEnd);
			
			
			float position, percentAlongAxis, tickValue;
			int currentValue, roundedTickValue;
			String currentValueString;
			for (Pair<Float, Integer> tickData : tickLocations){
				
				percentAlongAxis = tickData.first;
				if (percentAlongAxis < 0 || percentAlongAxis > 1) { continue; }
				currentValue = tickData.second;
				
				position = axesData.yPositionBounds.start + otherAxisSize.first + axisHeight * percentAlongAxis;
				
				if (tick.isLog()) {
					tickValue = (float)Math.exp(  (1.0 - percentAlongAxis) * Math.log1p(valueRange)  ) - 1.0f;
					roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);	
				} else {
					roundedTickValue = currentValue;
				}
				
				if (position - p.context.getFontAscent() / 2.0 > 0) {
					AxisMarkGenerator.drawLine(p.context, axisStart, position, axisStart + tickLength, position);
					currentValueString = tick.format(roundedTickValue);
					float textWidth = p.context.getTextWidth(currentValueString);
					
					if (!tick.isTextRotated()) {
						float tx = axisStart + axisWidth - tickLength*2.5f;
						float ty = position;
						float px = -textWidth/2f;
						float py = 0;
						p.context.save();
						p.context.translate(tx, ty);
						p.context.rotate(1.57f);					
						p.context.writeText(currentValueString, px, py);
						p.context.restore();
					} else {
						p.context.writeText(currentValueString, axisStart + tickLength*1.5f, position + p.context.getFontAscent() / 2.0f);
					}
				}
				
			}
			
		
		p.context.restore();
	}

	
	@Override
	public Pair<Float, Float> getAxisSizeX(PainterData p) {		
		return new Pair<Float, Float>(  getSingleAxisSize(p, yLeftValueBounds, true), getSingleAxisSize(p, yRightValueBounds, true)  );
	}

	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p) {
		return new Pair<Float, Float>(  getSingleAxisSize(p, xTopValueBounds, false), getSingleAxisSize(p, xBottomValueBounds, false)  );
	}
	
	private float getSingleAxisSize(PainterData p, TickFormatter tick, boolean vertical) {
		
		p.context.save();
		
		float textHeight = getTickFontHeight(p.context, p.dr);
		float textWidth = 0f;
		if (tick != null){
			if (!tick.isTextRotated()) {
				textWidth = getTickLength(p.dr, tick) + textHeight;
			} else {
				float startTextWidth = p.context.getTextWidth(tick.format(  SigDigits.toIntSigDigit(tick.getStart(), 2)  ));			
				float endTextWidth = p.context.getTextWidth(tick.format(  SigDigits.toIntSigDigit(PlotDrawing.getDataScale(tick.getEnd(), false, tick.isPadded()), 2)  ));
				textWidth = Math.max(startTextWidth, endTextWidth);
				
				textWidth += getTickLength(p.dr, tick) * 2.5;
			}
			
		} else {
			textWidth = 0;
		}
		
		p.context.restore();
		
		return textWidth;
	}
	
	/**
	 * Because we're calculating the number of ticks for an axis painter, we need to
	 * calculate the amount of space <i>inside</i> the axis painter, rather than the
	 * free space that it itself has. This method calculates that value and then
	 * calls {@link TickFormatter#calcMaxTicks(PainterData, float)} with it.
	 */
	public static float calcMaxTicksInternal(PainterData p, TickFormatter tick, Bounds<Float> axisBounds, Pair<Float, Float> otherAxisSize) {
		float freeSpace = axisBounds.end - axisBounds.start - otherAxisSize.first - otherAxisSize.second;
		return tick.calcMaxTicks(p, freeSpace);
	}
	
	
	private int getStartingAxisValue(float valueStart, int increment)
	{
		return (int)(valueStart + (Math.abs(valueStart) % increment));
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


	private static float getTickFontHeight(Surface context, DrawingRequest dr)
	{
		return context.getFontHeight();
	}



	
}
