package org.peakaboo.curvefit.peak.table;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.accent.numeric.Range;
import org.peakaboo.framework.accent.numeric.RangeSet;

import com.github.tschoonj.xraylib.Xraylib;

public class XrayLibPeakTable implements PeakTable {

	private List<PrimaryTransitionSeries> series;

	private void add(PrimaryTransitionSeries ts) {
		if (ts.getTransitionCount() == 0) return;
		series.add(ts);
	}


	@Override
	public List<PrimaryTransitionSeries> getAll() {
		if (series == null) {
			readPeakTableXraylib();
		}
		
		List<PrimaryTransitionSeries> copy = new ArrayList<>();
		for (PrimaryTransitionSeries ts : series) {
			copy.add(new PrimaryTransitionSeries(ts));
		}
		return copy;
		
	}


	
	public void readPeakTableXraylib() {
		series = new ArrayList<>();

		/*
		 * NB: These constants are all defined as negative nubmers, so
		 * they must be passed to the range in 'reverse' order
		 */
		
		//K Lines
		RangeSet kLines = new RangeSet();
		kLines.addRange(new Range(Xraylib.KP5_LINE, Xraylib.KL1_LINE));
		
		
		//L Lines
		RangeSet lLines = new RangeSet();
		lLines.addRange(new Range(Xraylib.L3P4_LINE, Xraylib.L1L2_LINE));
		//Remove L1->L*
		lLines.removeRange(new Range(Xraylib.L1L3_LINE, Xraylib.L1L2_LINE));
		//Remove L2->L*
		lLines.removeRange(new Range(Xraylib.L2L3_LINE, Xraylib.L2L3_LINE));
				
		//M Lines
		RangeSet mLines = new RangeSet();
		mLines.addRange(new Range(Xraylib.M5P5_LINE, Xraylib.M1M2_LINE));
		//Remove lines from one m to another
		mLines.removeRange(new Range(Xraylib.M1M5_LINE, Xraylib.M1M2_LINE));
		mLines.removeRange(new Range(Xraylib.M2M5_LINE, Xraylib.M2M3_LINE));
		mLines.removeRange(new Range(Xraylib.M3M5_LINE, Xraylib.M3M4_LINE));
		mLines.removeRange(new Range(Xraylib.M4M5_LINE, Xraylib.M4M5_LINE));
		
		for (Element e : Element.values()) {
			readElementShell(kLines, e, TransitionShell.K);
			readElementShell(lLines, e, TransitionShell.L);
			readElementShell(mLines, e, TransitionShell.M);			
		}

	}
	
	private void readElementShell(RangeSet lines, Element elem, TransitionShell tstype) {
		PrimaryTransitionSeries ts = new PrimaryTransitionSeries(elem, tstype);
		//find the strongest transition line, so we can skip anything significantly weaker than it
		float maxRel = 0f;
		for (int line : lines) {
			if (!hasLine(elem, line)) continue;
			maxRel = Math.max(maxRel, lineRelativeIntensity(elem, line));
		}
		for (int line : lines) {
			// maxRel will be set as long as one of these lines continued in the last block
			if (!hasLine(elem, line)) continue;
			
			float value = (float) Xraylib.LineEnergy(elem.atomicNumber(), line);
			float rel = lineRelativeIntensity(elem, line) / maxRel;
			
			//don't bother with this if the line is <1% of the normalized intensity
			if (rel < 0.01) { continue; }
			
			Transition t = new Transition(value, rel, elem.name() + " " + tstype.name() + " #" + line + " @" + value + " keV x " + rel*100 + "%");
			ts.addTransition(t);
		}
		if (ts.hasTransitions()) {
			add(ts);
		}

	}
	
	private static boolean hasLine(Element elem, int line) {
		try {
			lineEnergy(elem, line);
			lineRelativeIntensity(elem, line);
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}
	
	private static float lineEnergy(Element elem, int line) {
		return (float) Xraylib.LineEnergy(elem.atomicNumber(), line);
	}
	
	private static float lineRelativeIntensity(Element elem, int line) {	
		return (float) Xraylib.CS_FluorLine_Kissel(elem.atomicNumber(), line, 20);
	}
	

}
