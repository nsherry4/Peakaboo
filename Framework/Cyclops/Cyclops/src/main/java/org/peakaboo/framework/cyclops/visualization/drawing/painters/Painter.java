package org.peakaboo.framework.cyclops.visualization.drawing.painters;

import org.peakaboo.framework.cyclops.visualization.Surface.CompositeModes;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;



/**
 * A Painter is a general way to define a method of drawing.
 * 
 * @author Nathaniel Sherry, 2009-2023
 */

public abstract class Painter
{
	
	protected CompositeModes compositeMode;
	protected String sourceName;

	protected Painter()
	{
		compositeMode = CompositeModes.OVER;
	}

	protected abstract float getBaseUnitSize(DrawingRequest dr);


	public final void draw(PainterData p)
	{
		// makes sure that a painter can't mess up the save/restore balance by saving more than restoring.
		// this still doesn't prevent restoring more than saving, though, although that should cause a null pointer
		// exception somewhere, so that would be more noticable
		
		CompositeModes oldMode = p.context.getCompositeMode();
		p.context.setCompositeMode(compositeMode);
		
		int stackPointer = p.context.saveWithMarker();
		drawElement(p);
		p.context.restoreFromMarker(stackPointer);
		
		p.context.setCompositeMode(oldMode);

	}

	
	public CompositeModes getCompositeMode()
	{
		return compositeMode;
	}

	
	public void setCompositeMode(CompositeModes compositeMode)
	{
		this.compositeMode = compositeMode;
	}

	public abstract void drawElement(PainterData p);
	
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

}
