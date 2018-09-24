package peakaboo.display.plot.painters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SigDigits;
import scitypes.visualization.drawings.RoundedRectangle;
import scitypes.visualization.palette.PaletteColour;


/**
 * 
 * Draws titles for the {@link Transition}s in a list of {@link TransitionSeries} over the Transition peaks.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class FittingTitlePainter extends PlotPainter
{

	//private List<TransitionSeries> tsList;
	//private List<Element> visibleElements;
	private EnergyCalibration calibration;
	private List<FittingLabel> labels;
	private boolean drawMaxIntensities;
		
	private List<FittingLabel> configuredLabels = new ArrayList<>();
	
	/**
	 * Create a FittingTitlePainter which draws in the given {@link Color}
	 * @param fittings the {@link FittingResultSet} for the data being drawn
	 * @param drawTSNames flag to indicate if the names of {@link TransitionSeries} should be drawn
	 * @param drawMaxIntensities flag to indicate if the heights of {@link TransitionSeries} should be drawn
	 * @param colour the {@link Color} that should be used to draw the titles
	 */
	public FittingTitlePainter(
			EnergyCalibration calibration,
			List<FittingLabel> labels, 
			boolean drawMaxIntensities
		){
		
		//this.tsList = tsList;
		//this.visibleElements = visibleElements;
		this.calibration = calibration;
		this.labels = labels;
		this.drawMaxIntensities = drawMaxIntensities;
				
	}
	

	@Override
	public void drawElement(PainterData p)
	{
		
		//Don't draw without energy calibration
		if (calibration.isZero()) {
			return;
		}
		
		p.context.save();
			
			p.context.setFontSize(p.context.getFontSize() + 2);
					
			//calculate derived label info
			for (FittingLabel label : labels){
				configureLabel(label, p);
				configuredLabels.add(label);
			}
			

			//draw lines from the label to the peak
			for (FittingLabel label : labels) {
				if (!label.viable) continue;
				drawTextLine(p, label);
			}
			
			//draw the label
			for (FittingLabel label : labels){
				if (!label.viable) continue;
				drawTextLabel(p, label, false);
			}

			
			//update the channel height data to reflect the addition of text labels
			for (FittingLabel label : labels)
			{
				for (int i = label.position.x.start.intValue(); i <= label.position.x.end; i++){
					
					if (i < 0 || i > p.dr.dataWidth) continue;
					if (p.dataHeights.get(i) < label.position.y.end) p.dataHeights.set(i, label.position.y.end);
					
				}
			}
			
		p.context.restore();
			
	}
	
	// Calculates the minimum height the label can be drawn at based on the spectral data being displayed
	private float baseHeightFromData(PainterData p, FittingLabel label, Coord<Bounds<Float>> position) {
		
		//minimum height based on data
		int baselineStart = position.x.start.intValue();
		int baselineEnd = position.x.end.intValue();
		if (baselineStart >= baselineEnd) {
			return 0;
		}
		float baseline = p.dataHeights.subSpectrum(baselineStart, baselineEnd).max() + label.penWidth;
		return baseline;
	}
	
	//calculates the minimum height the label can be drawn at based on spectral data AND previous labels in the way
	private float baseHeightForTitle(PainterData p, FittingLabel label, float energy)
	{
			
		Coord<Bounds<Float>> position = getTextLabelDimensions(p, label, energy);
		
		//minimum height based on data
		float baseline = baseHeightFromData(p, label, position);
		float currentLabelHeight = position.y.end - position.y.start;
		
		
		
		//go over all previous labels-in-range in order of bottom y coordinate
		
		List<FittingLabel> labelsInRange = new ArrayList<>();
		
		//get a list of all labels which might get in the way of this one based on x coords
		for (FittingLabel pastLabel : configuredLabels) {
			if (
					//if the other's start point is within our start->end range
					(pastLabel.position.x.start <= position.x.end && pastLabel.position.x.start >= position.x.start)
					||
					//if the other's end point is within our start->end range
					(pastLabel.position.x.end <= position.x.end && pastLabel.position.x.end >= position.x.start)
					||
					//if the other starts before us, and ends after us
					(pastLabel.position.x.start <= position.x.start && pastLabel.position.x.end >= position.x.end)
			){
				labelsInRange.add(pastLabel);
			}
		}
		
		//sort the elements by order of bottom y position
		Collections.sort(labelsInRange, new Comparator<FittingLabel>() {

			public int compare(FittingLabel o1, FittingLabel o2)
			{
				if (o1.position.y.start < o2.position.y.start) return -1;
				if (o1.position.y.start > o2.position.y.start) return 1;
				return 0;
			}

		});
			
		for (FittingLabel labelInRange : labelsInRange) {

			//if there is enough room for the label, we've found the right baseline
			if (labelInRange.position.y.start - baseline > currentLabelHeight){
				break;
			}
			
			if (labelInRange.position.y.end > baseline) {
				baseline = labelInRange.position.y.end;
			}
			
			
		}
		
		//place this label at baseline
		return baseline;

		
	}
	


	
	protected Coord<Bounds<Float>> getTextLabelDimensions(PainterData p, FittingLabel label, float energy)
	{
		DrawingRequest dr = p.dr;

		float textWidth = p.context.getTextWidth(label.title);

		float channelSize = p.plotSize.x / dr.dataWidth;
		float centreChannel = calibration.fractionalChannelFromEnergy(energy);

		float titleStart = centreChannel * channelSize;
		titleStart -= (textWidth / 2.0);


		float titleHeight = p.context.getFontHeight();
		float penWidth = label.penWidth;
		float totalHeight = (titleHeight + penWidth * 2);
		
		float farLeft = titleStart - penWidth * 2;
		float width = textWidth + penWidth * 4;
		float farRight = farLeft + width;

		float leftChannel = (float) Math.floor(farLeft / channelSize);
		float rightChannel = (float) Math.ceil(farRight / channelSize);
		float rightMax = p.dr.dataWidth-1;
		
		//if one of the sides is out of bounds, first try shifting the label
		if (leftChannel < 0) {
			rightChannel -= leftChannel;
			leftChannel = 0;
		}
		if (rightChannel > rightMax) {
			leftChannel -= (rightChannel - rightMax);
			rightChannel = rightMax;
		}
		
		//finally, make sure both edges are in bounds
		leftChannel = (float)Math.max(0, leftChannel);
		rightChannel = (float)Math.min(rightMax, rightChannel);
		
		
		
		return new Coord<Bounds<Float>>(new Bounds<Float>(leftChannel, rightChannel), new Bounds<Float>(penWidth, totalHeight));
		
	}
	
	/**
	 * Draws a text label on the plot
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot.
	 * @param title the title of the label
	 * @param energy the energy value at which to centre the label
	 */
	protected void drawTextLabel(PainterData p, FittingLabel label, boolean resetColour) {
			
		if (label.title == null || label.title.length() == 0) { return; }
		
		float channelSize = p.plotSize.x / p.dr.dataWidth;
		float xStart = label.position.x.start * channelSize;
		float xTextStart = xStart + label.penWidth*2;
		float yTextStart = label.position.y.start + p.context.getFontDescent();
		if (xStart > p.plotSize.x) return;
		
		float w = (label.position.x.end - label.position.x.start) * channelSize;
		float h = label.position.y.end - label.position.y.start;
				
		p.context.setSource(label.palette.labelBackground);
		p.context.addShape(new RoundedRectangle(xStart, p.plotSize.y - label.position.y.end, w+1, h+1, 2.5f, 2.5f));
		//p.context.addShape(new RoundRectangle2D.Float(xStart, p.plotSize.y - label.position.y.end, w, h, 5, 5));
		p.context.fill();
		p.context.setSource(label.palette.labelStroke);
		p.context.addShape(new RoundedRectangle(xStart, p.plotSize.y - label.position.y.end, w+1, h+1, 2.5f, 2.5f));
		//p.context.addShape(new RoundRectangle2D.Float(xStart, p.plotSize.y - label.position.y.end, w, h, 5, 5));
		p.context.stroke();
		p.context.setSource(label.palette.labelText);
		p.context.writeText(label.title, xTextStart+1, p.plotSize.y - yTextStart+1);
		

	}
	
	protected void drawTextLine(PainterData p, FittingLabel label) {
		if (label.title == null || label.title.length() == 0) { return; }
		
		PaletteColour stroke = label.palette.labelStroke;
		PaletteColour semitransparent = new PaletteColour(64, stroke.getRed(), stroke.getGreen(), stroke.getBlue());
		p.context.setSource(semitransparent);
		float channelSize = p.plotSize.x / p.dr.dataWidth;
		TransitionSeries ts = label.fit.getTransitionSeries();
		Transition t = ts.getStrongestTransition();
		
		float centreChannel = calibration.fractionalChannelFromEnergy(t.energyValue);
		int endChannel = calibration.channelFromEnergy(t.energyValue);
		
		float xPeak = centreChannel * channelSize;
		float xLabel = ((label.position.x.start + label.position.x.end) / 2f) * channelSize;
		float yStart = p.plotSize.y - label.position.y.start;
		float yEnd = p.plotSize.y - p.originalHeights.get(endChannel);
		
		p.context.moveTo(xLabel, yStart);
		
		p.context.lineTo(xPeak, yEnd);
		p.context.stroke();
	}
	
	private String getTitle(FittingLabel label) {
		StringBuilder sb = new StringBuilder();
		TransitionSeries ts = label.fit.getTransitionSeries();
		String titleName = ts.getDescription();

		
		String titleHeight = SigDigits.roundFloatTo(label.fit.getCurveScale(), 1);

		sb.append(titleName);
		if (drawMaxIntensities) {
			sb.append(" (" + titleHeight + ")");
		}
		
		if (label.annotation != null) {
			if (sb.length() > 0) {
				sb.append(": ");
			}
			sb.append(label.annotation);
		}
		
		return sb.toString();
	}

	private void configureLabel(FittingLabel label, PainterData p) {

		label.viable = true;
		
		TransitionSeries ts = label.fit.getTransitionSeries();
		label.title = getTitle(label);
		
		Transition t = ts.getStrongestTransition();
		
		if (t == null) {
			label.viable = false;
			return; 
		}
		
		label.penWidth = getPenWidth(getBaseUnitSize(p.dr), p.dr);
		
		label.position = getTextLabelDimensions(p, label, t.energyValue);
		if (label.position.x.start.intValue() > p.dr.dataWidth) {
			label.viable = false;
			return;
		}
		float baseHeightForTitle = baseHeightForTitle(p, label, t.energyValue);
		label.position.y.start += baseHeightForTitle;
		label.position.y.end += baseHeightForTitle;

		return;
	}
	

}



