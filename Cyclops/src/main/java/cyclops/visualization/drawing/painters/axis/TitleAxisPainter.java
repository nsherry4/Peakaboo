package cyclops.visualization.drawing.painters.axis;

import cyclops.Pair;
import cyclops.visualization.drawing.Drawing;
import cyclops.visualization.drawing.painters.PainterData;

/**
 * Draws titles for all 4 axes. Can be used to draw a title for the whole {@link Drawing} as well.
 * @author Nathaniel Sherry, 2009
 *
 */

public class TitleAxisPainter extends AxisPainter
{

	private String leftTitle, rightTitle, topTitle, bottomTitle;
	private float titleScale;
	
	public TitleAxisPainter(float titleScale, String leftTitle, String rightTitle, String topTitle, String bottomTitle)
	{
		
		super();
		
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.topTitle = topTitle;
		this.bottomTitle = bottomTitle;
		
		this.titleScale = titleScale;
		
	}

	@Override
	public void drawElement(PainterData p)
	{
		Pair<Float, Float> otherAxis;

	
		/*==============================================
		 * X Axis
		 ==============================================*/
		p.context.save();
			
			p.context.setSource(0.0f, 0.0f, 0.0f);
	
			
			float titleWidth, plotWidth, centrepoint, titleFitting;
			otherAxis = getAxisSizeX(p);
	
			//top
			if (topTitle != null){
				
				plotWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxis.first - otherAxis.second;
				
				titleFitting = 1.0f;
				while (true){
					p.context.setFontSize(FONTSIZE_TITLE * titleScale * titleFitting);
					titleWidth = p.context.getTextWidth(topTitle);
					if (titleWidth < plotWidth) break;
					titleFitting -= 0.05;
					if (titleFitting * titleScale * FONTSIZE_TITLE <= 1.0) break;
				}
				
				centrepoint = (plotWidth - titleWidth) / 2.0f;
				p.context.writeText(topTitle, centrepoint, axesData.yPositionBounds.start + p.context.getFontAscent() - p.context.getFontLeading());
			}
				
			//bottom
			if (bottomTitle != null){
				
				plotWidth = axesData.xPositionBounds.end - axesData.xPositionBounds.start - otherAxis.first - otherAxis.second;
	
				titleFitting = 1.0f;
				while (true){
					p.context.setFontSize(FONTSIZE_TITLE * titleScale * titleFitting);
					titleWidth = p.context.getTextWidth(bottomTitle);
					if (titleWidth < plotWidth) break;
					titleFitting -= 0.05;
					if (titleFitting * titleScale * FONTSIZE_TITLE <= 1.0) break;
				}
				
				titleWidth = p.context.getTextWidth(bottomTitle);
				centrepoint = (axesData.xPositionBounds.end - axesData.xPositionBounds.start - titleWidth) / 2.0f;
				p.context.writeText(bottomTitle, centrepoint, axesData.yPositionBounds.end - p.context.getFontDescent() - p.context.getFontLeading());
			}
				
		p.context.restore();
		
		
		/*==============================================
		 * Y Axis
		 ==============================================*/
		
		float plotHeight, titleStart, titleHeight;
		
		if (leftTitle != null) {
			p.context.save();
	
				
				p.context.setSource(0.0f, 0.0f, 0.0f);
				p.context.useSansFont();
				p.context.setFontSize(FONTSIZE_TITLE * titleScale);
		
				p.context.rotate(-3.141592653589793238f / 2.0f);
		
				otherAxis = getAxisSizeY(p);
				
				
				plotHeight = (axesData.yPositionBounds.end - axesData.yPositionBounds.start) - otherAxis.first - otherAxis.second;
				
				titleFitting = 1.0f;
				
				while (true){
					p.context.setFontSize(FONTSIZE_TITLE * titleScale * titleFitting);
					titleWidth = p.context.getTextWidth(leftTitle);
					if (titleWidth < plotHeight) break;
					titleFitting -= 0.05;
					if (titleFitting * titleScale * FONTSIZE_TITLE <= 1.0) break;
				}
				
				titleStart = (plotHeight + titleWidth) / 2.0f + otherAxis.first;
				titleHeight = p.context.getFontLeading() + p.context.getFontAscent();
		
				p.context.writeText(leftTitle, -titleStart, titleHeight);
				
		
				p.context.stroke();
	
			p.context.restore();
		}
		
		
		if (rightTitle != null){
			p.context.save();
		
				
				p.context.setSource(0.0f, 0.0f, 0.0f);
				p.context.useSansFont();
				p.context.setFontSize(FONTSIZE_TITLE * titleScale);
		
				p.context.rotate(3.141592653589793238f / 2.0f);
		
				otherAxis = getAxisSizeY(p);
	
				plotHeight = (axesData.yPositionBounds.end - axesData.yPositionBounds.start) - otherAxis.first - otherAxis.second;
				
				titleFitting = 1.0f;
				while (true){
					p.context.setFontSize(FONTSIZE_TITLE * titleScale * titleFitting);
					titleWidth = p.context.getTextWidth(rightTitle);
					if (titleWidth < plotHeight) break;
					titleFitting -= 0.05;
					if (titleFitting * titleScale * FONTSIZE_TITLE <= 1.0) break;
				}
				
				titleStart = (plotHeight - titleWidth) / 2.0f + otherAxis.first + axesData.yPositionBounds.start;
		
				float textOffsetX = axesData.xPositionBounds.end - getAxisSizeX(p).second + p.context.getFontLeading() + p.context.getFontDescent();
				p.context.writeText(rightTitle, titleStart, -textOffsetX);
				
		
				p.context.stroke();
		
			p.context.restore();
		}
		
		
	}

	@Override
	public Pair<Float,Float> getAxisSizeX(PainterData p)
	{
		float titleHeight = getTitleFontHeight(p.context, p.dr, titleScale) + p.context.getFontLeading();
		return new Pair<Float,Float>(leftTitle != null ? titleHeight : 0.0f, rightTitle != null ? titleHeight : 0.0f);
	}

	@Override
	public Pair<Float,Float> getAxisSizeY(PainterData p)
	{
		float titleHeight = getTitleFontHeight(p.context, p.dr, titleScale) + p.context.getFontLeading();
		return new Pair<Float,Float>(topTitle != null ? titleHeight : 0.0f, bottomTitle != null ? titleHeight : 0.0f);
	}

}
