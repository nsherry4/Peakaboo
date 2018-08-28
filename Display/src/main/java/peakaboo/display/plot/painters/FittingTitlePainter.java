package peakaboo.display.plot.painters;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
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
import scitypes.ReadOnlySpectrum;
import scitypes.SigDigits;


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
	private List<FittingTitleLabel> labels;
	private boolean drawMaxIntensities;
	private boolean drawElementNames;
		
	private List<FittingTitleLabel> configuredLabels = new ArrayList<>();
	
	/**
	 * Create a FittingTitlePainter which draws in the given {@link Color}
	 * @param fittings the {@link FittingResultSet} for the data being drawn
	 * @param drawTSNames flag to indicate if the names of {@link TransitionSeries} should be drawn
	 * @param drawMaxIntensities flag to indicate if the heights of {@link TransitionSeries} should be drawn
	 * @param colour the {@link Color} that should be used to draw the titles
	 */
	public FittingTitlePainter(
			EnergyCalibration calibration,
			List<FittingTitleLabel> labels, 
			boolean drawTSNames, 
			boolean drawMaxIntensities
		){
		
		//this.tsList = tsList;
		//this.visibleElements = visibleElements;
		this.calibration = calibration;
		this.labels = labels;
		this.drawMaxIntensities = drawMaxIntensities;
		this.drawElementNames = drawTSNames;
				
	}
	

	@Override
	public void drawElement(PainterData p)
	{
		
		p.context.save();
			
			p.context.setFontSize(p.context.getFontSize() + 2);
					
			//calculate derived label info
			for (FittingTitleLabel label : labels){
				configureLabel(label, p);
				configuredLabels.add(label);
			}
			

			//draw lines from the label to the peak
			for (FittingTitleLabel label : labels) {
				drawTextLine(p, label);
			}
			
			//draw the label
			for (FittingTitleLabel label : labels){
				drawTextLabel(p, label, false);
			}

			
			//update the channel height data to reflect the addition of text labels
			for (FittingTitleLabel label : labels)
			{
				for (int i = label.position.x.start.intValue(); i <= label.position.x.end; i++){
					
					if (i < 0 || i > p.dr.dataWidth) continue;
					if (p.dataHeights.get(i) < label.position.y.end) p.dataHeights.set(i, label.position.y.end);
					
				}
			}
			
		p.context.restore();
			
	}
	
	// Calculates the minimum height the label can be drawn at based on the spectral data being displayed
	private float baseHeightFromData(PainterData p, FittingTitleLabel label, float energy) {
		return baseHeightFromData(p, label, energy, p.dataHeights);
	}
	private float baseHeightFromData(PainterData p, FittingTitleLabel label, float energy, ReadOnlySpectrum heights) {
		Coord<Bounds<Float>> currentLabel = getTextLabelDimensions(p, label, energy);
		
		//minimum height based on data
		int baselineStart = currentLabel.x.start.intValue();
		int baselineEnd = currentLabel.x.end.intValue();
		if (baselineStart >= baselineEnd) {
			return 0;
		}
		float baseline = heights.subSpectrum(baselineStart, baselineEnd).max();
		return baseline;
	}
	
	//calculates the minimum height the label can be drawn at based on spectral data AND previous labels in the way
	private float baseHeightForTitle(PainterData p, FittingTitleLabel label, float energy)
	{
			
		Coord<Bounds<Float>> currentLabel = getTextLabelDimensions(p, label, energy);
		
		//minimum height based on data
		float baseline = baseHeightFromData(p, label, energy);
		float currentLabelHeight = currentLabel.y.end - currentLabel.y.start;
		
		
		
		//go over all previous labels-in-range in order of bottom y coordinate
		
		List<FittingTitleLabel> labelsInRange = new ArrayList<>();
		
		//get a list of all labels which might get in the way of this one
		for (FittingTitleLabel pastLabel : configuredLabels) {
			if (
					(pastLabel.position.x.start <= currentLabel.x.end && pastLabel.position.x.start >= currentLabel.x.start)
					|| 
					(pastLabel.position.x.end <= currentLabel.x.end && pastLabel.position.x.end >= currentLabel.x.start)
			){
				labelsInRange.add(pastLabel);
			}
		}
		
		//sort the elements by order of bottom y position
		Collections.sort(labelsInRange, new Comparator<FittingTitleLabel>() {

			public int compare(FittingTitleLabel o1, FittingTitleLabel o2)
			{
				if (o1.position.y.start < o2.position.y.start) return -1;
				if (o1.position.y.start > o2.position.y.start) return 1;
				return 0;
			}

		});
		
		for (FittingTitleLabel labelInRange : labelsInRange)
		{
			
			//if there is enough room for the label, we've found the right baseline
			if (labelInRange.position.y.start - baseline > currentLabelHeight){
				break;
			} else {
				if (labelInRange.position.y.end > baseline) baseline = labelInRange.position.y.end;
			}
			
		}
		
		//place this label at baseline
		return baseline;

		
	}
	


	
	protected Coord<Bounds<Float>> getTextLabelDimensions(PainterData p, FittingTitleLabel label, float energy)
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

		float leftChannel = (float)Math.max(0, Math.floor(farLeft / channelSize));
		float rightChannel = (float)Math.min(p.dr.dataWidth-1, Math.ceil(farRight / channelSize));
		
		
		
		return new Coord<Bounds<Float>>(new Bounds<Float>(leftChannel, rightChannel), new Bounds<Float>(penWidth, totalHeight));
		
	}
	
	/**
	 * Draws a text label on the plot
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot.
	 * @param title the title of the label
	 * @param energy the energy value at which to centre the label
	 */
	protected void drawTextLabel(PainterData p, FittingTitleLabel label, boolean resetColour) {
			
		if (label.title == null) { return; }
		
		float channelSize = p.plotSize.x / p.dr.dataWidth;
		float xStart = label.position.x.start * channelSize;
		float xTextStart = xStart + label.penWidth*2;
		float yTextStart = label.position.y.start + p.context.getFontDescent();
		if (xStart > p.plotSize.x) return;
		
		float w = (label.position.x.end - label.position.x.start) * channelSize;
		float h = label.position.y.end - label.position.y.start;
				
		p.context.setSource(Color.WHITE);
		p.context.addShape(new RoundRectangle2D.Float(xStart, p.plotSize.y - label.position.y.end, w, h, 5, 5));
		p.context.fill();
		p.context.setSource(label.colour);
		p.context.addShape(new RoundRectangle2D.Float(xStart, p.plotSize.y - label.position.y.end, w, h, 5, 5));
		p.context.stroke();
		p.context.writeText(label.title, xTextStart, p.plotSize.y - yTextStart);
		

	}
	
	protected void drawTextLine(PainterData p, FittingTitleLabel label) {
		Color semitransparent = new Color(label.colour.getRed(), label.colour.getGreen(), label.colour.getBlue(), 64);
		p.context.setSource(semitransparent);
		float channelSize = p.plotSize.x / p.dr.dataWidth;
		TransitionSeries ts = label.fit.getTransitionSeries();
		Transition t = ts.getStrongestTransition();
		int endChannel = calibration.channelFromEnergy(t.energyValue);
		
		float x = ((label.position.x.start + label.position.x.end) / 2f) * channelSize;
		float yStart = p.plotSize.y - label.position.y.start;
		float yEnd = p.plotSize.y - p.originalHeights.get(endChannel);
		
		p.context.moveTo(x, yStart);
		
		p.context.lineTo(x, yEnd);
		p.context.stroke();
	}
	
	private String getTitle(FittingTitleLabel label) {
		TransitionSeries ts = label.fit.getTransitionSeries();
		String titleName = ts.getDescription();

		
		String titleHeight = SigDigits.roundFloatTo(label.fit.getCurveScale(), 1);

		String title = "";
		if (drawElementNames) title += titleName;
		if (drawElementNames && drawMaxIntensities) title += " (";
		if (drawMaxIntensities) title += titleHeight;
		if (drawElementNames && drawMaxIntensities) title += ")";
		
		if (label.annotation != null) {
			title += ": " + label.annotation;
		}
		
		return title;
	}

	private void configureLabel(FittingTitleLabel label, PainterData p) {

		label.viable = true;
		
		TransitionSeries ts = label.fit.getTransitionSeries();
		label.title = getTitle(label);
		
		//TransitionType type = TransitionType.a1;
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
		float baseHeightFromData = baseHeightFromData(p, label, t.energyValue, p.originalHeights);
		float baseHeightForTitle = baseHeightForTitle(p, label, t.energyValue);
		label.position.y.start += baseHeightForTitle;
		label.position.y.end += baseHeightForTitle;
		
		
		
		return;
	}
	

}



