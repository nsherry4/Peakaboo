package cyclops.visualization.drawing.plot.painters.axis;

import java.util.List;
import java.util.function.Function;

import cyclops.Bounds;
import cyclops.Pair;
import cyclops.SigDigits;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.painters.axis.AxisPainter;
import cyclops.visualization.drawing.plot.PlotDrawing;


public class TickMarkAxisPainter extends AxisPainter
{
	
	public static class TickFormatter {
		public Float start, end;
		Function<Integer, String> formatter;
		
		public TickFormatter(float start, float end) {
			this(start, end, String::valueOf);
		}
		
		public TickFormatter(float start, float endstop, Function<Integer, String> formatter) {
			this.start = start;
			this.end = endstop;
			this.formatter = formatter;
		}
	}
	
	public TickFormatter yRightValueBounds, xBottomValueBounds, xTopValueBounds, yLeftValueBounds;
	
	public TickMarkAxisPainter(TickFormatter rightValueBounds, TickFormatter bottomValueBounds,
			TickFormatter topValueBounds, TickFormatter leftValueBounds, boolean yLeftLog, boolean yRightLog)
	{

		super();
		super.axesData.yLeftLog = yLeftLog;
		super.axesData.yRightLog = yRightLog;
		
		this.yRightValueBounds = rightValueBounds;
		this.yLeftValueBounds = leftValueBounds;
		this.xBottomValueBounds = bottomValueBounds;
		this.xTopValueBounds = topValueBounds;
				

	}

	
	private boolean showTickMarks = true;
	

	
	@Override
	public void drawElement(PainterData p)
	{
		drawTopXAxis(p);
		drawBottomXAxis(p);
		drawLeftYAxis(p);
		drawRightYAxis(p);
		
	}

	public void drawBottomXAxis(PainterData p)
	{
	
		if (this.xBottomValueBounds == null) return;
		if (this.xBottomValueBounds.end - this.xBottomValueBounds.start <= 0) return;
	
		p.context.save();
				
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
		
			Pair<Float, Float> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			float tickSize = getTickSize(getBaseUnitSize(p.dr), p.dr);
			float textHeight = getTickFontHeight(p.context, p.dr);
		
			float axisYStart = axesData.yPositionBounds.end - getAxisSizeY(p).second;
			float axisXStart = axesData.xPositionBounds.start + otherAxisSize.first;
			float axisWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
		
			float textBaseline = axisYStart + tickSize + textHeight;
		
			// calculate the maximum width of a text entry here
		
			//width of single entry
			int maxValue = (int) (this.xBottomValueBounds.end.floatValue());
			String text = this.xBottomValueBounds.formatter.apply(maxValue);
			float maxWidth = p.context.getTextWidth(text);
		
			//how many ticks we can fit and the range of values we're drawing over
			float maxTicks = axisWidth / (maxWidth * 3.0f);
			float valueRange = this.xBottomValueBounds.end - this.xBottomValueBounds.start;
		
					
			String tickText;
			float tickWidth;
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(this.xBottomValueBounds.start, increment);
			int currentValue = startingValue;
			float percentAlongAxis, position;
	
			while (currentValue <= this.xBottomValueBounds.end)
			{
				
				percentAlongAxis = (currentValue - this.xBottomValueBounds.start)  / valueRange;
				//Don't draw tick marks outside of the proper axis range
				if (percentAlongAxis >= 0 && percentAlongAxis <= 1) {
					position = axisXStart + (axisWidth * percentAlongAxis);
	
					AxisMarkGenerator.drawTick(p.context, position, axisYStart, position, axisYStart + tickSize);
			
					tickText = this.xBottomValueBounds.formatter.apply(currentValue);
					tickWidth = p.context.getTextWidth(tickText);
					p.context.writeText(tickText, position - (tickWidth / 2.0f), textBaseline);
				}
				
				currentValue += increment;
	
			}
	
		p.context.restore();
			
	
		
	}

	
	public void drawTopXAxis(PainterData p)
	{

		if (this.xTopValueBounds == null) return;
		
		p.context.save();
			
			/*============================================
			 * X Axis
			 *==========================================*/
		
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
			
			Pair<Float, Float> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			float tickSize = getTickSize(getBaseUnitSize(p.dr), p.dr);
			float textHeight = getTickFontHeight(p.context, p.dr);
	
			float axisWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxisSize.first - otherAxisSize.second;		
		
			float textBaseline = axesData.yPositionBounds.start + textHeight;
	
			// calculate the maximum width of a text entry here
	
			//width of single entry
			int maxValue = (int) (this.xTopValueBounds.end.floatValue());
			String text = this.xTopValueBounds.formatter.apply(maxValue);
			float maxWidth = p.context.getTextWidth(text);
	
			//how many ticks we can fit
			float maxTicks = axisWidth / (maxWidth * 3.0f);
			
			//the range of values we have to show 
			float valueRange = this.xTopValueBounds.end - this.xTopValueBounds.start;
			
			// we know how many we can fit on to the axis, what is the increment
			// size so we can be near, but not above that number of ticks
					
			String tickText;
			float tickWidth;
	
	
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(this.xTopValueBounds.start, increment);
			int currentValue = startingValue;
			
			if (showTickMarks) {
	
				float percentAlongAxis, position;
				while (currentValue <= this.xTopValueBounds.end)
				{
					
					percentAlongAxis = (currentValue - this.xTopValueBounds.start)  / valueRange;
					if (percentAlongAxis >= 0 && percentAlongAxis <= 1) {
						position = axesData.xPositionBounds.start + otherAxisSize.first + axisWidth * percentAlongAxis;
						
						AxisMarkGenerator.drawTick(p.context, position, textBaseline + tickSize * 0.5f, position, textBaseline + tickSize * 1.5f);
		
						tickText = this.xTopValueBounds.formatter.apply(currentValue);
						tickWidth = p.context.getTextWidth(tickText);
						p.context.writeText(tickText, position - (tickWidth / 2.0f), textBaseline);
					}
					
					
					currentValue += increment;
				}
			}
			
		p.context.restore();
		
	}
	

