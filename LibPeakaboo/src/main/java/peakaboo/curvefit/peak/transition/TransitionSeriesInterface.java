package peakaboo.curvefit.peak.transition;

import peakaboo.curvefit.peak.table.Element;

public interface TransitionSeriesInterface {

	/**
	 * Is this TransitionSeries visible?
	 * 
	 * @return visibility
	 */
	boolean isVisible();
	
	/**
	 * Sets the visibility of this TransitionSeries
	 * 
	 * @param visible
	 */
	void setVisible(boolean visible);
	
	
	/**
	 * Returns true if the TransitionSeries has no transitions
	 * @return
	 */
	boolean isEmpty();
	
	
	TransitionShell getShell();
	
	Element getElement();
	
}
