package peakaboo.curvefit.transition;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.PeakTable;

/**
 * Describes the kind of escape peaks that would be expected from different kinds of detectors.
 * @author Nathaniel Sherry, 2010
 *
 */


public enum EscapePeakType
{
	NONE,
	SILICON,
	GERMANIUM,

	
	;
	
	/**
	 * returns true if this kind of {@link EscapePeakType} contains any {@link Transition}s
	 * @return true if this {@link EscapePeakType} is non-empty
	 */
	public boolean hasOffset() {
		switch (this) {
		case NONE: return false;
		case SILICON: return true;
		case GERMANIUM: return true;
		}
		return true;
	}
	
	
	/**
	 * Returns a list of {@link Transition}s representing this escape peak
	 * @return a list of {@link Transition}s
	 */
	public List<Transition> offset(){
		switch (this) {
		case SILICON: return PeakTable.getTransitionSeries(Element.Si, TransitionSeriesType.K).getAllTransitions();
		case GERMANIUM: return PeakTable.getTransitionSeries(Element.Ge, TransitionSeriesType.K).getAllTransitions();
		}
		return new ArrayList<Transition>(); 
	}
	
	
	public float energyGap() {
		switch (this) {
		case NONE: //have to assume something... 
		case SILICON: return 0.00358f;
		case GERMANIUM: return 0.0029f;
		}
		return SILICON.energyGap();
	}
	
	
	/**
	 * Returns a pretty-printed description of this {@link EscapePeakType}
	 * @return a {@link String} describing this {@link EscapePeakType}
	 */
	public String show() {
		switch (this) {
		case NONE: return "None";
		case SILICON: return "Silicon";
		case GERMANIUM: return "Germanium";
		}
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


	public float fanoFactor() {
		switch (this) {
		case NONE:
		case SILICON: return 0.144f;
		case GERMANIUM: return 0.13f;
		}
		return SILICON.fanoFactor();
	}
}
