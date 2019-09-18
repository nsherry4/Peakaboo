package org.peakaboo.curvefit.peak.table;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.RangeSet;

import com.github.tschoonj.xraylib.Xraylib;
import com.github.tschoonj.xraylib.XraylibException;

public class XrayLibPeakTable implements PeakTable {

	private List<PrimaryTransitionSeries> series;
	
	public XrayLibPeakTable() {}
	
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
			readElementShell(kLines, e, TransitionShell.K);
			
			//Don't read the L1L2,L1L3 lines -- they're at a way lower energy value and can 
			//mess up fitting on data where low energy ranges are poorly behaved
			//readElementShell(-30,  -110, e, TransitionSeriesType.L);
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
			maxRel = (float) Math.max(maxRel, lineRelativeIntensity(elem, line));	
			
		}
		for (int line : lines) {
			if (!hasLine(elem, line)) continue;
			
			float value = (float) Xraylib.LineEnergy(elem.atomicNumber(), line);
			float rel = lineRelativeIntensity(elem, line) / maxRel;
			
			//don't bother with this if the line is <0.1% the intensity of the largest line
			if (rel < maxRel*0.001) { continue; }
			
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
		} catch (XraylibException ex) {
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
