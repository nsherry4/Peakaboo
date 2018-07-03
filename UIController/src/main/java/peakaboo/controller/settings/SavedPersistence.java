package peakaboo.controller.settings;


import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.view.PersistentViewModel;

public class SavedPersistence {

	public PersistentViewModel persistent;
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedPersistence deserialize(String yaml) {
		return SettingsSerializer.deserialize(yaml);
	}
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		return SettingsSerializer.serialize(this);
	}
	
	
	/**
	 * Builds a SavedPersistence object from the model
	 */
	public static SavedPersistence storeFrom(PlotController plotController) {
		SavedPersistence saved = new SavedPersistence();
		saved.persistent = plotController.view().getViewModel().persistent;
		return saved;
	}
	
	/**
	 * applies serialized preferences to the model
	 */
	public void loadInto(PlotController plotController) {
		plotController.view().getViewModel().persistent = this.persistent;
	}
	
}
