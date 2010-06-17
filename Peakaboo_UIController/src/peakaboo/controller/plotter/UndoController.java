package peakaboo.controller.plotter;


public interface UndoController
{

	public void undo();
	public void redo();
	public boolean canUndo();
	public boolean canRedo();
	public void setUndoPoint();
	public void clearUndos();

}
