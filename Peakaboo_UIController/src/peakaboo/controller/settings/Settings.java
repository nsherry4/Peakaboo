package peakaboo.controller.settings;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import peakaboo.controller.plotter.PlotModel;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.filters.AbstractFilter;

/**
 * 
 * This class is responsible for (de)serialization of preferences. A given data set will have things such as
 * user view preferences, curve fittings, filters, etc.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class Settings
{

	/**
	 * 
	 * loads preferences from a file, and applies them to the given model
	 * 
	 * @param model model to apply the loaded preferences to
	 * @param filename name of the preferences file
	 */
	public static void loadPreferences(PlotModel model, String filename)
	{

		FileInputStream fin = null;
		ObjectInputStream in = null;

		Object read;

		SerializedData data = new SerializedData();

		try {
			fin = new FileInputStream(filename);
			in = new ObjectInputStream(fin);

			read = in.readObject();
			if (read == null) return;
			data = (SerializedData) read;

			// load transition series
			model.fittingSelections.clear();
			for (TransitionSeries ts : data.fittings) {
				model.fittingSelections.addTransitionSeries(ts);
			}

			// load filters
			model.filters.clearFilters();
			for (AbstractFilter f : data.filters) {
				model.filters.addFilter(f);
			}

			// read in the drawing request
			model.dr = data.dr;
			model.viewOptions = data.viewOptions;


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return;
	}


	/**
	 * 
	 * Saves preferences to a file, as read from the given model
	 * 
	 * @param model model to read the saved preferences from
	 * @param filename name of the preferences file
	 */
	public static void savePreferences(PlotModel model, String filename)
	{

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		SerializedData data = new SerializedData();
		data.fittings = model.fittingSelections.getFittedTransitionSeries();
		data.filters = DataTypeFactory.<AbstractFilter> list();
		for (AbstractFilter f : model.filters) {
			data.filters.add(f);
		}
		
		data.dr = model.dr;
		data.viewOptions = model.viewOptions;

		try {
			fos = new FileOutputStream(filename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			out = new ObjectOutputStream(fos);

			// Write out the SerializedData object
			out.writeObject(data);

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}



	}
}
