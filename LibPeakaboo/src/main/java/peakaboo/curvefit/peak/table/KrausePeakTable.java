package peakaboo.curvefit.peak.table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class KrausePeakTable implements PeakTable {

	private List<TransitionSeries> series = new ArrayList<>();
	
	public KrausePeakTable() {
		readPeakTableManual();
	}
	
	private void add(TransitionSeries ts) {
		series.add(ts);
	}
	
	@Override
	public List<TransitionSeries> getAll() {
		
		List<TransitionSeries> copy = new ArrayList<>();
		for (TransitionSeries ts : series) {
			copy.add(new TransitionSeries(ts));
		}
		return copy;
		
	}

	public void readPeakTableManual() {
		//PeakTable.clearSeries();
		
		int elementDataWidth = 2;

		InputStream ins = PeakTable.class.getResourceAsStream("/peakaboo/curvefit/peaktable/PeakTable.tsv");
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
			TransitionSeries ts = new TransitionSeries(e, TransitionShell.K);

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

			add(ts);
			//table.addSeries(ts.pileup());


			ts = new TransitionSeries(e, TransitionShell.L);
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

			if (e.atomicNumber() >= 23) add(ts);


			
			ts = new TransitionSeries(e, TransitionShell.M);
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

			if (e.atomicNumber() > 72) add(ts);

			atomicNumber++;

		}

	}

	private Transition createTransition(List<String> sections, int column)
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
	
}
