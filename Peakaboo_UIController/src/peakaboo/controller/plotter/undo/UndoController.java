package peakaboo.controller.plotter.undo;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.PlotController.UpdateType;
import eventful.Eventful;
import fava.datatypes.Pair;



public class UndoController extends Eventful
{

	PlotController	plotController;
	UndoModel		undoModel;


	public UndoController(PlotController plotController)
	{
		this.plotController = plotController;
		undoModel = new UndoModel();
	}


	public void setUndoPoint(String change)
	{
		//save the current state
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		plotController.savePreferences(baos);

		if (undoModel.undoStack.size() > 0)
		{
			byte[] lastState = undoModel.undoStack.peek().second.toByteArray();
			byte[] thisState = baos.toByteArray();


			//if these two are the same size, lets compare them -- if they are identical, we don't bother saving the state
			if (thisState.length == lastState.length)
			{
				boolean same = true;
				for (int i = 0; i < thisState.length; i++)
				{
					if (lastState[i] != thisState[i])
					{
						same = false;
						break;
					}
				}

				if (same) return;

			}
		}


		//if the last change description is the same as this one, but isnt blank
		//replace the last undo state with this one instead of adding it on top of
		if (undoModel.undoStack.size() > 0 && undoModel.undoStack.peek().first.equals(change) && (!change.equals("")))
		{
			undoModel.undoStack.pop();
		}
		undoModel.undoStack.push(new Pair<String, ByteArrayOutputStream>(change, baos));

		undoModel.redoStack.clear();

	}


	public String getNextUndo()
	{
		if (undoModel.undoStack.size() < 2) return "";

		return undoModel.undoStack.peek().first;
	}


	public String getNextRedo()
	{
		if (undoModel.redoStack.isEmpty()) return "";

		return undoModel.redoStack.peek().first;

	}


	public void undo()
	{
		if (undoModel.undoStack.size() < 2) return;

		undoModel.redoStack.push(undoModel.undoStack.pop());

		plotController.loadPreferences(new ByteArrayInputStream(undoModel.undoStack.peek().second.toByteArray()), true);

		updateListeners();
		plotController.settingsController.updateListeners();
		plotController.filteringController.updateListeners();
		plotController.fittingController.updateListeners(true);

	}


	public void redo()
	{

		if (undoModel.redoStack.isEmpty()) return;

		undoModel.undoStack.push(undoModel.redoStack.pop());

		plotController.loadPreferences(new ByteArrayInputStream(undoModel.undoStack.peek().second.toByteArray()), true);

		updateListeners();
		plotController.settingsController.updateListeners();
		plotController.filteringController.updateListeners();
		plotController.fittingController.updateListeners(true);

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
