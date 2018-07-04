package peakaboo.controller.plotter.undo;

public class UndoPoint {

	private String name;
	private String state;
	
	public UndoPoint(String name, String state) {
		this.name = name;
		this.state = state;
	}
	
	public String getName() {
		return name;
	}
	
	public String getState() {
		return state;
	}
	
	
	
}
