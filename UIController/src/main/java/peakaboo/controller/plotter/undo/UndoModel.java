package peakaboo.controller.plotter.undo;

import java.util.Stack;

import scitypes.Pair;


public class UndoModel
{

	public Stack<Pair<String, String>>	undoStack;
	public Stack<Pair<String, String>>	redoStack;
	
	
	public UndoModel()
	{
		undoStack = new Stack<Pair<String, String>>();
		redoStack = new Stack<Pair<String, String>>();
	}

	
	
}
