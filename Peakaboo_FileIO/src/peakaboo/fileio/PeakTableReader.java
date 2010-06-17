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

		float intensity;
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


			Element e = Element.values()[atomicNumber];
			
			// K
			TransitionSeries ts = new TransitionSeries(e, TransitionSeriesType.K);

			esc = createTransition(sections, column, TransitionType.esc);
			column += elementDataWidth;

			// ts.setTransition(TransitionType.esc, esc);

			// ka
			k = createTransition(sections, column, TransitionType.other);
			column += elementDataWidth;
			k1 = createTransition(sections, column, TransitionType.a1);
			column += elementDataWidth;
			k2 = createTransition(sections, column, TransitionType.a2);
			column += elementDataWidth;

			ts.setTransition(k1);
			ts.setTransition(k2);


			// kB
			k = createTransition(sections, column, TransitionType.other);
			column += elementDataWidth;
			k1 = createTransition(sections, column, TransitionType.b1);
			column += elementDataWidth;
			k3 = createTransition(sections, column, TransitionType.b2);
			column += elementDataWidth;
			k2 = createTransition(sections, column, TransitionType.b3);
			column += elementDataWidth;

			ts.setTransition(k1);
			ts.setTransition(k2);
			ts.setTransition(k3);

			table.addSeries(ts);
			//table.addSeries(ts.pileup());


			ts = new TransitionSeries(e, TransitionSeriesType.L);
			Transition la, lb1, lb2, lg1, lg2, lg3, lg4, ll;

			esc = createTransition(sections, column, TransitionType.esc);
			column += elementDataWidth;

			la = createTransition(sections, column, TransitionType.a1);
			column += elementDataWidth;

			lb1 = createTransition(sections, column, TransitionType.b1);
			column += elementDataWidth;
			lb2 = createTransition(sections, column, TransitionType.b2);
			column += elementDataWidth;

			lg1 = createTransition(sections, column, TransitionType.g1);
			column += elementDataWidth;
			lg2 = createTransition(sections, column, TransitionType.g2);
			column += elementDataWidth;
			lg3 = createTransition(sections, column, TransitionType.g3);
			column += elementDataWidth;
			lg4 = createTransition(sections, column, TransitionType.g4);
			column += elementDataWidth;

			ll = createTransition(sections, column, TransitionType._l);
			column += elementDataWidth;


			ts.setTransition(la);

			ts.setTransition(lb1);
			ts.setTransition(lb2);

			ts.setTransition(lg1);
			ts.setTransition(lg2);
			ts.setTransition(lg3);
			ts.setTransition(lg4);

			ts.setTransition(ll);

			if (e.atomicNumber() > 44) table.addSeries(ts);


			
			ts = new TransitionSeries(e, TransitionSeriesType.M);
			Transition mz, ma1, mb1, mg, mn, unknown;

			esc = createTransition(sections, column, TransitionType.esc);
			column += elementDataWidth;

			mz = createTransition(sections, column, TransitionType.other);
			column += elementDataWidth;
			unknown = createTransition(sections, column, TransitionType.other);
			column += elementDataWidth;
			ma1 = createTransition(sections, column, TransitionType.a1);
			column += elementDataWidth;
			mb1 = createTransition(sections, column, TransitionType.b1);
			column += elementDataWidth;
			mg = createTransition(sections, column, TransitionType.g1);
			column += elementDataWidth;
			mn = createTransition(sections, column, TransitionType.other);
			column += elementDataWidth;
			unknown = createTransition(sections, column, TransitionType.other);
			column += elementDataWidth;

			
			ts.setTransition(ma1);
			ts.setTransition(mb1);
			ts.setTransition(mg);

			if (e.atomicNumber() > 72) table.addSeries(ts);

			atomicNumber++;

		}

		return table;
	}

	private static Transition createTransition(List<String> sections, int column, TransitionType type)
	{

		float energy = 0.0f;
		float relIntensity = 0.0f;

		try {
			energy = Float.parseFloat(sections.get(column));
			relIntensity = Float.parseFloat(sections.get(column + 1));
		} catch (NumberFormatException e) {
			return null;
		}

		if (energy == 0.0 || relIntensity == 0.0) return null;
		return new Transition(energy, relIntensity / 100.0f, type);

	}

}
