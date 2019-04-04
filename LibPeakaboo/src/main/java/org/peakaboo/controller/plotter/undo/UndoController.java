package org.peakaboo.controller.plotter.undo;


import java.util.Stack;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.eventful.Eventful;


public class UndoController extends Eventful
{

	PlotController	plot;
	
	/*
	 * Stores undos and redos. The most recent entry on the undo stack should be the
	 * current state
	 */
	private Stack<UndoPoint> undoStack;
	private Stack<UndoPoint> redoStack;
	
	private UndoPoint lastSave;
	

	public UndoController(PlotController plotController)
	{
		this.plot = plotController;
		undoStack = new Stack<UndoPoint>();
		redoStack = new Stack<UndoPoint>();
		lastSave = null;
		
	}

	public void setUndoPoint(String change)
	{
		//save the current state
		String saved = plot.getSavedSettings().serialize();

		if (undoStack.size() > 0)
		{
			String lastState = undoStack.peek().getState();
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
		UndoPoint undoable = new UndoPoint(change, saved);
		if (undoStack.size() > 0 && undoStack.peek().getName().equals(change) && (!change.equals("")))
		{
			UndoPoint replaced = undoStack.pop();
			if (replaced == lastSave) {
				lastSave = undoable;
			}
		}
		undoStack.push(undoable);

		redoStack.clear();

	}

	
	public String getNextUndo()
	{
		if (undoStack.size() < 2) return "";

		return undoStack.peek().getName();
	}


	public String getNextRedo()
	{
		if (redoStack.isEmpty()) return "";

		return redoStack.peek().getName();

	}


	public void undo()
	{
		if (undoStack.size() < 2) return;

		redoStack.push(undoStack.pop());

		plot.loadSettings(undoStack.peek().getState(), true);

		updateListeners();

	}


	public void redo()
	{

		if (redoStack.isEmpty()) return;

		undoStack.push(redoStack.pop());

		plot.loadSettings(undoStack.peek().getState(), true);

		updateListeners();
		plot.view().updateListeners();
		plot.filtering().updateListeners();
		plot.fitting().updateListeners(true);

	}


	public boolean canUndo()
	{
		return undoStack.size() >= 2;
	}


	public boolean canRedo()
	{
		return !redoStack.isEmpty();
	}


	public void clearUndos()
	{
		undoStack.clear();
		lastSave = null;
		setUndoPoint("");
	}
	
	/**
	 * Marks this controller as having been saved <i>after</i> the most recent undo point
	 */
	public void setSavePoint() {
		if (undoStack.size() == 0) {
			lastSave = null;
			return;
		}
		lastSave = undoStack.peek();
		updateListeners();
	}
	
	public boolean hasUnsavedWork() {
		if (lastSave == null) {
			return undoStack.size() > 1;
		}
		
		if (lastSave == undoStack.peek()) {
			return false;
		}
		
		return true;
	}


}
