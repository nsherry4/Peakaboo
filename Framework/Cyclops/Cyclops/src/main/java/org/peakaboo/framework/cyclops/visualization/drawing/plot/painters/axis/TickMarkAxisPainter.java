package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.List;
import java.util.function.Function;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;


public class TickMarkAxisPainter extends AxisPainter
{
	
	public static class TickFormatter {
		public Float start, end;
		Function<Integer, String> formatter;
		public boolean log = false;
		
		/**
		 * Rotates text 90 degrees so that it is running perpendicular to the axis instead of parallel
		 */
		public boolean textRotate = false;
		
		/**
		 * Percentage value where 1 = 100%. Used to scale the size of tickmarks, with 1 being 100%
		 */
		public float tickScale = 1f;
		
		
		public TickFormatter(float start, float end) {
			this(start, end, String::valueOf);
		}
		
		public TickFormatter(float start, float endstop, Function<Integer, String> formatter) {
			this.start = start;
			this.end = endstop;
			this.formatter = formatter;
		}
		
		public TickFormatter withLog(boolean log) {
			this.log = log;
			return this;
		}
		
		public TickFormatter withTick(float percent) {
			this.tickScale = percent;
			return this;
		}
		
		public TickFormatter withRotate(boolean rotate) {
			this.textRotate = rotate;
			return this;
		}
	}
	
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
		drawTopXAxis(p, xTopValueBounds);
		drawBottomXAxis(p, xBottomValueBounds);
		drawLeftYAxis(p, yLeftValueBounds);
		drawRightYAxis(p, yRightValueBounds);
		
	}
	
	public void drawBottomXAxis(PainterData p, TickFormatter tick)
	{
	
		if (tick == null) return;
		if (tick.end - tick.start <= 0) return;
	
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
			float maxTicks = AxisMarkGenerator.calcMaxTicks(p, tick, axesData.xPositionBounds, otherAxisSize);
			
		
			
			
			float valueRange = tick.end - tick.start;
					
			String tickText;
			float tickWidth;
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(tick.start, increment);
			int currentValue = startingValue;
			float percentAlongAxis, position;
	
			while (currentValue <= tick.end)
			{
				
				percentAlongAxis = (currentValue - tick.start)  / valueRange;
				//Don't draw tick marks outside of the proper axis range
				if (percentAlongAxis >= 0 && percentAlongAxis <= 1) {
					position = axisXStart + (axisWidth * percentAlongAxis);
	
					AxisMarkGenerator.drawLine(p.context, position, axisYStart, position, axisYStart + tickLength);
			
					tickText = tick.formatter.apply(currentValue);
					tickWidth = p.context.getTextWidth(tickText);
					if (tick.textRotate) {
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
			float maxTicks = AxisMarkGenerator.calcMaxTicks(p, tick, axesData.xPositionBounds, otherAxisSize);
			
			//the range of values we have to show 
			float valueRange = tick.end - tick.start;
			
			// we know how many we can fit on to the axis, what is the increment
			// size so we can be near, but not above that number of ticks
					
			String tickText;
			float tickWidth;
	
	
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(tick.start, increment);
			int currentValue = startingValue;
			
			if (showTickMarks) {
	
				float percentAlongAxis, position;
				while (currentValue <= tick.end)
				{
					
					percentAlongAxis = (currentValue - tick.start)  / valueRange;
					if (percentAlongAxis >= 0 && percentAlongAxis <= 1) {
						position = axesData.xPositionBounds.start + otherAxisSize.first + axisWidth * percentAlongAxis;
						
						AxisMarkGenerator.drawLine(p.context, position, axisYEnd, position, axisYEnd - tickLength);
						float textBaseline = axisYEnd - tickLength*1.5f;
						
						tickText = tick.formatter.apply(currentValue);
						tickWidth = p.context.getTextWidth(tickText);
						
						if (tick.textRotate) {
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
			float maxTicks = AxisMarkGenerator.calcMaxTicks(p, tick, axesData.yPositionBounds, otherAxisSize);
			
			float valueRangeStart = tick.start;
			float valueRangeEnd = PlotDrawing.getDataScale(tick.end, false);
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
				
				if (tick.log) {
					tickValue = (float)Math.exp(  (1.0 - percentAlongAxis) * (Math.log(valueRange) + 1.0)  ) - 1.0f;
					roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);
				} else {
					roundedTickValue = currentValue;
				}
				
				if (position - p.context.getFontAscent() / 2.0 > 0) {
					AxisMarkGenerator.drawLine(p.context, axisStart + axisWidth - tickLength, position, axisStart + axisWidth, position);
					currentValueString = tick.formatter.apply(roundedTickValue);
					textWidth = p.context.getTextWidth(currentValueString);
					

					
					if (!tick.textRotate) {
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
			float maxTicks = AxisMarkGenerator.calcMaxTicks(p, tick, axesData.yPositionBounds, otherAxisSize);
			
			float valueRangeStart = tick.start;
			float valueRangeEnd = PlotDrawing.getDataScale(tick.end, false);
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
				
				if (tick.log) {
					tickValue = (float)Math.exp(  (1.0 - percentAlongAxis) * (Math.log(valueRange) + 1.0)  ) - 1.0f;
					roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);
				} else {
					roundedTickValue = currentValue;
				}
				
				if (position - p.context.getFontAscent() / 2.0 > 0) {
					AxisMarkGenerator.drawLine(p.context, axisStart, position, axisStart + tickLength, position);
					currentValueString = tick.formatter.apply(roundedTickValue);
					float textWidth = p.context.getTextWidth(currentValueString);
					
					if (!tick.textRotate) {
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
			if (!tick.textRotate) {
				textWidth = getTickLength(p.dr, tick) + textHeight;
			} else {
				float startTextWidth = p.context.getTextWidth(tick.formatter.apply(  SigDigits.toIntSigDigit(tick.start, 2)  ));			
				float endTextWidth = p.context.getTextWidth(tick.formatter.apply(  SigDigits.toIntSigDigit(PlotDrawing.getDataScale(tick.end, false), 2)  ));
				textWidth = Math.max(startTextWidth, endTextWidth);
				
				textWidth += getTickLength(p.dr, tick) * 2.5;
			}
			
		} else {
			textWidth = 0;
		}
		
		p.context.restore();
		
		return textWidth;
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
			scale *= tick.tickScale;
		}
		return baseSize * scale;
	}


	private static float getTickFontHeight(Surface context, DrawingRequest dr)
	{
		return context.getFontHeight();
	}



	
}
