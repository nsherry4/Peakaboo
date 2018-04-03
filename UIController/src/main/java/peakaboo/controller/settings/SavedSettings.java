package peakaboo.controller.settings;



import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.fitting.FittingModel;
import peakaboo.controller.plotter.settings.SettingsModel;
import peakaboo.curvefit.fitting.EnergyCalibration;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilteringModel;
import peakaboo.filter.model.SerializedFilter;
import peakaboo.filter.plugins.noise.SpringSmoothing;
import scidraw.drawing.DrawingRequest;



/**
 * This class acts as a struct for serialization and allows us to (de)serialize a single object and hava a single
 * serialVersionUID
 * 
 * @author Nathaniel Sherry, 2009
 */

public class SavedSettings
{

	public DrawingRequest					drawingRequest;
	public SettingsModel					settings;
	
	public List<Integer>					badScans;

	
	public List<SerializedFilter>			filters = new ArrayList<>();
	public void storeFilters(List<Filter> filters) {
		this.filters.clear();
		for (Filter filter : filters) {
			this.filters.add(new SerializedFilter(filter));
		}
	}
	
	
	
	
	//Outer List - list of fitting
	//Middle List - list of TS in a fitting
	//Inner List - stand-in for a pair, since this YAML library seems to have trouble serializing pairs
	// the pair contains Element and Transition. eg (Ni, K)
	//public List<List<List<String>>>	fittings;
	public List<SerializedTransitionSeries> fittings;

	
	
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedSettings deserialize(String yaml)
	{
		
		Yaml y = new Yaml();
		SavedSettings data = (SavedSettings)y.load(yaml);
		return data;
		
	}
	
	
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize()
	{
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		
		Yaml y = new Yaml(options);
		
				
		return y.dump(this);
		
	}
	
	/**
	 * applies serialized preferences to the model
	 */
	public static void unpack(SavedSettings data, PlotController plotController)
	{

		SettingsModel settings = plotController.settings().getSettingsModel();
		FittingModel fittings = plotController.fitting().getFittingModel();
		FilteringModel filters = plotController.filtering().getFilteringMode();
		DataController dataController = plotController.data();
		
		// load transition series
		fittings.selections.clear();		
		
		
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		for (SerializedTransitionSeries sts : data.fittings)
		{
			fittings.selections.addTransitionSeries(sts.toTS());
		}

		
		// load filters
		filters.filters.clear();
		for (SerializedFilter f : data.filters)
		{
			filters.filters.add(f.getFilter());
		}

		
		//System.out.println("bad scans restore");
		dataController.getDiscards().clear();
		for (Integer i : data.badScans)
		{
			if (  (dataController.hasDataSet() && dataController.getDataSet().getScanData().scanCount() > i)  ) {
				dataController.getDiscards().discard(i);
			}
		}
		
		
		
		settings.copy( data.settings );
		
		
		if (dataController.hasDataSet()) {
			EnergyCalibration calibration = new EnergyCalibration(settings.minEnergy, settings.maxEnergy, dataController.getDataSet().getAnalysis().channelsPerScan());
			fittings.selections.getFittingParameters().setCalibration(calibration);
			fittings.proposals.getFittingParameters().setCalibration(calibration);
			
			fittings.selections.getFittingParameters().setEscapeType(settings.escape);
			fittings.proposals.getFittingParameters().setEscapeType(settings.escape);
		}


		return;
	}

	
	
	/**
	 * Builds a SavedSettings object from the model
	 */
	public static SavedSettings pack(PlotController plotController) {

		SettingsModel settings = plotController.settings().getSettingsModel();
		FittingModel fittings = plotController.fitting().getFittingModel();
		FilteringModel filters = plotController.filtering().getFilteringMode();
		
		SavedSettings data = new SavedSettings();

		//map our list of TransitionSeries to SerializedTransitionSeries since we can't use the
		//yaml library to build TransitionSeries
		data.fittings = fittings.selections.getFittedTransitionSeries().stream().map(ts -> new SerializedTransitionSeries(ts)).collect(toList());
		
		//get a copy of the in-use/created filters
		data.storeFilters(filters.filters.getFilters());
		
		
		//other structs
		data.settings = settings;

		data.badScans = plotController.data().getDiscards().list();
		
		return data;
	}
	
	public static void main(String[] args) {
		
		Yaml y = new Yaml();
		Filter filter = new SpringSmoothing();
		SerializedFilter serial = new SerializedFilter(filter);
			
		y.dump(serial);
		y.dump(filter);
	}



	
}
