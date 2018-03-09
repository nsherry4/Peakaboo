package peakaboo.controller.plotter.undo;


import eventful.Eventful;
import peakaboo.controller.plotter.PlotController;
import scitypes.Pair;



public class UndoController extends Eventful implements IUndoController
{

	PlotController	plot;
	UndoModel		undoModel;


	public UndoController(PlotController plotController)
	{
		this.plot = plotController;
		undoModel = new UndoModel();
	}

	@Override
	public void setUndoPoint(String change)
	{
		//save the current state
		String saved = plot.saveSettings();

		if (undoModel.undoStack.size() > 0)
		{
			String lastState = undoModel.undoStack.peek().second;
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
		if (undoModel.undoStack.size() > 0 && undoModel.undoStack.peek().first.equals(change) && (!change.equals("")))
		{
			undoModel.undoStack.pop();
		}
		undoModel.undoStack.push(new Pair<>(change, saved));

		undoModel.redoStack.clear();

	}

	
	@Override
	public String getNextUndo()
	{
		if (undoModel.undoStack.size() < 2) return "";

		return undoModel.undoStack.peek().first;
	}


	@Override
	public String getNextRedo()
	{
		if (undoModel.redoStack.isEmpty()) return "";

		return undoModel.redoStack.peek().first;

	}


	@Override
	public void undo()
	{
		if (undoModel.undoStack.size() < 2) return;

		undoModel.redoStack.push(undoModel.undoStack.pop());

		plot.loadSettings(undoModel.undoStack.peek().second, true);

		updateListeners();

	}


	@Override
	public void redo()
	{

		if (undoModel.redoStack.isEmpty()) return;

		undoModel.undoStack.push(undoModel.redoStack.pop());

		plot.loadSettings(undoModel.undoStack.peek().second, true);

		updateListeners();
		plot.settings().updateListeners();
		plot.filtering().updateListeners();
		plot.fitting().updateListeners(true);

	}


	@Override
	public boolean canUndo()
	{
		return undoModel.undoStack.size() >= 2;
	}


	@Override
	public boolean canRedo()
	{
		return !undoModel.redoStack.isEmpty();
	}


	@Override
	public void clearUndos()
	{
		undoModel.undoStack.clear();
		setUndoPoint("");
	}


}
