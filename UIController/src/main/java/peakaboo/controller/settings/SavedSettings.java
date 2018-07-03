package peakaboo.controller.settings;



import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.filtering.FilteringModel;
import peakaboo.controller.plotter.fitting.FittingModel;
import peakaboo.controller.plotter.view.SessionViewModel;
import peakaboo.controller.plotter.view.ViewModel;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.SerializedFilter;
import peakaboo.filter.plugins.noise.SpringNoiseFilter;



/**
 * This class acts as a struct for serialization and allows us to (de)serialize a single object and hava a single
 * serialVersionUID for both session and persistent settings
 * 
 * @author Nathaniel Sherry, 2018
 */

public class SavedSettings
{

	public SavedSession						session;
	public SavedPersistence					persistent;

	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedSettings deserialize(String yaml) {
		return SettingsSerializer.deserialize(yaml);
	}
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		return SettingsSerializer.serialize(this);
	}
	
	/**
	 * applies serialized preferences to the model
	 */
	public void loadInto(PlotController plotController) {
		
		this.session.loadInto(plotController);
		this.persistent.loadInto(plotController);
		
	}

	
	
	/**
	 * Builds a SavedSettings object from the model
	 */
	public static SavedSettings storeFrom(PlotController plotController) {
		SavedSettings saved = new SavedSettings();

		saved.session = SavedSession.storeFrom(plotController);
		saved.persistent = SavedPersistence.storeFrom(plotController);

		return saved;
	}
	
	public static void main(String[] args) {
		
		Yaml y = new Yaml();
		Filter filter = new SpringNoiseFilter();
		SerializedFilter serial = new SerializedFilter(filter);
			
		y.dump(serial);
		y.dump(filter);
	}



	
}
