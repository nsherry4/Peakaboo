package peakaboo.curvefit.fitting;

import java.util.List;


import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.PeakTable;
import peakaboo.curvefit.peaktable.Transition;
import peakaboo.curvefit.peaktable.TransitionSeriesType;
import scidraw.datatypes.DataTypeFactory;

/**
 * Describes the kind of escape peaks that would be expected from different kinds of detectors.
 * @author Nathaniel Sherry, 2010
 *
 */


public enum EscapePeakType
{
	NONE {
		@Override
		public boolean hasOffset() { return false; }
		@Override
		public String show() { return "None"; }
	}
	,
	SILICON {
		@Override
		public List<Transition> offset() { 
			
			return PeakTable.getTransitionSeries(Element.Si, TransitionSeriesType.K).getAllTransitions();
			
		}
		@Override
		public String show() { return "Silicon"; }
	}
	,
	GERMANIUM {
		@Override
		public List<Transition> offset() { 
			
			return PeakTable.getTransitionSeries(Element.Ge, TransitionSeriesType.K).getAllTransitions();
			
		}
		@Override
		public String show() { return "Germanium"; }
	}		
	,

	
	;
	
	/**
	 * returns true if this kind of {@link EscapePeakType} contains any {@link Transition}s
	 * @return true if this {@link EscapePeakType} is non-empty
	 */
	public boolean hasOffset() 			{ return true; }
	
	
	/**
	 * Returns a list of {@link Transition}s representing this escape peak
	 * @return a list of {@link Transition}s
	 */
	public List<Transition> offset()	{ return DataTypeFactory.<Transition>list(); }
	
	/**
	 * Returns a pretty-printed description of this {@link EscapePeakType}
	 * @return a {@link String} describing this {@link EscapePeakType}
	 */
	public String show()				{ return this.name().toLowerCase(); }
	

}
