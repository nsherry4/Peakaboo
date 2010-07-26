package peakaboo.controller.settings;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


import commonenvironment.IOOperations;
import fava.Fn;
import fava.Functions;
import fava.signatures.FunctionMap;



import peakaboo.controller.plotter.PlotModel;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.filter.AbstractFilter;



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
	public static void loadPreferences(final PlotModel model, InputStream inStream)
	{

		SerializedData data;
		String yaml = IOOperations.readerToString(new BufferedReader(new InputStreamReader(inStream)));
		data = SerializedData.deserialize(yaml);
		
		// load transition series
		model.fittingSelections.clear();		
		
		
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		for (SerializedTransitionSeries sts : data.fittings)
		{
			model.fittingSelections.addTransitionSeries(sts.toTS());
		}

		
		// load filters
		model.filters.clearFilters();
		for (AbstractFilter f : data.filters)
		{
			model.filters.addFilter(f);
		}

		
		// read in the drawing request
		model.dr = data.drawingRequest;
		model.viewOptions = data.viewOptions;
		
		
		if (model.dataset.hasData()) model.fittingSelections.setDataParameters(model.dataset.scanSize(), model.dr.unitSize, model.viewOptions.escape);


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
	public static void savePreferences(PlotModel model, OutputStream outStream)
	{


		SerializedData data = new SerializedData();

		//map our list of TransitionSeries to SerializedTransitionSeries since we can't use the
		//yaml library to build TransitionSeries
		data.fittings = Fn.map(
				model.fittingSelections.getFittedTransitionSeries(), 
				new FunctionMap<TransitionSeries, SerializedTransitionSeries>() {

					public SerializedTransitionSeries f(TransitionSeries ts)
					{
						return new SerializedTransitionSeries(ts);
					}
				});
		
		//map the filters from a FilterSet to a list
		data.filters = Fn.map(model.filters, Functions.<AbstractFilter>id());
		
		
		//other structs
		data.drawingRequest = model.dr;
		data.viewOptions = model.viewOptions;


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
