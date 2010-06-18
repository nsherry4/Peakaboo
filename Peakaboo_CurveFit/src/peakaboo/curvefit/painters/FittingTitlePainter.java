package peakaboo.curvefit.painters;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import peakaboo.calculations.SpectrumCalculations;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.peaktable.Transition;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.painters.PlotPainter;

/**
 * 
 * Draws titles for the {@link Transition}s in a list of {@link TransitionSeries} over the Transition peaks.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class FittingTitlePainter extends PlotPainter {

	//private List<TransitionSeries> tsList;
	//private List<Element> visibleElements;
	private FittingResultSet fittings;
	private boolean drawMaxIntensities;
	private boolean drawElementNames;
	
	private List<Coord<Range<Float>>> previousLabels;
	
	public FittingTitlePainter(FittingResultSet fittings, boolean drawElementNames, boolean drawMaxIntensities){
		
		//this.tsList = tsList;
		//this.visibleElements = visibleElements;
		this.fittings = fittings;
		this.drawMaxIntensities = drawMaxIntensities;
		this.drawElementNames = drawElementNames;
		
		this.previousLabels = DataTypeFactory.<Coord<Range<Float>>>list();
		
		
	}
	

	@Override
	public void drawElement(PainterData p)
	{
		String titleName, titleHeight, title;
		Transition t;
		
		for (FittingResult fit : fittings.fits){
			
			//if (fit.transitionSeries.visible && visibleElements.contains(fit.transitionSeries.element)){
				titleName = fit.transitionSeries.getDescription();

				
				titleHeight = SigDigits.roundFloatTo(fit.scaleFactor, 1);

				title = "";
				if (drawElementNames) title += titleName;
				if (drawElementNames && drawMaxIntensities) title += " (";
				if (drawMaxIntensities) title += titleHeight;
				if (drawElementNames && drawMaxIntensities) title += ")";
				
				
				//if (fit.transitionSeries.type != TransitionSeriesType.Kx2)
				//{
				
					//TransitionType type = TransitionType.a1;
					t = fit.transitionSeries.getStrongestTransition();
					
					if (t != null) {
						
						Coord<Range<Float>> currentLabel = getTextLabelDimensions(p, title, t.energyValue);
						if (currentLabel.x.start.intValue() > p.dr.dataWidth) continue;
						float baseHeightForTitle = baseHeightForTitle(p, title, t.energyValue);
						currentLabel.y.start += baseHeightForTitle;
						currentLabel.y.end += baseHeightForTitle;
						float channelSize = p.plotSize.x / p.dr.dataWidth;
						
						drawTextLabel(p, title, t.energyValue, currentLabel.x.start * channelSize, currentLabel.y.start);
						
						previousLabels.add(currentLabel);
					}
				//}
				
			//} //visible?
			
			
		}// for all transitionseries
		
		//update the channel height data to reflect the addition of text labels
		for (Coord<Range<Float>> label : previousLabels)
		{
			for (int i = label.x.start.intValue(); i <= label.x.end; i++){
				
				if (i < 0 || i > p.dr.dataWidth) continue;
				if (p.dataHeights.get(i) < label.y.end) p.dataHeights.set(i, label.y.end);
				
			}
		}
	}
	
	public float baseHeightForTitle(PainterData p, String title, float energy)
	{
		
		Coord<Range<Float>> currentLabel = getTextLabelDimensions(p, title, energy);
		List<Coord<Range<Float>>> labelsInRange = DataTypeFactory.<Coord<Range<Float>>>list();
		
		//get a list of all labels which might get in the way of this one
		for (Coord<Range<Float>> label : previousLabels)
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
		Collections.sort(labelsInRange, new Comparator<Coord<Range<Float>>>() {

			public int compare(Coord<Range<Float>> o1, Coord<Range<Float>> o2)
			{
				if (o1.y.start < o2.y.start) return -1;
				if (o1.y.start > o2.y.start) return 1;
				return 0;
			}

		});
		
		
		//get the starting baseline from the pre-existing dataHeights
		float baseline = SpectrumCalculations.max(p.dataHeights.subSpectrum(currentLabel.x.start.intValue(), currentLabel.x.end.intValue()));
		float currentLabelHeight = currentLabel.y.end - currentLabel.y.start;
		
		//go over all previous labels in order of bottom y coordinate
		for (Coord<Range<Float>> label : labelsInRange)
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
