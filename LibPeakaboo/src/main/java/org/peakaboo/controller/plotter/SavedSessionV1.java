package org.peakaboo.controller.plotter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.Version;
import org.peakaboo.calibration.SavedCalibrationSessionV1;
import org.peakaboo.controller.plotter.data.SavedDataSessionV1;
import org.peakaboo.controller.plotter.filtering.SavedFilteringSessionV1;
import org.peakaboo.controller.plotter.fitting.SavedFittingSessionV1;
import org.peakaboo.controller.plotter.view.SessionViewModel;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.druthers.DruthersStorable;

/**
 * Stores session settings which are saved/loaded when the user chooses to
 * @author NAS
 *
 */
@Deprecated(since = "6", forRemoval = true)
public class SavedSessionV1 implements DruthersStorable {

	public SavedDataSessionV1 data;
	public SavedFilteringSessionV1 filtering;
	public SavedFittingSessionV1 fitting;
	public SessionViewModel view;
	public SavedCalibrationSessionV1 calibration;
	public String version = Version.LONG_VERSION;
	
	/**
	 * applies serialized preferences to the model
	 */
	@Deprecated(since = "6", forRemoval = true)
	public List<String> loadInto(PlotController plotController) {
		
		List<String> errors = new ArrayList<>();
		
		//restore data settings
		this.data.loadInto(plotController.data());
		
		//restore filtering settings
		this.filtering.loadInto(plotController.filtering(), errors);
		
		//restore fitting settings
		errors.addAll(this.fitting.loadInto(plotController.fitting()));
		
		//restore calibration information
		if (this.calibration == null) {
			this.calibration = new SavedCalibrationSessionV1();
		}
		try {
			plotController.calibration().loadSavedV1(this.calibration);
		} catch (IOException e) {
			OneLog.log(Level.SEVERE, "Failed to load detector calibration session", e);
		}
		
		
		//restore view settings directly, since it's model is serializable
		plotController.view().getViewModel().copy(this.view);

		return errors;
		
	}
	
}
