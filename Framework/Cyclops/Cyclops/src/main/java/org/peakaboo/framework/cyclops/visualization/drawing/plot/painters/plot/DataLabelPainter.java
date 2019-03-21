package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.PlotPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;


/**
 * 
 * Draws titles for the {@link Transition}s in a list of {@link TransitionSeries} over the Transition peaks.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class DataLabelPainter extends PlotPainter
{

	public static class DataLabel {
		
		//passed in
		public float index;
		public PlotPalette palette;

		//derived by painters
		public String title;
		public Coord<Bounds<Float>> position;
		public boolean viable = true;
		public float penWidth = 1f;
		
		public DataLabel(PlotPalette palette, float index, String text) {
			this.title = text;
			this.index = index;
			this.palette = palette;
		}
		
	}
	
	private List<DataLabel> labels;
	private List<DataLabel> configuredLabels = new ArrayList<>();
	private float xoffset;
	
	public DataLabelPainter(List<? extends DataLabel> labels){
		this(labels, 0f);
	}
	
	/**
	 * Create a FittingTitlePainter which draws in the given {@link Color}
	 */
	public DataLabelPainter(List<? extends DataLabel> labels, float xoffset){
		this.labels = (List<DataLabel>) labels;
		this.xoffset = xoffset;
	}
	

	@Override
	public void drawElement(PainterData p) {
		
		configuredLabels.clear();
				
		p.context.save();
			
			p.context.setFontSize(p.context.getFontSize() + 2);
					
			//calculate derived label info
			for (DataLabel label : labels){
				configureLabel(label, p);
				configuredLabels.add(label);
			}
			

			//draw lines from the label to the data
			for (DataLabel label : labels) {
				if (!label.viable) continue;
				drawTextLine(p, label);
			}
			
			//draw the label
			for (DataLabel label : labels){
				if (!label.viable) continue;
				drawTextLabel(p, label, false);
			}

			
			//update the channel height data to reflect the addition of text labels
			for (DataLabel label : labels)
			{
				for (int i = label.position.x.start.intValue(); i <= label.position.x.end; i++){
					
					if (i < 0 || i > p.dr.dataWidth) continue;
					if (p.dataHeights.get(i) < label.position.y.end) p.dataHeights.set(i, label.position.y.end);
					
				}
			}
			
		p.context.restore();
			
	}
	
	// Calculates the minimum height the label can be drawn at based on the spectral data being displayed
	private float baseHeightFromData(PainterData p, DataLabel label, Coord<Bounds<Float>> position) {
		
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
	private float baseHeightForTitle(PainterData p, DataLabel label)
	{
			
		Coord<Bounds<Float>> position = getTextLabelDimensions(p, label);
		
		//minimum height based on data
		float baseline = baseHeightFromData(p, label, position);
		float currentLabelHeight = position.y.end - position.y.start;
		
		
		
		//go over all previous labels-in-range in order of bottom y coordinate
		
		List<DataLabel> labelsInRange = new ArrayList<>();
		
		//get a list of all labels which might get in the way of this one based on x coords
		for (DataLabel pastLabel : configuredLabels) {
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
		Collections.sort(labelsInRange, new Comparator<DataLabel>() {

			public int compare(DataLabel o1, DataLabel o2)
			{
				if (o1.position.y.start < o2.position.y.start) return -1;
				if (o1.position.y.start > o2.position.y.start) return 1;
				return 0;
			}

		});
			
		for (DataLabel labelInRange : labelsInRange) {

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
	


	
	protected Coord<Bounds<Float>> getTextLabelDimensions(PainterData p, DataLabel label)
	{
		DrawingRequest dr = p.dr;

		float textWidth = p.context.getTextWidth(label.title);

		float channelSize = p.plotSize.x / dr.dataWidth;
		float centreChannel = label.index + xoffset;

		float titleStart = centreChannel * channelSize;
		titleStart -= (textWidth / 2.0);


		float titleHeight = p.context.getFontHeight();
		float penWidth = label.penWidth;
		float totalHeight = (titleHeight + penWidth * 2);
		
		float farLeft = titleStart - penWidth * 2;
		float width = textWidth + penWidth * 6;
		float farRight = farLeft + width;
		
		float leftChannel = (farLeft / channelSize);
		float rightChannel = (farRight / channelSize);
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
	 */
	protected void drawTextLabel(PainterData p, DataLabel label, boolean resetColour) {
			
		if (label.title == null || label.title.length() == 0) { return; }
		
		float channelSize = p.plotSize.x / p.dr.dataWidth;
		float xStart = label.position.x.start * channelSize;
		float xTextStart = xStart + label.penWidth*2;
		float yTextStart = label.position.y.start + p.context.getFontDescent();
		if (xStart > p.plotSize.x) return;
		
		float w = (label.position.x.end - label.position.x.start) * channelSize;
		float h = label.position.y.end - label.position.y.start;
				
		p.context.setSource(label.palette.labelBackground);
		p.context.roundRectAt(xStart, p.plotSize.y - label.position.y.end, w+1, h+1, 2.5f, 2.5f);
		//p.context.addShape(new RoundRectangle2D.Float(xStart, p.plotSize.y - label.position.y.end, w, h, 5, 5));
		p.context.fill();
		p.context.setSource(label.palette.labelStroke);
		p.context.roundRectAt(xStart, p.plotSize.y - label.position.y.end, w+1, h+1, 2.5f, 2.5f);
		//p.context.addShape(new RoundRectangle2D.Float(xStart, p.plotSize.y - label.position.y.end, w, h, 5, 5));
		p.context.stroke();
		p.context.setSource(label.palette.labelText);
		p.context.writeText(label.title, xTextStart+1, p.plotSize.y - yTextStart+1);
		

	}
	
	protected void drawTextLine(PainterData p, DataLabel label) {
		if (label.title == null || label.title.length() == 0) { return; }
		
		PaletteColour stroke = label.palette.labelStroke;
		PaletteColour semitransparent = new PaletteColour(64, stroke.getRed(), stroke.getGreen(), stroke.getBlue());
		p.context.setSource(semitransparent);
		float channelSize = p.plotSize.x / p.dr.dataWidth;
		float centreChannel = label.index + xoffset;
		int endChannel = Math.round(label.index);
		/*
		 * if this is out of range of the dataset, just cram it into the edges of the plot
		 */
		if (endChannel < 0) { endChannel = 0; }
		if (endChannel >= p.originalHeights.size()) { endChannel = p.originalHeights.size()-1; }
		
		float xPeak = centreChannel * channelSize;
		float xLabel = ((label.position.x.start + label.position.x.end) / 2f) * channelSize;
		float yStart = p.plotSize.y - label.position.y.start;
		float yEnd = p.plotSize.y - p.originalHeights.get(endChannel);
		
		p.context.moveTo(xLabel, yStart);
		
		p.context.lineTo(xPeak, yEnd);
		p.context.stroke();
	}
	
	private void configureLabel(DataLabel label, PainterData p) {

		label.viable = true;
		label.penWidth = 1f;
		
		label.position = getTextLabelDimensions(p, label);
		if (label.position.x.start.intValue() > p.dr.dataWidth) {
			label.viable = false;
			return;
		}
		float baseHeightForTitle = baseHeightForTitle(p, label);
		label.position.y.start += baseHeightForTitle;
		label.position.y.end += baseHeightForTitle;

		return;
	}
	

}



