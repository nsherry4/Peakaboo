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
}
