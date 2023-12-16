package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.ticks.LinearTickGenerator;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.ticks.LogTickGenerator;

public class RangeTickFormatter extends AbstractTickFormatter {
	
	private float scaleMin, scaleMax, maxScaleSignal;
	private Function<Integer, String> formatter;
		
	
	public RangeTickFormatter(float scaleStart, float scaleEnd) {
		this(scaleStart, scaleEnd, String::valueOf);
	}
	
	public RangeTickFormatter(float scaleStart, float scaleEnd, float maxScaleSignal) {
		this(scaleStart, scaleEnd, maxScaleSignal, String::valueOf);
	}

	
	public RangeTickFormatter(float scaleStart, float scaleEnd, Function<Integer, String> formatter) {
		this(scaleStart, scaleEnd, scaleEnd, formatter);
	}
	
	public RangeTickFormatter(float scaleMin, float scaleMax, float maxScaleSignal, Function<Integer, String> formatter) {
		this.scaleMin = scaleMin;
		this.scaleMax = scaleMax;
		this.formatter = formatter;
		this.maxScaleSignal = maxScaleSignal;
	}
	
	
	@Override
	public List<TickMark> getTickMarks(PainterData p, float size, boolean includeMinorTicks) {
		var marks = new ArrayList<TickMark>();
		
		if (this.isEmpty()) return marks;
		float maxTicks = this.calcMaxTicks(p, size);
	
		if (!this.isLog()) {
			marks.clear();
			
			var tickGen = new LinearTickGenerator();
			var minorTicks = tickGen.getTicks(scaleMax, (int)maxTicks, true);
			var majorTicks = tickGen.getTicks(scaleMax, (int)maxTicks, false);
			float scaleRange = scaleMax - scaleMin;
			
			for (int scaleValue : minorTicks) {
				boolean minor = !majorTicks.contains(scaleValue);
				
				float tickY = (scaleValue - scaleMin) / scaleRange;
				marks.add(new TickMark(format(scaleValue), tickY, minor));
			}
			
		} else {
			marks.clear();
			
			var tickGen = new LogTickGenerator();
			var minorTicks = tickGen.getTicks(scaleMax, (int)maxTicks, true);
			var majorTicks = tickGen.getTicks(scaleMax, (int)maxTicks, false);

			float logMax = (float) Math.log10(1+scaleMax);
			if (Math.abs(scaleMin) > 0.01) {
				throw new IllegalArgumentException("Cannot use log scale with axes that do not start at zero");
			}
			
			for (int scaleValue : minorTicks) {
				boolean minor = !majorTicks.contains(scaleValue);
				
				float logValue = (float) Math.log10(1+scaleValue);
				float tickY = logValue / logMax;
				var tickmark = new TickMark(format(scaleValue), tickY, minor);
				marks.add(tickmark);
			}
			
		}
		
		
		
		return marks;
	}
	
	@Override
	public TickTextSize maxTextSize(PainterData p) {
		
		float height = p.context.getFontHeight();
		
		float startWidth = p.context.getTextWidth(this.format(  SigDigits.toIntSigDigit(this.scaleMin, 2)  ));			
		float endWidth = p.context.getTextWidth(this.format(  SigDigits.toIntSigDigit(PlotDrawing.getDataScale(this.scaleMax, false, this.isPadded()), 2)  ));
		float width = Math.max(startWidth, endWidth);
		
		return new TickTextSize(width, height);
		
	}
	
	@Override
	public boolean isEmpty() {
		return this.scaleMax - this.scaleMin <= 0;
	}

	private String format(int value) {
		return this.formatter.apply(value);
	}
	







	private float calcMaxTicks(PainterData p, float freeSpace) {
		//how many ticks we can fit and the range of values we're drawing over
		float maxTicks = 0;
		if (this.isTextRotated()) {
			float textHeight = p.context.getFontHeight();
			maxTicks = (float) Math.floor(freeSpace / (textHeight*3.0));
			return maxTicks;
		} else {
			// text isn't rotated out so calculate the maximum width of a text entry here
			int maxValue = (int) (this.scaleMax);
			String text = this.formatter.apply(maxValue);
			float maxWidth = p.context.getTextWidth(text) + 4;
			if (maxWidth < 1) { maxWidth = 1; }
			maxTicks = freeSpace / (maxWidth * 3.0f);
		}
		return maxTicks;
	}
	
	private static int getIncrement(float valueRange, float maxTickCount, int significantDigits) {		
		if (maxTickCount == 0) return Integer.MAX_VALUE;
		return SigDigits.toIntSigDigit(valueRange / maxTickCount, significantDigits);
	}
	
	
}