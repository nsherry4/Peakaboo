package peakaboo.curvefit.fitting;

import java.util.List;


import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.PeakTable;
import peakaboo.curvefit.peaktable.Transition;
import peakaboo.curvefit.peaktable.TransitionSeriesType;
import scidraw.datatypes.DataTypeFactory;


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
	
	public boolean hasOffset() 			{ return true; }
	public List<Transition> offset()	{ return DataTypeFactory.<Transition>list(); }
	public String show()				{ return this.name().toLowerCase(); }
	

}
