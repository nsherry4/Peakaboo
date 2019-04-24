package org.peakaboo.controller.plotter.calibration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.calibration.CalibrationReference;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.fitter.OptimizingCurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.MultisamplingOptimizingFittingSolver;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.framework.eventful.Eventful;

public class CalibrationController extends Eventful {

	private CalibrationModel model = new CalibrationModel();
	private PlotController plotController;
	

	public CalibrationController(PlotController plotController) {
		this.plotController = plotController;
	}
	
	/**
	 * Replaces fittings and settings with those used to create a new calibration
	 * profile based on the given calibration reference
	 */
	public void loadCalibrationReference(CalibrationReference ref) {
		plotController.fitting().clearTransitionSeries();
		List<ITransitionSeries> tss = new ArrayList<>(ref.getTransitionSeries());
		tss.sort((a, b) -> a.getElement().compareTo(b.getElement()));
		
		//CalibrationReferences use blank TransitionSeries so it's not limited by the peaktable data
		//we have to convert here
		//TODO: Should the controller convert all added transitionseries to ones from the PeakTable?
		tss = tss.stream().map(ts -> PeakTable.SYSTEM.get(ts)).filter(ts -> ts != null).collect(Collectors.toList());
		plotController.fitting().addAllTransitionSeries(tss);
		//TODO: Should we be doing this, or should the user be doing it?
		plotController.fitting().setFittingSolver(new MultisamplingOptimizingFittingSolver());
		plotController.fitting().setCurveFitter(new OptimizingCurveFitter());
		
		setCalibrationReference(ref);
	}
	
	public void setCalibrationReference(CalibrationReference ref) {
		model.calibrationReference = ref;
		updateListeners();
	}
	
	public void clearCalibrationReference() {
		model.calibrationReference = null;
		updateListeners();
	}
	
	public CalibrationReference getCalibrationReference() {
		return model.calibrationReference;
	}

	public boolean hasCalibrationReference() {
		if (model.calibrationReference == null || model.calibrationReference.hasConcentrations()) {
			return false;
		}		
		return true;
	}
	
	public CalibrationProfile generateCalibrationProfile() {
		
		CalibrationReference reference = getCalibrationReference();
		FittingResultSet sample = plotController.fitting().getFittingSelectionResults();
		if (reference == null || sample == null) {
			return new CalibrationProfile();
		}
		CalibrationProfile profile = new CalibrationProfile(reference, sample);
		
		String name = reference.getName();
		Optional<Metadata> metadata = plotController.data().getDataSet().getMetadata();
		if (metadata.isPresent()) {
			String lab = metadata.get().getLaboratoryName();
			if (lab != null && lab.length() > 0) {
				name += " from " + lab;
			}
			String instrument = metadata.get().getInstrumentName();
			if (instrument != null && instrument.length() > 0) {
				name += " on " + instrument;
			}
			
			String date = metadata.get().getStartTime();
			if (date != null && date.length() > 0) {
				name += " at " + date;
			}
		}
		
		profile.setName(name);
		return profile;
	}

	public boolean hasCalibrationProfile() {
		//if we have a reference, then we're in the middle 
		//of creating, so we always have a profile
		if (hasCalibrationReference()) {
			return true;
		}
		
		if (this.model.calibrationProfile.isEmpty()) {
			return false;
		}
		
		return true;
		
	}
	
	public CalibrationProfile getCalibrationProfile() {
		if (!hasCalibrationReference()) {
			return this.model.calibrationProfile;
		} else {
			return this.generateCalibrationProfile();
		}
	}
	
	public void setCalibrationProfile(CalibrationProfile zprofile, File source) {
		if (zprofile == null) {
			//null means blank profile
			zprofile = new CalibrationProfile();
		}
		
		this.model.calibrationProfile = zprofile;
		this.model.calibrationProfileFile = source;
		
		//If we're loading a profile, we're not creating one
		//this will also call updateListeners for us
		clearCalibrationReference();
		
	}

	public File getCalibrationProfileFile() {
		if (!hasCalibrationReference()) {
			return this.model.calibrationProfileFile;
		} else {
			return null;
		}
	}


	
	
}
