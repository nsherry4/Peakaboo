package peakaboo.controller.plotter.undo;

import java.io.ByteArrayOutputStream;
import java.util.Stack;

import scitypes.Pair;


public class UndoModel
{

	public Stack<Pair<String, ByteArrayOutputStream>>	undoStack;
	public Stack<Pair<String, ByteArrayOutputStream>>	redoStack;
	
	
	public UndoModel()
	{
		undoStack = new Stack<Pair<String, ByteArrayOutputStream>>();
		redoStack = new Stack<Pair<String, ByteArrayOutputStream>>();
	}

	
	
}
