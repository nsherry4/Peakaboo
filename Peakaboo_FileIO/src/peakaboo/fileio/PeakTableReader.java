package peakaboo.fileio;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Arrays;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.PeakTable;
import peakaboo.datatypes.peaktable.Transition;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.datatypes.peaktable.TransitionType;

/**
 * 
 * This class reads a peak table and generates a PeakTable object
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PeakTableReader
{

	/**
	 * Read a peak table from a predetermined relative location 
	 * @return a populated PeakTable object
	 */
	public static PeakTable readPeakTable()
	{

		PeakTable table = new PeakTable();
		int elementDataWidth = 2;

		InputStream ins = PeakTableReader.class.getResourceAsStream("/peakaboo/fileio/PeakTable.tsv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));



		String line;
		List<String> elements = DataTypeFactory.<String> list();

		try {

			while ((line = reader.readLine()) != null) {
				elements.add(line);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * String types, relintens; types = elementsArray[0]; relintens = elementsArray[1];
		 */
		elements.remove(0);
		elements.remove(0);


		String[] lineSplit;
		List<String> sections;


		String name;
		int number;

		double intensity;
		int atomicNumber = 0;

		for (String element : elements) {

			if (element == null) continue;

			lineSplit = element.split("\t");
			sections = Arrays.asList(lineSplit);

			// name and number
			number = Integer.parseInt(sections.get(0));
			name = sections.get(1);

			int column = 2;

			Transition k, k1, k2, k3, esc;

			// table.addElement( createTransition(sections, name, number,
			// TransitionType.esc, column++) );


			// K
			TransitionSeries ts = new TransitionSeries(Element.values()[atomicNumber], TransitionSeriesType.K);

			esc = createTransition(sections, column);
			column += elementDataWidth;

			// ts.setTransition(TransitionType.esc, esc);

			// ka
			k = createTransition(sections, column);
			column += elementDataWidth;
			k1 = createTransition(sections, column);
			column += elementDataWidth;
			k2 = createTransition(sections, column);
			column += elementDataWidth;

			ts.setTransition(TransitionType.a1, k1);
			ts.setTransition(TransitionType.a2, k2);


			// kB
			k = createTransition(sections, column);
			column += elementDataWidth;
			k1 = createTransition(sections, column);
			column += elementDataWidth;
			k3 = createTransition(sections, column);
			column += elementDataWidth;
			k2 = createTransition(sections, column);
			column += elementDataWidth;

			ts.setTransition(TransitionType.b1, k1);
			ts.setTransition(TransitionType.b2, k2);
			ts.setTransition(TransitionType.b3, k3);

			table.addSeries(ts);


			ts = new TransitionSeries(Element.values()[atomicNumber], TransitionSeriesType.L);
			Transition la, lb1, lb2, lg1, lg2, lg3, lg4, ll;

			esc = createTransition(sections, column);
			column += elementDataWidth;

			la = createTransition(sections, column);
			column += elementDataWidth;

			lb1 = createTransition(sections, column);
			column += elementDataWidth;
			lb2 = createTransition(sections, column);
			column += elementDataWidth;

			lg1 = createTransition(sections, column);
			column += elementDataWidth;
			lg2 = createTransition(sections, column);
			column += elementDataWidth;
			lg3 = createTransition(sections, column);
			column += elementDataWidth;
			lg4 = createTransition(sections, column);
			column += elementDataWidth;

			ll = createTransition(sections, column);
			column += elementDataWidth;


			ts.setTransition(TransitionType.a1, la);

			ts.setTransition(TransitionType.b1, lb1);
			ts.setTransition(TransitionType.b2, lb2);

			ts.setTransition(TransitionType.g1, lg1);
			ts.setTransition(TransitionType.g2, lg2);
			ts.setTransition(TransitionType.g3, lg3);
			ts.setTransition(TransitionType.g4, lg4);

			ts.setTransition(TransitionType._l, ll);

			table.addSeries(ts);


			
			ts = new TransitionSeries(Element.values()[atomicNumber], TransitionSeriesType.M);
			Transition mz, ma1, mb1, mg, mn, unknown;

			esc = createTransition(sections, column);
			column += elementDataWidth;

			mz = createTransition(sections, column);
			column += elementDataWidth;
			unknown = createTransition(sections, column);
			column += elementDataWidth;
			ma1 = createTransition(sections, column);
			column += elementDataWidth;
			mb1 = createTransition(sections, column);
			column += elementDataWidth;
			mg = createTransition(sections, column);
			column += elementDataWidth;
			mn = createTransition(sections, column);
			column += elementDataWidth;
			unknown = createTransition(sections, column);
			column += elementDataWidth;

			
			ts.setTransition(TransitionType.a1, ma1);
			ts.setTransition(TransitionType.b1, mb1);
			ts.setTransition(TransitionType.g1, mg);

			table.addSeries(ts);

			atomicNumber++;

		}

		return table;
	}

	private static Transition createTransition(List<String> sections, int column)
	{

		double energy = 0.0;
		double relIntensity = 0.0;

		try {
			energy = Double.parseDouble(sections.get(column));
			relIntensity = Double.parseDouble(sections.get(column + 1));
		} catch (NumberFormatException e) {
			return null;
		}

		if (energy == 0.0 || relIntensity == 0.0) return null;
		return new Transition(energy, relIntensity / 100.0);

	}

}
