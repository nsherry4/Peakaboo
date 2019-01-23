package peakaboo.curvefit.peak.transition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.escape.EscapePeak;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.table.Element;

public interface ITransitionSeries extends Iterable<Transition>, Comparable<ITransitionSeries>, Serializable {

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
	 * Returns a list of all {@link Transition}s that this {@link ITransitionSeries} is composed of
	 * @return a list of constituent {@link Transition}s
	 */
	List<Transition> getAllTransitions();
	
	/**
	 * Returns the strongest {@link Transition} for this {@link ITransitionSeries}.
	 * @return the most intense {@link Transition}
	 */
	Transition getStrongestTransition();
	
	/**
	 * Checks to see if this {@link ITransitionSeries} is empty
	 * @return true if this {@link ITransitionSeries} is non-empty, false otherwise
	 */
	boolean hasTransitions();
	
	
	/**
	 * Returns the number of filled {@link Transition}s in this TransitionSeries
	 * 
	 * @return the number of {@link Transition}s in this TransitionSeries
	 */
	int getTransitionCount();
	
	
	TransitionSeriesMode getMode();
	
	
	
	
	
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

	/**
	 * Generates an identifier string of the form He:K which can be used to uniquely
	 * identify the TransitionSeries.
	 */
	String toIdentifierString();

	/**
	 * Returns a list of {@link PrimaryTransitionSeries} which compose this TransitionSeries. If this is a primary transition series, it returns itself within a list.
	 */
	List<ITransitionSeries> getPrimaryTransitionSeries();
	
	
	
	default ITransitionSeries summation(final ITransitionSeries other) {
		return pileup(this, other);
	}

	/**
	 * Inspects the type of TransitionSeries given and returns a copy
	 */
	static ITransitionSeries copy(ITransitionSeries other) {
		if (other instanceof PrimaryTransitionSeries) {
			return new PrimaryTransitionSeries((PrimaryTransitionSeries)other);
		}
		if (other instanceof PileUpTransitionSeries) {
			return new PileUpTransitionSeries((PileUpTransitionSeries)other);
		}
		throw new IllegalArgumentException("Unknown Type of Transition");
	}
	
	static ITransitionSeries pileup(List<ITransitionSeries> tss) {
		if (tss.size() == 0) return null;
		if (tss.size() == 1) return tss.get(0);
		return new PileUpTransitionSeries(tss);
	}
	
	static ITransitionSeries pileup(ITransitionSeries... tss) {
		return pileup(Arrays.asList(tss));
	}
	
	/**
	 * Reads the identifier string and returns a new blank TransitionSeries, or null
	 * if the identifier string is invalid.
	 */
	public static ITransitionSeries get(String identifier) {
		if (identifier.contains("+")) {
			List<ITransitionSeries> tss = Arrays.asList(identifier.split("\\+")).stream().map(ITransitionSeries::get).collect(Collectors.toList());
			if (tss.contains(null)) {
				throw new RuntimeException("Poorly formated TransitionSeries identifier string: " + identifier);
			}
			return new PileUpTransitionSeries(tss);
		}
		String[] parts = identifier.split(":", 2);
		if (parts.length != 2) {
			return null;
		}
		Element e = Element.valueOf(parts[0]);
		TransitionShell tst = TransitionShell.fromTypeString(parts[1].trim());
		
		return new PrimaryTransitionSeries(e, tst);
	}

	
	
}
