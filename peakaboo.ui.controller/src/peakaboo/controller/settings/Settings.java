package peakaboo.controller.settings;



import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import fava.functionable.FStringInput;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.controller.plotter.data.IDataController;
import peakaboo.controller.plotter.settings.SettingsModel;
import peakaboo.curvefit.model.FittingModel;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilteringModel;
import peakaboo.filter.model.SerializedFilter;



/**
 * This class is responsible for (de)serialization of preferences. A given data set will have things such as user view
 * preferences, curve fittings, filters, etc.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class Settings
{

	/**
	 * loads preferences from a file, and applies them to the given model
	 * 
	 * @param model
	 *            model to apply the loaded preferences to
	 * @param filename
	 *            name of the preferences file
	 */
	public static void loadPreferences(
			IPlotController plotController,
			IDataController dataController,
			final SettingsModel settings,
			final FittingModel fittings,
			final FilteringModel filters,
			InputStream inStream)
	{

		SerializedData data;
		String yaml = FStringInput.contents(inStream); //IOOperations.readerToString(new BufferedReader(new InputStreamReader(inStream)));
		data = SerializedData.deserialize(yaml);
		
		// load transition series
		fittings.selections.clear();		
		
		
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		for (SerializedTransitionSeries sts : data.fittings)
		{
			fittings.selections.addTransitionSeries(sts.toTS());
		}

		
		// load filters
		filters.filters.clearFilters();
		for (SerializedFilter f : data.filters)
		{
			filters.filters.addFilter(f.getFilter());
		}

		
		System.out.println("bad scans restore");
		dataController.getDiscards().clear();
		for (Integer i : data.badScans)
		{
			if (  (dataController.hasDataSet() && dataController.getDataSet().getScanData().scanCount() > i)  ) {
				dataController.getDiscards().discard(i);
			}
		}
		
		
		// read in the drawing request
		plotController.setDR(data.drawingRequest);
		settings.copy( data.settings );
		
		
		if (dataController.hasDataSet()) {
			fittings.selections.setDataParameters(dataController.getDataSet().channelsPerScan(), plotController.getDR().unitSize, settings.escape);
			fittings.proposals.setDataParameters(dataController.getDataSet().channelsPerScan(), plotController.getDR().unitSize, settings.escape);
		}


		return;
	}


	/**
	 * Saves preferences to a file, as read from the given model
	 * 
	 * @param model
	 *            model to read the saved preferences from
	 * @param filename
	 *            name of the preferences file
	 */
	public static void savePreferences(
			final IPlotController plotController,
			final SettingsModel settings,
			final FittingModel fittings,
			final FilteringModel filters,
			OutputStream outStream)
	{


		SerializedData data = new SerializedData();

		//map our list of TransitionSeries to SerializedTransitionSeries since we can't use the
		//yaml library to build TransitionSeries
		data.fittings = fittings.selections.getFittedTransitionSeries().stream().map(ts -> new SerializedTransitionSeries(ts)).collect(toList());
		
		//get a copy of the in-use/created filters
		data.storeFilters(filters.filters.getFilters());
		
		
		//other structs
		data.drawingRequest = plotController.getDR();
		data.settings = settings;

		data.badScans = plotController.data().getDiscards().list();

		//try writing the serialized data
		try
		{

			OutputStreamWriter osw = new OutputStreamWriter(outStream);	
			osw.write(data.serialize());
			osw.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}






	}
}
