package peakaboo.controller.plotter.data;

import java.util.List;

public class SavedDataSession {

	public List<Integer> discards;

	
	public SavedDataSession storeFrom(DataController controller) {
		this.discards = controller.getDiscards().list();
		return this;
	}
	
	public SavedDataSession loadInto(DataController controller) {
		controller.getDiscards().clear();
		for (int i : discards) {
			controller.getDiscards().discard(i);
		}
		return this;
	}
	
	
}
