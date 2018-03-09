package peakaboo.controller.plotter.undo;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import eventful.Eventful;
import peakaboo.common.PeakabooLog;
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String saved = plot.saveSettings();
		try {
			baos.write(saved.getBytes());
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to write state for undo point", e);
		}

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

		plot.loadSettings(new String(undoModel.undoStack.peek().second.toByteArray()), true);

		updateListeners();

	}


	@Override
	public void redo()
	{

		if (undoModel.redoStack.isEmpty()) return;

		undoModel.undoStack.push(undoModel.redoStack.pop());

		plot.loadSettings(new String(undoModel.undoStack.peek().second.toByteArray()), true);

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
