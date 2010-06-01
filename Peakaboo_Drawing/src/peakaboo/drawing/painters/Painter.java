package peakaboo.drawing.painters;



/**
 * A Painter is a general way to define a method of drawing.
 * 
 * @author Nathaniel Sherry, 2009
 */

public abstract class Painter
{

	protected abstract double getBaseUnitSize(peakaboo.drawing.DrawingRequest dr);


	public final void draw(PainterData p)
	{
		// makes sure that a painter can't mess up the save/restore balance by saving more than restoring.
		// this still doesn't prevent restoring more than saving, though, although that should cause a null pointer
		// exception somewhere, so that would be more noticable
		
		int stackPointer = p.context.saveWithMarker();
		drawElement(p);
		p.context.restoreFromMarker(stackPointer);

	}


	public abstract void drawElement(PainterData p);

}
