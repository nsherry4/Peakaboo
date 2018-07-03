package peakaboo.controller.plotter.fitting;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.controller.settings.SerializedTransitionSeries;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;

public class SavedFittingSession {

	public List<SerializedTransitionSeries> fittings;
	//Class name for FittingSolver
	public String solver;
	//Class name for CurveFitter
	public String fitter;
	
	public SavedFittingSession storeFrom(FittingController controller) {
		
		//Save fitting selections
		fittings = controller.fittingModel.selections.getFittedTransitionSeries()
				.stream()
				.map(ts -> new SerializedTransitionSeries(ts))
				.collect(toList());
		
		//Save multi-curve fitting solver name
		solver = controller.getFittingSolver().getClass().getName();
		
		//Save single-curve fitter
		fitter = controller.getCurveFitter().getClass().getName();
		
		return this;
	}
	
	public SavedFittingSession loadInto(FittingController controller) {
				
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		controller.fittingModel.selections.clear();
		for (SerializedTransitionSeries sts : this.fittings) {
			controller.fittingModel.selections.addTransitionSeries(sts.toTS());
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
		
		
		
		return this;
	}
	
}
