package peakaboo.curvefit.peak.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.tschoonj.xraylib.Xraylib;
import com.github.tschoonj.xraylib.XraylibException;

import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import scitypes.Range;
import scitypes.RangeSet;

public class XrayLibPeakTable implements PeakTable {

	private List<TransitionSeries> series = new ArrayList<>();
	
	public XrayLibPeakTable() {
		readPeakTableXraylib();
	}
	
	private void add(TransitionSeries ts) {
		if (ts.getTransitionCount() == 0) return;
		series.add(ts);
	}


	@Override
	public Collection<TransitionSeries> getAll() {
		return new ArrayList<>(series);
	}


	
	public void readPeakTableXraylib() {
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
	
	private void readElementShell(RangeSet lines, Element elem, TransitionSeriesType tstype) {
		TransitionSeries ts = new TransitionSeries(elem, tstype);
		
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
			ts.setTransition(t);

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
