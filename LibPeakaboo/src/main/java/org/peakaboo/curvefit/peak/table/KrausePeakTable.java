package org.peakaboo.curvefit.peak.table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.accent.log.OneLog;

public class KrausePeakTable implements PeakTable {

	private List<PrimaryTransitionSeries> series = new ArrayList<>();
	
	public KrausePeakTable() {
		readPeakTableManual();
	}
	
	private void add(PrimaryTransitionSeries ts) {
		series.add(ts);
	}
	
	@Override
	public List<PrimaryTransitionSeries> getAll() {
		
		List<PrimaryTransitionSeries> copy = new ArrayList<>();
		for (PrimaryTransitionSeries ts : series) {
			copy.add(new PrimaryTransitionSeries(ts));
		}
		return copy;
		
	}

	public void readPeakTableManual() {
		
		int elementDataWidth = 2;

		InputStream ins = PeakTable.class.getResourceAsStream("/org/peakaboo/curvefit/peaktable/PeakTable.tsv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
		

		String line;
		List<String> elements = new ArrayList<>();

		try {

			while ((line = reader.readLine()) != null) {
				elements.add(line);
			}

		} catch (IOException e) {
			OneLog.log(Level.SEVERE, "Error reading Peak Table", e);
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


			int column = 2;

			Transition k1, k2, k3;



			Element e = Element.values()[atomicNumber];
						
			// K
			PrimaryTransitionSeries ts = new PrimaryTransitionSeries(e, TransitionShell.K);

			//escape
			column += elementDataWidth;


			// ka
			//k
			column += elementDataWidth;
			k1 = createTransition(sections, column);
			column += elementDataWidth;
			k2 = createTransition(sections, column);
			column += elementDataWidth;


			ts.addTransition(k1);
			ts.addTransition(k2);


			// kB
			//k
			column += elementDataWidth;
			k1 = createTransition(sections, column);
			column += elementDataWidth;
			k3 = createTransition(sections, column);
			column += elementDataWidth;
			k2 = createTransition(sections, column);
			column += elementDataWidth;

			ts.addTransition(k1);
			ts.addTransition(k2);
			ts.addTransition(k3);

			add(ts);

			ts = new PrimaryTransitionSeries(e, TransitionShell.L);
			Transition la, lb1, lb2, lg1, lg2, lg3, lg4, ll;

			//escape
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


			ts.addTransition(la);

			ts.addTransition(lb1);
			ts.addTransition(lb2);

			ts.addTransition(lg1);
			ts.addTransition(lg2);
			ts.addTransition(lg3);
			ts.addTransition(lg4);

			ts.addTransition(ll);

			if (e.atomicNumber() >= 23) add(ts);


			
			ts = new PrimaryTransitionSeries(e, TransitionShell.M);
			Transition ma1, mb1, mg;

			//escape
			column += elementDataWidth;

			//mz
			column += elementDataWidth;
			
			//unknown
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

			
			ts.addTransition(ma1);
			ts.addTransition(mb1);
			ts.addTransition(mg);

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
