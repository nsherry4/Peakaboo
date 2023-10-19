package org.peakaboo.controller.plotter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Version;
import org.peakaboo.calibration.SavedCalibrationSession;
import org.peakaboo.controller.plotter.data.SavedDataSessionV1;
import org.peakaboo.controller.plotter.filtering.SavedFilteringSessionV1;
import org.peakaboo.controller.plotter.fitting.SavedFittingSessionV1;
import org.peakaboo.controller.plotter.view.SessionViewModel;
import org.peakaboo.framework.druthers.DruthersStorable;

/**
 * Stores session settings which are saved/loaded when the user chooses to
 * @author NAS
 *
 */
public class SavedSessionV1 implements DruthersStorable {

	public SavedDataSessionV1 data;
	public SavedFilteringSessionV1 filtering;
	public SavedFittingSessionV1 fitting;
	public SessionViewModel view;
	public SavedCalibrationSession calibration;
	public String version = Version.longVersionNo;
	
	
	/**
	 * Builds a SavedSession object from the model
	 */
	public static SavedSessionV1 storeFrom(PlotController plotController) {
		
		SavedSessionV1 saved = new SavedSessionV1();
		
		
		//store bad scans
		saved.data = new SavedDataSessionV1().storeFrom(plotController.data());
		
		//store filters
		saved.filtering = new SavedFilteringSessionV1().storeFrom(plotController.filtering());
		
		//store fittings
		saved.fitting = new SavedFittingSessionV1().storeFrom(plotController.fitting());
		
		//store calibration
		saved.calibration = plotController.calibration().toSavedV1();
		
		//store view settings -- this is done differently, since view's session settings is itself serializable
		saved.view = plotController.view().getViewModel();
		
		

		return saved;
	}
	
	/**
	 * applies serialized preferences to the model
	 */
	public List<String> loadInto(PlotController plotController) {
		
		List<String> errors = new ArrayList<>();
		
		//restore data settings
		this.data.loadInto(plotController.data());
		
		//restore filtering settings
		this.filtering.loadInto(plotController.filtering());
		
		//restore fitting settings
		errors.addAll(this.fitting.loadInto(plotController.fitting()));
		
		//restore calibration information
		if (this.calibration == null) {
			this.calibration = new SavedCalibrationSession();
		}
		try {
			plotController.calibration().loadSavedV1(this.calibration);
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load detector calibration session", e);
		}
		
		
		//restore view settings directly, since it's model is serializable
		plotController.view().getViewModel().copy(this.view);

		return errors;
		
	}
	
}
