package peakaboo.curvefit.peak.transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;

/**
 * Describes the kind of escape peaks that would be expected from different kinds of detectors.
 * @author Nathaniel Sherry, 2010
 *
 */


public enum EscapePeakType
{
	NONE {
		public boolean hasOffset() { return false; }
		public List<Transition> offset() { return Collections.emptyList(); }
		public float energyGap() { return SILICON.energyGap(); }
		public float fanoFactor() { return SILICON.fanoFactor(); }
		public String pretty() { return "None"; }
	},
	
	SILICON {
		public boolean hasOffset() { return true; }
		public List<Transition> offset() { return PeakTable.getTransitionSeries(Element.Si, TransitionSeriesType.K).getAllTransitions(); }
		public float energyGap() { return 0.00358f; }
		public float fanoFactor() { return 0.144f; }
		public String pretty() { return "Silicon"; }
	},
	
	GERMANIUM {
		public boolean hasOffset() { return true; }
		public List<Transition> offset() { return PeakTable.getTransitionSeries(Element.Ge, TransitionSeriesType.K).getAllTransitions(); }
		public float energyGap() { return 0.0029f; }
		public float fanoFactor() { return 0.13f; }
		public String pretty() { return "Germanium"; }
	},

	
	;
	

	


	
	/**
	 * Returns a pretty-printed description of this {@link EscapePeakType}
	 * @return a {@link String} describing this {@link EscapePeakType}
	 */
	public String pretty() {
		return this.name().toLowerCase(); 
	}
	

	public static EscapePeakType getDefault()
	{
		return SILICON;
	}
	

	public static float escapeIntensity(Element e)
	{
		/*
		 * The paper
		 * 
		 * " Measurement and calculation of escape peak intensities in synchrotron radiation X-ray fluorescence analysis
		 * S.X. Kang a, X. Sun a, X. Ju b, Y.Y. Huang b, K. Yao a, Z.Q. Wu a, D.C. Xian b"
		 * 
		 * provides a listing of escape peak intensities relative to the real peak by element. By taking this data into
		 * openoffice and fitting an exponential regression line to it, we arrive at the formula esc(z) = (543268.59
		 * z^-4.48)%
		 */

		return 543268.59f * (float) Math.pow((e.ordinal() + 1), -4.48) / 100.0f;
	}



	
	
	/**
	 * returns true if this kind of {@link EscapePeakType} contains any {@link Transition}s
	 * @return true if this {@link EscapePeakType} is non-empty
	 */
	public boolean hasOffset() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a list of {@link Transition}s representing this escape peak
	 * @return a list of {@link Transition}s
	 */
	public List<Transition> offset(){
		throw new UnsupportedOperationException();
	}
	
	
	/**
	 * Energy gap is the energy required to produce an electron-hole pair in the 
	 * element, and is used in the calculation of peak widths.
	 */
	public float energyGap() {
		throw new UnsupportedOperationException();
	}
	
	
	/**
	 * This is used in the calculation of peak widths. 
	 * @return
	 */
	public float fanoFactor() {
		throw new UnsupportedOperationException();
	}
	
}
