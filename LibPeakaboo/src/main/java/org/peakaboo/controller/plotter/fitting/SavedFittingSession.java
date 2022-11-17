package org.peakaboo.controller.plotter.fitting;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.SerializedTransitionSeries;

public class SavedFittingSession {

	public List<SerializedTransitionSeries> fittings;
	
	public Map<SerializedTransitionSeries, String> annotations;
	
	//Class name for FittingSolver
	public String solver;
	
	//Class name for CurveFitter
	public String fitter;
	
	//Class name for FittingFunction
	public String function;
	
	//Energy calibration
	public float minEnergy;
	public float maxEnergy;
	
	//Escape type
	public DetectorMaterialType escape;
	
	//Peak Model parameters
	public float fwhmBase;
	
	public boolean showEscapePeaks=true;
	
	
	public SavedFittingSession storeFrom(FittingController controller) {
		
		//Save fitting selections
		fittings = controller.fittingModel.selections.getFittedTransitionSeries()
				.stream()
				.map(SerializedTransitionSeries::new)
				.collect(toList());
		
		annotations = new HashMap<>();
		for (ITransitionSeries ts : controller.getAnnotations().keySet()) {
			SerializedTransitionSeries sts = new SerializedTransitionSeries(ts);
			annotations.put(sts, controller.getAnnotation(ts));
		}
		
		//Save multi-curve fitting solver name
		solver = controller.getFittingSolver().getClass().getName();
		
		//Save single-curve fitter
		fitter = controller.getCurveFitter().getClass().getName();
		
		//Save the fitting function
		function = controller.getFittingFunction().getName();
		
		//Save energy calibration
		minEnergy = controller.getMinEnergy();
		maxEnergy = controller.getMaxEnergy();
		
		//Save escape peak type
		escape = controller.getDetectorMaterial();
		
		//Save peak model base width
		fwhmBase = controller.getFWHMBase();
		
		showEscapePeaks = controller.getShowEscapePeaks();
		
		return this;
	}
	
	public List<String> loadInto(FittingController controller) {
	
		List<String> errors = new ArrayList<>();
	
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		controller.fittingModel.selections.clear();
		for (SerializedTransitionSeries sts : this.fittings) {
			Optional<ITransitionSeries> newTs = sts.deserialize();
			if (! newTs.isPresent()) { continue; }
			controller.fittingModel.selections.addTransitionSeries(newTs.get());
		}
		
		controller.clearAnnotations();
		if (annotations != null) {
			for (SerializedTransitionSeries sts : annotations.keySet()) {
				Optional<ITransitionSeries> newTs = sts.deserialize();
				if (! newTs.isPresent()) { continue; }
				controller.setAnnotation(newTs.get(), annotations.get(sts));
			}
		}
		
		
		//Restore the FittingSolver
		Class<? extends FittingSolver> fittingSolverClass;
		try {
			fittingSolverClass = (Class<? extends FittingSolver>) Class.forName(solver);
			controller.fittingModel.fittingSolver = fittingSolverClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException | RuntimeException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to find Fitting Solver " + solver, e);
		}
		
		//Restore CurveFitter
		Class<? extends CurveFitter> curveFitterClass;
		try {
			curveFitterClass = (Class<? extends CurveFitter>) Class.forName(fitter);
			controller.fittingModel.curveFitter = curveFitterClass.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			String[] parts = fitter.split("\\.");
			String shortname = parts[parts.length-1];
			errors.add("Failed to find Curve Fitter " + shortname);
		} catch (ReflectiveOperationException | RuntimeException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to find Curve Fitter " + fitter, e);
		}
		
		//Restore the fitting function
		Class<? extends FittingFunction> fittingFunctionClass ;
		try {
			fittingFunctionClass = (Class<? extends FittingFunction>) Class.forName(function);
			controller.fittingModel.selections.getFittingParameters().setFittingFunction(fittingFunctionClass);
			controller.fittingModel.proposals.getFittingParameters().setFittingFunction(fittingFunctionClass);
		} catch (ClassNotFoundException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to find Fitting Function " + function, e);
		}
		
		
		//Restore energy calibration
		controller.setMinMaxEnergy(minEnergy, maxEnergy);
		

		//Restore escape peak type
		controller.setDetectorMaterial(escape);
		
		
		//Restore peak model base width
		controller.setFWHMBase(fwhmBase);
		
		
		controller.setShowEscapePeaks(showEscapePeaks);
		
		return errors;
		
	}
	
}
