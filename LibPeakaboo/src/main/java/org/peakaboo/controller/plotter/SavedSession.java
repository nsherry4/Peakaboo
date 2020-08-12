package org.peakaboo.controller.plotter;

import org.peakaboo.common.Version;
import org.peakaboo.common.YamlSerializer;
import org.peakaboo.controller.plotter.calibration.SavedCalibrationSession;
import org.peakaboo.controller.plotter.data.SavedDataSession;
import org.peakaboo.controller.plotter.filtering.SavedFilteringSession;
import org.peakaboo.controller.plotter.fitting.SavedFittingSession;
import org.peakaboo.controller.plotter.view.SessionViewModel;

/**
 * Stores session settings which are saved/loaded when the user chooses to
 * @author NAS
 *
 */
public class SavedSession {

	
	
	
	public SavedDataSession data;
	public SavedFilteringSession filtering;
	public SavedFittingSession fitting;
	public SessionViewModel view;
	public SavedCalibrationSession calibration;
	public String version = Version.longVersionNo;
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedSession deserialize(String yaml) {
		return YamlSerializer.deserialize(yaml);
	}
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		return YamlSerializer.serialize(this);
	}

	
	
	/**
	 * Builds a SavedSession object from the model
	 */
	public static SavedSession storeFrom(PlotController plotController) {
		
		SavedSession saved = new SavedSession();
		
		
		//store bad scans
		saved.data = new SavedDataSession().storeFrom(plotController.data());
		
		//store filters
		saved.filtering = new SavedFilteringSession().storeFrom(plotController.filtering());
		
		//store fittings
		saved.fitting = new SavedFittingSession().storeFrom(plotController.fitting());
		
		//store calibration
		saved.calibration = new SavedCalibrationSession().storeFrom(plotController.calibration());
		
		//store view settings -- this is done differently, since view's session settings is itself serializable
		saved.view = plotController.view().getViewModel();
		
		

		return saved;
	}
	
	/**
	 * applies serialized preferences to the model
	 */
	public void loadInto(PlotController plotController) {
		
		//restore data settings
		this.data.loadInto(plotController.data());
		
		//restore filtering settings
		this.filtering.loadInto(plotController.filtering());
		
		//restore fitting settings
		this.fitting.loadInto(plotController.fitting());
		
		//restore calibration information
		if (this.calibration == null) {
			this.calibration = new SavedCalibrationSession();
		}
		this.calibration.loadInto(plotController.calibration());
		
		
		//restore view settings directly, since it's model is serializable
		plotController.view().getViewModel().copy(this.view);

	}
	
}