	public void drawLeftYAxis(PainterData p)
	{
		
		if (this.yLeftValueBounds == null) return;
	
		p.context.save();
			
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
		
			Pair<Float, Float> otherAxisSize = getAxisSizeY(p);
	
			float axisHeight = axesData.yPositionBounds.end - axesData.yPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
			float axisStart = axesData.xPositionBounds.start;
			float axisWidth = getAxisSizeX(p).first;
	
			float tickWidth = getTickSize(getBaseUnitSize(p.dr), p.dr);
			int maxTicks = AxisMarkGenerator.getMaxTicksY(p.context, axisHeight); //axisHeight / (textHeight*2.0);
			
			float valueRangeStart = this.yLeftValueBounds.start;
			float valueRangeEnd = PlotDrawing.getDataScale(this.yLeftValueBounds.end, false);
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
				
				if (axesData.yRightLog) {
					tickValue = (float)Math.exp(  (1.0 - percentAlongAxis) * (Math.log(valueRange) + 1.0)  ) - 1.0f;
					roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);
				} else {
					roundedTickValue = currentValue;
				}
				
				if (position - p.context.getFontAscent() / 2.0 > 0) {
					AxisMarkGenerator.drawTick(p.context, axisStart + axisWidth - tickWidth, position, axisStart + axisWidth, position);
					currentValueString = this.yLeftValueBounds.formatter.apply(roundedTickValue);
					textWidth = p.context.getTextWidth(currentValueString);
					p.context.writeText(currentValueString, axisStart + axisWidth - tickWidth*1.5f - textWidth, position + p.context.getFontAscent() / 2.0f);		
				}
				
			}
			
		p.context.restore();
	
	}


	public void drawRightYAxis(PainterData p)
	{
		
		if (this.yRightValueBounds == null) return;
		
		
		p.context.save();
			
			p.context.setSource(0,0,0);
			p.context.useMonoFont();
			
			Pair<Float, Float> otherAxisSize = getAxisSizeY(p);
	
			float axisHeight = axesData.yPositionBounds.end - axesData.yPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
			float axisWidth = getAxisSizeX(p).first;
			float axisStart = axesData.xPositionBounds.end - axisWidth;
	
			
			float tickWidth = getTickSize(getBaseUnitSize(p.dr), p.dr);
			int maxTicks = AxisMarkGenerator.getMaxTicksY(p.context, axisHeight); //axisHeight / (textHeight*2.0);
			
			float valueRangeStart = this.yRightValueBounds.start;
			float valueRangeEnd = PlotDrawing.getDataScale(this.yRightValueBounds.end, false);
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
				
				if (axesData.yRightLog) {
					tickValue = (float)Math.exp(  (1.0 - percentAlongAxis) * (Math.log(valueRange) + 1.0)  ) - 1.0f;
					roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);
				} else {
					roundedTickValue = currentValue;
				}
				
				if (position - p.context.getFontAscent() / 2.0 > 0) {
					AxisMarkGenerator.drawTick(p.context, axisStart, position, axisStart + tickWidth, position);
					currentValueString = this.yRightValueBounds.formatter.apply(roundedTickValue);
					p.context.writeText(currentValueString, axisStart + tickWidth*1.5f, position + p.context.getFontAscent() / 2.0f);			
				}
				
			}
			
		
		p.context.restore();
	}

	
	
	@Override
	public Pair<Float, Float> getAxisSizeX(PainterData p)
	{
	
		p.context.save();
			
			float baseSize = getBaseUnitSize(p.dr);
			float tickSize = getTickSize(baseSize, p.dr);
			float textWidth;
			
			float leftTextWidth = 0.0f;
			if (this.yLeftValueBounds != null){
				textWidth = p.context.getTextWidth(this.yLeftValueBounds.formatter.apply(  SigDigits.toIntSigDigit(this.yLeftValueBounds.start, 2)  ));
				if (textWidth > leftTextWidth) leftTextWidth = textWidth;
				
				textWidth = p.context.getTextWidth(this.yLeftValueBounds.formatter.apply(  SigDigits.toIntSigDigit(PlotDrawing.getDataScale(this.yLeftValueBounds.end, false), 2)  ));
				if (textWidth > leftTextWidth) leftTextWidth = textWidth;
				
				leftTextWidth += tickSize * 2.5f;
				
			}
			
			float rightTextWidth = 0.0f;
			if (this.yRightValueBounds != null){
				textWidth = p.context.getTextWidth(this.yRightValueBounds.formatter.apply(  SigDigits.toIntSigDigit(this.yRightValueBounds.start, 2)  ));
				if (textWidth > rightTextWidth) rightTextWidth = textWidth;
				
				textWidth = p.context.getTextWidth(this.yRightValueBounds.formatter.apply(  SigDigits.toIntSigDigit(PlotDrawing.getDataScale(this.yRightValueBounds.end, false), 2)  ));
				if (textWidth > rightTextWidth) rightTextWidth = textWidth;
				
				rightTextWidth += tickSize * 2.5;
				
			}
			
		p.context.restore();
		
		return new Pair<Float, Float>(  leftTextWidth, rightTextWidth  );
	}


	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p)
	{
		
		float baseSize = getBaseUnitSize(p.dr);
		float tickSize = getTickSize(baseSize, p.dr);
		float textHeight = getTickFontHeight(p.context, p.dr);
		
		
		float heightTop = tickSize*1.5f + textHeight;
		float heightBottom = tickSize + textHeight;
		
		if (this.xTopValueBounds == null) heightTop = 0.0f;
		if (this.xBottomValueBounds == null) heightBottom = 0.0f;
		
		
		return new Pair<Float, Float>(  heightTop, heightBottom  );
	}
	
	
	
	private int getStartingAxisValue(float valueStart, int increment)
	{
		return (int)(valueStart + (Math.abs(valueStart) % increment));
	}


	
}
