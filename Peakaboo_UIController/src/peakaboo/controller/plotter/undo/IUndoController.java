package peakaboo.controller.plotter.undo;

import eventful.IEventful;


public interface IUndoController extends IEventful
{

	public void undo();
	public void redo();
	
	public boolean canUndo();
	public boolean canRedo();

	public String getNextUndo();
	public String getNextRedo();
	
	public void setUndoPoint(String s);
	public void clearUndos();
	
}
