package peakaboo.drawing.plot.painters.axis;

import java.util.List;

import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.SigDigits;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.painters.axis.AxisPainter;
import peakaboo.drawing.plot.Plot;


public class TickMarkAxisPainter extends AxisPainter
{
	
	public Range<Double> yRightValueBounds, xBottomValueBounds, xTopValueBounds, yLeftValueBounds;
	
	public TickMarkAxisPainter(Range<Double> rightValueBounds, Range<Double> bottomValueBounds,
			Range<Double> topValueBounds, Range<Double> leftValueBounds, boolean yLeftLog, boolean yRightLog)
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
	
		p.context.save();
			
			Pair<Double, Double> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			double tickSize = getTickSize(getBaseUnitSize(p.dr), p.dr);
			double textHeight = getTickFontHeight(p.context, p.dr);
		
			double axisStart = axesData.yPositionBounds.end - getAxisSizeY(p).second;
			double axisWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxisSize.first - otherAxisSize.second;		
		
			double textBaseline = axisStart + tickSize + textHeight;
		
			// calculate the maximum width of a text entry here
			p.context.setSource(0.0, 0.0, 0.0);
			p.context.useMonoFont();
		
			//width of single entry
			int maxValue = (int) (this.xBottomValueBounds.end.doubleValue());
			String text = String.valueOf(maxValue);
			double maxWidth = p.context.getTextWidth(text);
		
			//how many ticks we can fit and the range of values we're drawing over
			double maxTicks = axisWidth / (maxWidth * 3.0);
			double valueRange = this.xBottomValueBounds.end - this.xBottomValueBounds.start;
		
					
			String tickText;
			double tickWidth;
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(this.xBottomValueBounds.start, increment);
			int currentValue = startingValue;
			double percentAlongAxis, position;
	
			while (currentValue < this.xBottomValueBounds.end)
			{
				
				percentAlongAxis = (currentValue - this.xBottomValueBounds.start)  / valueRange;
				position = axesData.xPositionBounds.start + otherAxisSize.first + axisWidth * percentAlongAxis;
				
				AxisMarkGenerator.drawTick(p.context, position, axisStart, position, axisStart + tickSize);
		
				tickText = String.valueOf(currentValue);
				tickWidth = p.context.getTextWidth(tickText);
				p.context.writeText(tickText, position - (tickWidth / 2.0), textBaseline);
				
				
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
		
			Pair<Double, Double> otherAxisSize = getAxisSizeX(p);
			
			// dimensions for various parts of the axis
			double tickSize = getTickSize(getBaseUnitSize(p.dr), p.dr);
			double textHeight = getTickFontHeight(p.context, p.dr);
	
			double axisWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxisSize.first - otherAxisSize.second;		
		
			double textBaseline = axesData.yPositionBounds.start + textHeight;
	
			// calculate the maximum width of a text entry here
			
			p.context.setSource(0.0, 0.0, 0.0);
			p.context.useMonoFont();
	
			//width of single entry
			int maxValue = (int) (this.xTopValueBounds.end.doubleValue());
			String text = String.valueOf(maxValue);
			double maxWidth = p.context.getTextWidth(text);
	
			//how many ticks we can fit
			double maxTicks = axisWidth / (maxWidth * 3.0);
			
			//the range of values we have to show 
			double valueRange = this.xTopValueBounds.end - this.xTopValueBounds.start;
			
			// we know how many we can fit on to the axis, what is the increment
			// size so we can be near, but not above that number of ticks
					
			String tickText;
			double tickWidth;
	
	
			int increment = AxisMarkGenerator.getIncrement(valueRange, maxTicks, 1);
			int startingValue = getStartingAxisValue(this.xTopValueBounds.start, increment);
			int currentValue = startingValue;
			
			if (showTickMarks) {
	
				double percentAlongAxis, position;
				while (currentValue < this.xTopValueBounds.end)
				{
					
					percentAlongAxis = (currentValue - this.xTopValueBounds.start)  / valueRange;
					position = axesData.xPositionBounds.start + otherAxisSize.first + axisWidth * percentAlongAxis;
					
					AxisMarkGenerator.drawTick(p.context, position, textBaseline + tickSize * 0.5, position, textBaseline + tickSize * 1.5);
	
					tickText = String.valueOf(currentValue);
					tickWidth = p.context.getTextWidth(tickText);
					p.context.writeText(tickText, position - (tickWidth / 2.0), textBaseline);
					
					
					currentValue += increment;
				}
			}
			
		p.context.restore();
		
	}
	

