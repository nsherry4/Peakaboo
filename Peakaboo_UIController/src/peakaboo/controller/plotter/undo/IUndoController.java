package peakaboo.controller.plotter.undo;

import eventful.IEventful;


public interface IUndoController extends IEventful
{

	void undo();
	void redo();
	
	boolean canUndo();
	boolean canRedo();

	String getNextUndo();
	String getNextRedo();
	
	void setUndoPoint(String s);
	void clearUndos();
	
}
