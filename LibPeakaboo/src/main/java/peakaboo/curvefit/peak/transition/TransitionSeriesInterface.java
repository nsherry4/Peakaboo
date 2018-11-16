package peakaboo.curvefit.peak.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.escape.EscapePeak;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.table.Element;

public interface TransitionSeriesInterface extends Iterable<Transition>, Comparable<TransitionSeriesInterface> {

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
	
	
	
	
	TransitionShell getShell();
	
	Element getElement();
	
	
	
	/////////////////////////////////////////////////////////////
	//
	//  TRANSITIONS
	//
	/////////////////////////////////////////////////////////////
	
	/**
	 * Returns a list of all {@link Transition}s that this {@link TransitionSeries} is composed of
	 * @return a list of constituent {@link Transition}s
	 */
	List<Transition> getAllTransitions();
	
	/**
	 * Returns the strongest {@link Transition} for this {@link TransitionSeries}.
	 * @return the most intense {@link Transition}
	 */
	Transition getStrongestTransition();
	
	/**
	 * Checks to see if this {@link TransitionSeries} is empty
	 * @return true if this {@link TransitionSeries} is non-empty, false otherwise
	 */
	boolean hasTransitions();
	
	/**
	 * Adds the {@link Transition} to the given {@link TransitionType}
	 * 
	 * @param type
	 *            the {@link TransitionType} to fill
	 * @param t
	 *            the {@link Transition}
	 */
	void addTransition(Transition t);
	
	/**
	 * Returns the number of filled {@link Transition}s in this TransitionSeries
	 * 
	 * @return the number of {@link Transition}s in this TransitionSeries
	 */
	int getTransitionCount();
	
	
	
	
	
	
	
	default List<Transition> escape(EscapePeakType type) {
		if (! type.get().hasOffset()) {
			return new ArrayList<>();
		}
		
		List<Transition> escapePeaks = new ArrayList<>();
		for (Transition t : this) {
			for (Transition o : type.get().offset()) {
				escapePeaks.add(new Transition(t.energyValue - o.energyValue, t.relativeIntensity * o.relativeIntensity * EscapePeak.intensity(this.getElement()), t.name + " Escape"));
			}
		}
		return escapePeaks;
	}

	double getIntensity();

	/**
	 * Generates an identifier string of the form He:K which can be used to uniquely
	 * identify the TransitionSeries.
	 */
	String toIdentifierString();

	/**
	 * Returns a list of {@link PrimaryTransitionSeries} which compose this TransitionSeries. If this is a primary transition series, it returns itself within a list.
	 */
	List<TransitionSeriesInterface> getPrimaryTransitionSeries();
	
	
	
	
	

}
