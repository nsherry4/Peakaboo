package peakaboo.curvefit.peaktable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.github.tschoonj.xraylib.Xraylib;
import com.github.tschoonj.xraylib.XraylibException;

import peakaboo.common.PeakabooLog;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeries;
import peakaboo.curvefit.transition.TransitionSeriesType;
import scitypes.Range;
import scitypes.RangeSet;




/**
 * 
 * This class reads a peak table definitions file and populates the static PeakTable class with data
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public class PeakTableReader
{
	
	//default peak table source
	public static void readPeakTable() {
		readPeakTableXraylib();
		//readPeakTableManual();
	}
	
	public static void readPeakTableXraylib() {
		//PeakTable.clearSeries();
		
		RangeSet kLines = new RangeSet();
		kLines.addRange(new Range(-29, -1));
		
		RangeSet lLines = new RangeSet();
		lLines.addRange(new Range(-110, -30));
		//L1L*
		lLines.removeRange(new Range(-30, -31));
		//L2L*
		lLines.removeRange(new Range(-59, -59));
		
		RangeSet mLines = new RangeSet();
		mLines.addRange(new Range(-219, -114));
		mLines.removeRange(new Range(-114, -117));
		mLines.removeRange(new Range(-137, -139));
		mLines.removeRange(new Range(-159, -160));
		mLines.removeRange(new Range(-181, -181));
		
		
		for (Element e : Element.values()) {
			readElementShell(kLines, e, TransitionSeriesType.K);
			
			//Don't read the L1L2,L1L3 lines -- they're at a way lower energy value and can 
			//mess up fitting on data where low energy ranges are poorly behaved
			//readElementShell(-30,  -110, e, TransitionSeriesType.L);
			readElementShell(lLines, e, TransitionSeriesType.L);
			readElementShell(mLines, e, TransitionSeriesType.M);			
		}

	}
	
	private static void readElementShell(RangeSet lines, Element elem, TransitionSeriesType tstype) {
		TransitionSeries ts = new TransitionSeries(elem, tstype);
		
		//find the strongest transition line, so we can skip anything significantly weaker than it
		float maxRel = 0f;
		for (int i : lines) {
			try {
				maxRel = (float) Math.max(maxRel, Xraylib.CS_FluorLine_Kissel(elem.atomicNumber(), i, 20000));	
			} catch (XraylibException e) {
				//this is normal, not all lines are available
			}
			
		}
		for (int i : lines) {
			try {
				float value = (float) Xraylib.LineEnergy(elem.atomicNumber(), i);
				float rel = 1f;
				try {
					rel = (float) Xraylib.CS_FluorLine_Kissel(elem.atomicNumber(), i, 20000);
				} catch (XraylibException e) {
					
				}
				
				
				//don't bother with this if the line is <0.1% the intensity of the largest line
				if (rel < maxRel*0.001) { continue; }
				
				Transition t = new Transition(value, rel, elem.name() + " " + tstype.name() + " #" + i + " @" + value + " keV x " + rel*100 + "%");
				ts.setTransition(t);
			} catch (XraylibException ex) {
				//this is normal, not all lines are available
			}
		}
		if (ts.hasTransitions()) {
			PeakTable.addSeries(ts);
		}
	}



	/**
	 * Read a peak table from a predetermined relative location 
	 * @return a populated PeakTable object
	 */
	public static void readPeakTableManual() {
		//PeakTable.clearSeries();
		
		int elementDataWidth = 2;

		InputStream ins = PeakTableReader.class.getResourceAsStream("/peakaboo/curvefit/peaktable/PeakTable.tsv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
		

		String line;
		List<String> elements = new ArrayList<String>();

		try {

			while ((line = reader.readLine()) != null) {
				elements.add(line);
			}

		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Error reading Peak Table", e);
		}

		//remove headers
		elements.remove(0);
		elements.remove(0);


		String[] lineSplit;
		List<String> sections;



		int atomicNumber = 0;

		for (String element : elements) {

			if (element == null) continue;

			lineSplit = element.split("\t");
			sections = Arrays.asList(lineSplit);

			//name
			//Integer.parseInt(sections.get(0));
			
			//number
			//sections.get(1);

			int column = 2;

			Transition k1, k2, k3;

			// table.addElement( createTransition(sections, name, number,
			// TransitionType.esc, column++) );


			Element e = Element.values()[atomicNumber];
						
			// K
			TransitionSeries ts = new TransitionSeries(e, TransitionSeriesType.K);

			//escape
			//createTransition(sections, column);
			column += elementDataWidth;

			// ts.setTransition(TransitionType.esc, esc);

			// ka
			//k
			//createTransition(sections, column);
			column += elementDataWidth;
			k1 = createTransition(sections, column);
			column += elementDataWidth;
			k2 = createTransition(sections, column);
			column += elementDataWidth;


			ts.setTransition(k1);
			ts.setTransition(k2);


			// kB
			//k
			//createTransition(sections, column);
			column += elementDataWidth;
			k1 = createTransition(sections, column);
			column += elementDataWidth;
			k3 = createTransition(sections, column);
			column += elementDataWidth;
			k2 = createTransition(sections, column);
			column += elementDataWidth;

			ts.setTransition(k1);
			ts.setTransition(k2);
			ts.setTransition(k3);

			//PeakTable.addSeries(ts);
			//table.addSeries(ts.pileup());


			ts = new TransitionSeries(e, TransitionSeriesType.L);
			Transition la, lb1, lb2, lg1, lg2, lg3, lg4, ll;

			//escape
			//createTransition(sections, column);
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


			ts.setTransition(la);

			ts.setTransition(lb1);
			ts.setTransition(lb2);

			ts.setTransition(lg1);
			ts.setTransition(lg2);
			ts.setTransition(lg3);
			ts.setTransition(lg4);

			ts.setTransition(ll);

			//if (e.atomicNumber() >= 23) PeakTable.addSeries(ts);


			
			ts = new TransitionSeries(e, TransitionSeriesType.M);
			Transition ma1, mb1, mg;

			//escape
			//createTransition(sections, column);
			column += elementDataWidth;

			//mz
			//createTransition(sections, column);
			column += elementDataWidth;
			
			//unknown
			//createTransition(sections, column);
			column += elementDataWidth;
			ma1 = createTransition(sections, column);
			column += elementDataWidth;
			mb1 = createTransition(sections, column);
			column += elementDataWidth;
			mg = createTransition(sections, column);
			column += elementDataWidth;
			
			//mn
			createTransition(sections, column);
			column += elementDataWidth;
			
			//unknown
			createTransition(sections, column);
			column += elementDataWidth;

			
			ts.setTransition(ma1);
			ts.setTransition(mb1);
			ts.setTransition(mg);

			if (e.atomicNumber() > 72) PeakTable.addSeries(ts);

			atomicNumber++;

		}

	}

	private static Transition createTransition(List<String> sections, int column)
	{

		float energy = 0.0f;
		float relIntensity = 0.0f;

		try {
			energy = Float.parseFloat(sections.get(column));
			relIntensity = Float.parseFloat(sections.get(column + 1));
		} catch (NumberFormatException e) {
			//Empty spaces with no data end up here. It's normal.
		}

		if (energy == 0.0 || relIntensity == 0.0) return null;
		return new Transition(energy, relIntensity / 100.0f, ""+column);

	}
	
	
	public static void main(String[] args) {
		readPeakTableXraylib();
		TransitionSeries ts = PeakTable.getTransitionSeries(Element.Au, TransitionSeriesType.M);
		for (Transition t : ts.getAllTransitions()) {
			System.out.println(t.name);
		}
		
		
	}

	
}
