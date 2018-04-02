package peakaboo.ui.swing.plotting.fitting.painters;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import peakaboo.curvefit.fitting.FittingResult;
import peakaboo.curvefit.fitting.FittingResultSet;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Bounds;
import scitypes.Coord;
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
	private FittingResultSet fittings;
	private boolean drawMaxIntensities;
	private boolean drawElementNames;
	
	private List<Coord<Bounds<Float>>> previousLabels;
	
	private Color colour;
	
	/**
	 * Create a FittingTitlePainter which draws in black
	 * @param fittings the {@link FittingResultSet} for the data being drawn
	 * @param drawTSNames flag to indicate if the names of {@link TransitionSeries} should be drawn
	 * @param drawMaxIntensities flag to indicate if the heights of {@link TransitionSeries} should be drawn
	 */
	public FittingTitlePainter(FittingResultSet fittings, boolean drawTSNames, boolean drawMaxIntensities){
		
		this(fittings, drawTSNames, drawMaxIntensities, Color.black);	
		
	}
	
	/**
	 * Create a FittingTitlePainter which draws in the given {@link Color}
	 * @param fittings the {@link FittingResultSet} for the data being drawn
	 * @param drawTSNames flag to indicate if the names of {@link TransitionSeries} should be drawn
	 * @param drawMaxIntensities flag to indicate if the heights of {@link TransitionSeries} should be drawn
	 * @param colour the {@link Color} that should be used to draw the titles
	 */
	public FittingTitlePainter(FittingResultSet fittings, boolean drawTSNames, boolean drawMaxIntensities, Color colour){
		
		//this.tsList = tsList;
		//this.visibleElements = visibleElements;
		this.fittings = fittings;
		this.drawMaxIntensities = drawMaxIntensities;
		this.drawElementNames = drawTSNames;
		
		this.previousLabels = new ArrayList<Coord<Bounds<Float>>>();
		
		this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue());
		
	}
	

	@Override
	public void drawElement(PainterData p)
	{
		String titleName, titleHeight, title;
		Transition t;
		
		p.context.save();
			
			p.context.setSource(colour);
			
			for (FittingResult fit : fittings.fits){
		
			titleName = fit.getTransitionSeries().getDescription();

			
			titleHeight = SigDigits.roundFloatTo(fit.getCurveScale(), 1);

			title = "";
			if (drawElementNames) title += titleName;
			if (drawElementNames && drawMaxIntensities) title += " (";
			if (drawMaxIntensities) title += titleHeight;
			if (drawElementNames && drawMaxIntensities) title += ")";
			
			

				//TransitionType type = TransitionType.a1;
				t = fit.getTransitionSeries().getStrongestTransition();
				
				if (t != null) {
					
					Coord<Bounds<Float>> currentLabel = getTextLabelDimensions(p, title, t.energyValue);
					if (currentLabel.x.start.intValue() > p.dr.dataWidth) continue;
					float baseHeightForTitle = baseHeightForTitle(p, title, t.energyValue);
					currentLabel.y.start += baseHeightForTitle;
					currentLabel.y.end += baseHeightForTitle;
					float channelSize = p.plotSize.x / p.dr.dataWidth;
					
					drawTextLabel(p, title, t.energyValue, currentLabel.x.start * channelSize, currentLabel.y.start, false);
					
					previousLabels.add(currentLabel);
				}

			
				
				
			}// for all transitionseries
			
			//update the channel height data to reflect the addition of text labels
			for (Coord<Bounds<Float>> label : previousLabels)
			{
				for (int i = label.x.start.intValue(); i <= label.x.end; i++){
					
					if (i < 0 || i > p.dr.dataWidth) continue;
					if (p.dataHeights.get(i) < label.y.end) p.dataHeights.set(i, label.y.end);
					
				}
			}
			
		p.context.restore();
			
	}
	
	
	public float baseHeightForTitle(PainterData p, String title, float energy)
	{
			
		Coord<Bounds<Float>> currentLabel = getTextLabelDimensions(p, title, energy);
		List<Coord<Bounds<Float>>> labelsInRange = new ArrayList<Coord<Bounds<Float>>>();
		
		//get a list of all labels which might get in the way of this one
		for (Coord<Bounds<Float>> label : previousLabels)
		{
			if (
					(label.x.start <= currentLabel.x.end && label.x.start >= currentLabel.x.start)
					|| 
					(label.x.end <= currentLabel.x.end && label.x.end >= currentLabel.x.start)
			){
				labelsInRange.add(label);
			}
		}
		
		//sort the elements by order of bottom y position
		Collections.sort(labelsInRange, new Comparator<Coord<Bounds<Float>>>() {

			public int compare(Coord<Bounds<Float>> o1, Coord<Bounds<Float>> o2)
			{
				if (o1.y.start < o2.y.start) return -1;
				if (o1.y.start > o2.y.start) return 1;
				return 0;
			}

		});
		
				
		float baseline = p.dataHeights.subSpectrum(currentLabel.x.start.intValue(), currentLabel.x.end.intValue()).max();
		float currentLabelHeight = currentLabel.y.end - currentLabel.y.start;
		
		//go over all previous labels in order of bottom y coordinate
		for (Coord<Bounds<Float>> label : labelsInRange)
		{
			
			//if there is enough room for the label, we've found the right baseline
			if (label.y.start - baseline > currentLabelHeight){
				break;
			} else {
				if (label.y.end > baseline) baseline = label.y.end;
			}
			
		}
		
		//place this label at baseline
		return baseline;

		
	}
	


}
