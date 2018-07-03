package peakaboo.controller.plotter.data;

import java.util.List;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.settings.SavedSession;
import peakaboo.controller.settings.SettingsSerializer;

public class SavedDataSession {

	public List<Integer> discards;
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedSession deserialize(String yaml) {
		return SettingsSerializer.deserialize(yaml);
	}
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		return SettingsSerializer.serialize(this);
	}

	
	public static SavedDataSession storeFrom(DataController controller) {
		SavedDataSession saved = new SavedDataSession();
		saved.discards = controller.getDiscards().list();
		return saved;
	}
	
	public void loadInto(DataController controller) {
		controller.getDiscards().clear();
		for (int i : discards) {
			controller.getDiscards().discard(i);
		}
	}
	
	
}
