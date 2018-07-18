package peakaboo.controller.plotter.undo;


import eventful.Eventful;
import peakaboo.controller.plotter.PlotController;



public class UndoController extends Eventful
{

	PlotController	plot;
	UndoModel		undoModel;


	public UndoController(PlotController plotController)
	{
		this.plot = plotController;
		undoModel = new UndoModel();
	}

	public void setUndoPoint(String change)
	{
		//save the current state
		String saved = plot.getSavedSettings().serialize();

		if (undoModel.undoStack.size() > 0)
		{
			String lastState = undoModel.undoStack.peek().getState();
			String thisState = saved;

			//if these two states are the same, we don't bother saving the state
			if (thisState.equals(lastState)) {
				return;
			}
		}


		/*
		 * if the last change description is the same as this one, but isn't blank
		 * replace the last undo state with this one instead of adding it on top of.
		 * This allows us to merge several similar quick actions into a single unto
		 * to make navigating the undo stack easier/faster for the user  
		 */
		if (undoModel.undoStack.size() > 0 && undoModel.undoStack.peek().getName().equals(change) && (!change.equals("")))
		{
			undoModel.undoStack.pop();
		}
		undoModel.undoStack.push(new UndoPoint(change, saved));

		undoModel.redoStack.clear();

	}

	
	public String getNextUndo()
	{
		if (undoModel.undoStack.size() < 2) return "";

		return undoModel.undoStack.peek().getName();
	}


	public String getNextRedo()
	{
		if (undoModel.redoStack.isEmpty()) return "";

		return undoModel.redoStack.peek().getName();

	}


	public void undo()
	{
		if (undoModel.undoStack.size() < 2) return;

		undoModel.redoStack.push(undoModel.undoStack.pop());

		plot.loadSettings(undoModel.undoStack.peek().getState(), true);

		updateListeners();

	}


	public void redo()
	{

		if (undoModel.redoStack.isEmpty()) return;

		undoModel.undoStack.push(undoModel.redoStack.pop());

		plot.loadSettings(undoModel.undoStack.peek().getState(), true);

		updateListeners();
		plot.view().updateListeners();
		plot.filtering().updateListeners();
		plot.fitting().updateListeners(true);

	}


	public boolean canUndo()
	{
		return undoModel.undoStack.size() >= 2;
	}


	public boolean canRedo()
	{
		return !undoModel.redoStack.isEmpty();
	}


	public void clearUndos()
	{
		undoModel.undoStack.clear();
		setUndoPoint("");
	}


}
