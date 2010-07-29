package peakaboo.controller.plotter;


public interface UndoController
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
