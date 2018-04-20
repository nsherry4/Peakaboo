package peakaboo.controller.settings;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.fitting.FittingModel;
import peakaboo.controller.plotter.settings.SessionSettingsModel;
import peakaboo.controller.plotter.settings.SettingsModel;
import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilteringModel;
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
		}
		
		
	}
	
}