	public void drawLeftYAxis(PainterData p)
	{
		
		if (this.yLeftValueBounds == null) return;
	
		
		Pair<Double, Double> otherAxisSize = getAxisSizeY(p);

		double axisHeight = axesData.yPositionBounds.end - axesData.yPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
		double axisStart = axesData.xPositionBounds.start;
		double axisWidth = getAxisSizeX(p).first;

		double tickWidth = getTickSize(getBaseUnitSize(p.dr), p.dr);
		int maxTicks = AxisMarkGenerator.getMaxTicksY(p.context, axisHeight); //axisHeight / (textHeight*2.0);
		
		double valueRangeStart = this.yLeftValueBounds.start;
		double valueRangeEnd = Plot.getDataScale(this.yLeftValueBounds.end, false);
		double valueRange = valueRangeEnd - valueRangeStart;
		
		
		List<Pair<Double, Integer>> tickLocations = AxisMarkGenerator.getAxisMarkList(maxTicks, axisHeight, 1, valueRangeStart, valueRangeEnd);
		
		
		
		double position, percentAlongAxis, textWidth, tickValue;
		int currentValue, roundedTickValue;
		String currentValueString;
		for (Pair<Double, Integer> tickData : tickLocations){
			
			percentAlongAxis = tickData.first;
			currentValue = tickData.second;
			
			position = axesData.yPositionBounds.start + otherAxisSize.first + axisHeight * percentAlongAxis;
			
			if (axesData.yRightLog) {
				tickValue = Math.exp(  (1.0 - percentAlongAxis) * (Math.log(valueRange) + 1.0)  ) - 1.0;
				roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);
			} else {
				roundedTickValue = currentValue;
			}
			
			if (position - p.context.getFontAscent() / 2.0 > 0) {
			
				AxisMarkGenerator.drawTick(p.context, axisStart + axisWidth - tickWidth, position, axisStart + axisWidth, position);
				
				currentValueString = String.valueOf(roundedTickValue);
				textWidth = p.context.getTextWidth(currentValueString);
				
				p.context.writeText(currentValueString, axisStart + axisWidth - tickWidth*1.5 - textWidth, position + p.context.getFontAscent() / 2.0);
				
							
			}
			
		}
			
	
	}


	public void drawRightYAxis(PainterData p)
	{
		
		if (this.yRightValueBounds == null) return;
		
		
		p.context.save();
			
			Pair<Double, Double> otherAxisSize = getAxisSizeY(p);
	
			double axisHeight = axesData.yPositionBounds.end - axesData.yPositionBounds.start - otherAxisSize.first - otherAxisSize.second;
			double axisWidth = getAxisSizeX(p).first;
			double axisStart = axesData.xPositionBounds.end - axisWidth;
	
			
			double tickWidth = getTickSize(getBaseUnitSize(p.dr), p.dr);
			int maxTicks = AxisMarkGenerator.getMaxTicksY(p.context, axisHeight); //axisHeight / (textHeight*2.0);
			
			double valueRangeStart = this.yRightValueBounds.start;
			double valueRangeEnd = Plot.getDataScale(this.yRightValueBounds.end, false);
			double valueRange = valueRangeEnd - valueRangeStart;
			
			List<Pair<Double, Integer>> tickLocations = AxisMarkGenerator.getAxisMarkList(maxTicks, axisHeight, 1, valueRangeStart, valueRangeEnd);
			
			
			double position, percentAlongAxis, tickValue;
			int currentValue, roundedTickValue;
			String currentValueString;
			for (Pair<Double, Integer> tickData : tickLocations){
				
				percentAlongAxis = tickData.first;
				currentValue = tickData.second;
				
				position = axesData.yPositionBounds.start + otherAxisSize.first + axisHeight * percentAlongAxis;
				
				if (axesData.yRightLog) {
					tickValue = Math.exp(  (1.0 - percentAlongAxis) * (Math.log(valueRange) + 1.0)  ) - 1.0;
					roundedTickValue = SigDigits.toIntSigDigit(tickValue, 2);
				} else {
					roundedTickValue = currentValue;
				}
				
				if (position - p.context.getFontAscent() / 2.0 > 0) {
				
					AxisMarkGenerator.drawTick(p.context, axisStart, position, axisStart + tickWidth, position);
	
					currentValueString = String.valueOf(roundedTickValue);
					
					p.context.writeText(currentValueString, axisStart + tickWidth*1.5, position + p.context.getFontAscent() / 2.0);
	
					
								
				}
				
			}
			
		
		p.context.restore();
	}

	
	
	@Override
	public Pair<Double, Double> getAxisSizeX(PainterData p)
	{
	
		p.context.save();
			
			double baseSize = getBaseUnitSize(p.dr);
			double tickSize = getTickSize(baseSize, p.dr);
			double textWidth;
			
			double leftTextWidth = 0.0;
			if (this.yLeftValueBounds != null){
				textWidth = p.context.getTextWidth(String.valueOf(  SigDigits.toIntSigDigit(this.yLeftValueBounds.start, 2)  ));
				if (textWidth > leftTextWidth) leftTextWidth = textWidth;
				
				textWidth = p.context.getTextWidth(String.valueOf(  SigDigits.toIntSigDigit(Plot.getDataScale(this.yLeftValueBounds.end, false), 2)  ));
				if (textWidth > leftTextWidth) leftTextWidth = textWidth;
				
				leftTextWidth += tickSize * 2.5;
				
			}
			
			double rightTextWidth = 0.0;
			if (this.yRightValueBounds != null){
				textWidth = p.context.getTextWidth(String.valueOf(  SigDigits.toIntSigDigit(this.yRightValueBounds.start, 2)  ));
				if (textWidth > rightTextWidth) rightTextWidth = textWidth;
				
				textWidth = p.context.getTextWidth(String.valueOf(  SigDigits.toIntSigDigit(Plot.getDataScale(this.yRightValueBounds.end, false), 2)  ));
				if (textWidth > rightTextWidth) rightTextWidth = textWidth;
				
				rightTextWidth += tickSize * 2.5;
				
			}
			
		p.context.restore();
		
		return new Pair<Double, Double>(  leftTextWidth, rightTextWidth  );
	}


	@Override
	public Pair<Double, Double> getAxisSizeY(PainterData p)
	{
		
		double baseSize = getBaseUnitSize(p.dr);
		double tickSize = getTickSize(baseSize, p.dr);
		double textHeight = getTickFontHeight(p.context, p.dr);
		
		
		double heightTop = tickSize*1.5 + textHeight;
		double heightBottom = tickSize + textHeight;
		
		if (this.xTopValueBounds == null) heightTop = 0.0;
		if (this.xBottomValueBounds == null) heightBottom = 0.0;
		
		
		return new Pair<Double, Double>(  heightTop, heightBottom  );
	}
	
	
	
	private int getStartingAxisValue(double valueStart, int increment)
	{
		return (int)(valueStart + (Math.abs(valueStart) % increment));
	}


	
}
