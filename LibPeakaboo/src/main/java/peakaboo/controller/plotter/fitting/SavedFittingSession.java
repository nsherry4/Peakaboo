package peakaboo.controller.plotter.fitting;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.controller.settings.SerializedTransitionSeries;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.fitting.FittingFunction;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

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
	public EscapePeakType escape;
	
	//Peak Model parameters
	public float fwhmBase;
	
	
	public SavedFittingSession storeFrom(FittingController controller) {
		
		//Save fitting selections
		fittings = controller.fittingModel.selections.getFittedTransitionSeries()
				.stream()
				.map(ts -> new SerializedTransitionSeries(ts))
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
		escape = controller.getEscapeType();
		
		//Save peak model base width
		fwhmBase = controller.getFWHMBase();
		
		return this;
	}
	
	public SavedFittingSession loadInto(FittingController controller) {
				
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		controller.fittingModel.selections.clear();
		for (SerializedTransitionSeries sts : this.fittings) {
			controller.fittingModel.selections.addTransitionSeries(sts.toTS());
		}
		
		controller.clearAnnotations();
		if (annotations != null) {
			for (SerializedTransitionSeries sts : annotations.keySet()) {
				ITransitionSeries ts = sts.toTS();
				controller.setAnnotation(ts, annotations.get(sts));
			}
		}
		
		
		//Restore the FittingSolver
		Class<? extends FittingSolver> fittingSolverClass;
		try {
			fittingSolverClass = (Class<? extends FittingSolver>) Class.forName(solver);
			controller.fittingModel.fittingSolver = fittingSolverClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to find Fitting Solver " + solver, e);
		}
		
		//Restore CurveFitter
		Class<? extends CurveFitter> curveFitterClass;
		try {
			curveFitterClass = (Class<? extends CurveFitter>) Class.forName(fitter);
			controller.fittingModel.curveFitter = curveFitterClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
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
		controller.setMaxEnergy(maxEnergy);
		controller.setMinEnergy(minEnergy);
		

		//Restore escape peak type
		controller.setEscapeType(escape);
		
		
		//Restore peak model base width
		controller.setFWHMBase(fwhmBase);
		
		return this;
	}
	
}
