package peakaboo.controller.settings;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.filtering.FilteringModel;
import peakaboo.controller.plotter.fitting.FittingModel;
import peakaboo.controller.plotter.settings.SessionSettingsModel;
import peakaboo.controller.plotter.settings.SettingsModel;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.peak.fitting.FittingFunction;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.SerializedFilter;

public class SavedSession {

	public SessionSettingsModel session;
	public List<Integer> badScans;
	public List<SerializedFilter> filters = new ArrayList<>();
	public List<SerializedTransitionSeries> fittings;
	
	
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
		FilteringModel filterModel = plotController.filtering().getFilteringMode();
		FittingModel fittingsModel = plotController.fitting().getFittingModel();
		
		
		SavedSession saved = new SavedSession();
		saved.session = plotController.settings().getSettingsModel().session;
		
		//store bad scans
		saved.badScans = plotController.data().getDiscards().list();
		
		//store filters
		saved.filters.clear();
		for (Filter filter : filterModel.filters) {
			saved.filters.add(new SerializedFilter(filter));
		}
		
		//store fittings
		//map our list of TransitionSeries to SerializedTransitionSeries since we can't use the
		//yaml library to build TransitionSeries
		saved.fittings = fittingsModel.selections.getFittedTransitionSeries().stream().map(ts -> new SerializedTransitionSeries(ts)).collect(toList());
		
		return saved;
	}
	
	/**
	 * applies serialized preferences to the model
	 */
	public void loadInto(PlotController plotController) {
		FilteringModel filterModel = plotController.filtering().getFilteringMode();
		FittingModel fittingModel = plotController.fitting().getFittingModel();
		SettingsModel settingsModel = plotController.settings().getSettingsModel();
		DataController dataController = plotController.data();
		
		settingsModel.session = this.session;
		
		//restore bad scans
		dataController.getDiscards().clear();
		for (Integer i : this.badScans)
		{
			if (  (dataController.hasDataSet() && dataController.getDataSet().getScanData().scanCount() > i)  ) {
				dataController.getDiscards().discard(i);
			}
		}
		
		
		//restore filters
		filterModel.filters.clear();
		for (SerializedFilter f : this.filters) {
			filterModel.filters.add(f.getFilter());
		}
		
		
		//restore fittings
		// load transition series
		fittingModel.selections.clear();		
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		for (SerializedTransitionSeries sts : this.fittings) {
			fittingModel.selections.addTransitionSeries(sts.toTS());
		}
		
		
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
