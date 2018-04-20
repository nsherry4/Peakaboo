package peakaboo.controller.settings;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.settings.PersistentSettingsModel;

public class SavedPersistence {

	public PersistentSettingsModel persistent;
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedPersistence deserialize(String yaml) {
		Yaml y = new Yaml();
		SavedPersistence data = (SavedPersistence)y.load(yaml);
		return data;
	}
	
	
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		Yaml y = new Yaml(options);	
		return y.dump(this);
	}
	
	/**
	 * Builds a SavedPersistence object from the model
	 */
	public static SavedPersistence pack(PlotController plotController) {
		SavedPersistence saved = new SavedPersistence();
		saved.persistent = plotController.settings().getSettingsModel().persistent;
		return saved;
	}
	
	/**
	 * applies serialized preferences to the model
	 */
	public static void unpack(SavedPersistence data, PlotController plotController) {
		plotController.settings().getSettingsModel().persistent = data.persistent;
	}
	
}
