package org.peakaboo.controller.plotter.undo;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.eventful.EventfulBeacon;


public class UndoController extends EventfulBeacon
{

	PlotController	plot;
	
	
	private UndoPoint currentState;
	private Deque<UndoPoint> undoStack;
	private Deque<UndoPoint> redoStack;
	private boolean working = false;
	
	private UndoPoint lastSave;
	

	public UndoController(PlotController plotController)
	{
		this.plot = plotController;
		undoStack = new ArrayDeque<>();
		redoStack = new ArrayDeque<>();
		lastSave = null;
		currentState = null;
		
	}


	/**
	 * Sets whether undo-point recording is suppressed, returning the previous
	 * value so callers can restore it. Used to make bulk operations (e.g. loading
	 * a session) atomic from the undo system's perspective, so sub-controllers
	 * don't each push their own undo point.
	 */
	public boolean suppress(boolean suppress) {
		boolean previous = working;
		working = suppress;
		return previous;
	}


	/**
	 * Re-syncs the recorded current state to the present model without adding an
	 * undo step or disturbing the undo/redo stacks. Use after a bulk change that
	 * deliberately records no undo point (e.g. a session applied over a freshly
	 * opened dataset whose history was just cleared) so that the recorded current
	 * state matches the model. Without this, the next change would push the stale
	 * pre-change state and undoing it would also revert the bulk change.
	 * <p>
	 * No-ops while suppressed (e.g. during undo/redo), so it never disturbs an
	 * in-progress state replay.
	 */
	public void rebaseline() {
		if (working) { return; }
		String saved = plot.save().serialize();
		String name = (currentState != null) ? currentState.getName() : "";
		currentState = new UndoPoint(name, saved);
	}


	public void setUndoPoint(String change, boolean distinctChange) {
		if (working) { return; }
		
		//save the current state
		String saved = plot.save().serialize();

		if (currentState != null)
		{
			String lastState = currentState.getState();
			String thisState = saved;

			//if these two states are the same, we don't bother saving the state
			if (thisState.equals(lastState)) {
				return;
			}
		}


		/*
		 * if the last change description is the same as this one, but isn't blank
		 * replace the current state with this one instead of adding it on top of.
		 * This allows us to merge several similar quick actions into a single undo
		 * to make navigating the undo stack easier/faster for the user  
		 */
		UndoPoint undoable = new UndoPoint(change, saved);
		if (currentState != null && currentState.getName().equals(change) && (!change.equals("")) && !distinctChange) {
			//these changes are the same (in sequence) so don't save the last one
		} else if (currentState != null) {
			//different changes mean we save the last one
			undoStack.push(currentState);
		}
		currentState = undoable;

		redoStack.clear();

	}

	
	public String getNextUndo()
	{
		if (currentState == null) {
			return "";
		}

		return currentState.getName();
	}


	public String getNextRedo()
	{
		if (!canRedo()) return "";

		return redoStack.peek().getName();

	}


	public void undo()
	{
		if (!canUndo()) return;

		boolean wasSuppressed = suppress(true);
		try {
			if (currentState != null) { redoStack.push(currentState); }
			currentState = undoStack.pop();
			plot.load(currentState.getState(), false);
		} catch (DruthersLoadException e) {
			OneLog.log(Level.WARNING, "Could not load application state from undo history", e);
		} finally {
			suppress(wasSuppressed);
		}

		updateListeners();

	}


	public void redo()
	{

		if (!canRedo()) return;

		boolean wasSuppressed = suppress(true);
		try {
			if (currentState != null) { undoStack.push(currentState); }
			currentState = redoStack.pop();
			plot.load(currentState.getState(), false);
		} catch (DruthersLoadException e) {
			OneLog.log(Level.WARNING, "Could not load application state from undo history", e);
		} finally {
			suppress(wasSuppressed);
		}

		updateListeners();
		plot.view().updateListeners();
		plot.filtering().updateListeners();
		plot.fitting().updateListeners(true);

	}


	public boolean canUndo()
	{
		return !undoStack.isEmpty() && currentState != null;
	}


	public boolean canRedo()
	{
		return !redoStack.isEmpty();
	}


	public void clear()
	{
		undoStack.clear();
		redoStack.clear();
		currentState = null;
		lastSave = null;
		setUndoPoint("", /*distinctChange =*/ true);
	}
	
	/**
	 * Marks this controller as having been saved <i>after</i> the most recent undo point
	 */
	public void setSavePoint() {
		if (currentState == null) {
			lastSave = null;
			return;
		}
		lastSave = currentState;
		updateListeners();
	}
	
	public boolean hasUnsavedWork() {
		if (lastSave == null) {
			return currentState != null && !undoStack.isEmpty();
		}
		
		return lastSave != currentState;
	}


}
