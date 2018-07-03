package peakaboo.controller.settings;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.data.SavedDataSession;
import peakaboo.controller.plotter.filtering.SavedFilteringSession;
import peakaboo.controller.plotter.fitting.FittingModel;
import peakaboo.controller.plotter.fitting.SavedFittingSession;
import peakaboo.controller.plotter.view.SessionViewModel;
import peakaboo.controller.plotter.view.ViewModel;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.peak.fitting.FittingFunction;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.SerializedFilter;

/**
 * Stores session settings which are saved/loaded when the user chooses to
 * @author NAS
 *
 */
public class SavedSession {

	public SessionViewModel session;
	
	
	public SavedDataSession data;
	public SavedFilteringSession filtering;
	public SavedFittingSession fitting;
	
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedSession deserialize(String yaml) {
		return SettingsSerializer.deserialize(yaml);
	}
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		return SettingsSerializer.serialize(this);
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
		
		
		
		
		
		
		//OLD STYLE
		
		
		saved.session = plotController.view().getViewModel().session;
		

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
		
		
		
		
		FittingModel fittingModel = plotController.fitting().getFittingModel();
		ViewModel settingsModel = plotController.view().getViewModel();
		DataController dataController = plotController.data();
		
		settingsModel.session = this.session;
		
		

		

		
		
		if (dataController.hasDataSet()) {
			EnergyCalibration calibration = new EnergyCalibration(
					settingsModel.session.minEnergy, 
					settingsModel.session.maxEnergy, 
					dataController.getDataSet().getAnalysis().channelsPerScan()
				);
			fittingModel.selections.getFittingParameters().setCalibration(calibration);
			fittingModel.proposals.getFittingParameters().setCalibration(calibration);
			
			fittingModel.selections.getFittingParameters().setEscapeType(settingsModel.session.escape);
			fittingModel.proposals.getFittingParameters().setEscapeType(settingsModel.session.escape);
			
			fittingModel.selections.getFittingParameters().setFWMHBase(settingsModel.session.fwhmBase);
			
			Class<? extends FittingFunction> fittingFunctionClass ;
			try {
				fittingFunctionClass = (Class<? extends FittingFunction>) Class.forName(settingsModel.session.fittingFunctionName);
				fittingModel.selections.getFittingParameters().setFittingFunction(fittingFunctionClass);
				fittingModel.proposals.getFittingParameters().setFittingFunction(fittingFunctionClass);
			} catch (ClassNotFoundException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to find Fitting Function " + settingsModel.session.fittingFunctionName, e);
			}
			
			
			Class<? extends CurveFitter> curveFitterClass;
			try {
				curveFitterClass = (Class<? extends CurveFitter>) Class.forName(settingsModel.session.curveFitterName);
				fittingModel.curveFitter = curveFitterClass.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to find Curve Fitter " + settingsModel.session.curveFitterName, e);
			}
			
			
			
			Class<? extends FittingSolver> fittingSolverClass;
			try {
				fittingSolverClass = (Class<? extends FittingSolver>) Class.forName(settingsModel.session.fittingSolverName);
				fittingModel.fittingSolver = fittingSolverClass.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to find Fitting Solver " + settingsModel.session.fittingSolverName, e);
			}
			
			
		}
		
		
	}
	
}
