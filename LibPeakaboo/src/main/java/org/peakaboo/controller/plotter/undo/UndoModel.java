package org.peakaboo.controller.plotter.undo;

import java.util.Stack;


public class UndoModel
{

	public Stack<UndoPoint>	undoStack;
	public Stack<UndoPoint>	redoStack;
	
	
	public UndoModel()
	{
		undoStack = new Stack<UndoPoint>();
		redoStack = new Stack<UndoPoint>();
	}

	
	
}
