package peakaboo.curvefit.peaktable;

import com.github.tschoonj.xraylib.Xraylib;
import com.github.tschoonj.xraylib.XraylibException;

import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transitionseries.TransitionSeries;
import peakaboo.curvefit.transitionseries.TransitionSeriesType;




/**
 * 
 * This class reads a peak table definitions file and populates the static PeakTable class with data
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public class PeakTableReader
{
	
	public static void readPeakTable() {
		
		for (Element e : Element.values()) {
			readElementShell(-1 ,   -29, e, TransitionSeriesType.K);
			readElementShell(-30,  -110, e, TransitionSeriesType.L);
			readElementShell(-114, -400, e, TransitionSeriesType.M);			
		}

	}
	
	private static void readElementShell(int firstLine, int lastLine, Element elem, TransitionSeriesType tstype) {
		TransitionSeries ts = new TransitionSeries(elem, tstype);
		
		//find the strongest transition line, so we can skip anything significantly weaker than it
		float maxRel = 0f;
		for (int i = firstLine; i >= lastLine; i--) {
			try {
				maxRel = (float) Math.max(maxRel, Xraylib.RadRate(elem.atomicNumber(), i));	
			} catch (XraylibException e) {
				//this is normal, not all lines are available
			}
			
		}
		for (int i = firstLine; i >= lastLine; i--) {
			try {
				float value = (float) Xraylib.LineEnergy(elem.atomicNumber(), i);
				float rel = (float) Xraylib.RadRate(elem.atomicNumber(), i);
				
				//don't bother with this if the line is <0.1% the intensity of the largest line
				if (rel < maxRel*0.001) { continue; }
				
				Transition t = new Transition(value, rel);
				ts.setTransition(t);
			} catch (XraylibException ex) {
				//this is normal, not all lines are available
			}
		}
		if (ts.hasTransitions()) {
			PeakTable.addSeries(ts);
		}
	}

}
