package org.peakaboo.curvefit.peak.transition;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.peakaboo.curvefit.peak.escape.EscapePeak;
import org.peakaboo.curvefit.peak.escape.EscapePeakType;
import org.peakaboo.curvefit.peak.table.Element;

public class PrimaryTransitionSeries implements ITransitionSeries {

	private List<Transition> transitions = new ArrayList<>();
	private TransitionShell shell;
	private Element element;
	private boolean visible = true;
	
	public PrimaryTransitionSeries(Element e, TransitionShell s) {
		this.element = e;
		this.shell = s;
	}
	
	public PrimaryTransitionSeries(PrimaryTransitionSeries other) {
		this.transitions = new ArrayList<>(other.transitions);
		this.shell = other.shell;
		this.element = other.element;
		this.visible = other.visible;
	}

	
	@Override
	public boolean equals(Object oother) {
		//even derived classes of BaseTransitionSeries can't be equal, because they all have different data.
		if (!(oother instanceof PrimaryTransitionSeries)) return false;
		PrimaryTransitionSeries other = (PrimaryTransitionSeries) oother;
		
		return shell == other.shell && element == other.element;
	}
	
	@Override
	public int hashCode() {
		return element.ordinal() * shell.ordinal();
	}
	
	@Override
	public int compareTo(ITransitionSeries o) {
		if (o.getElement() == getElement())
		{
			return -getShell().compareTo(o.getShell());
		}
		else
		{
			return -getElement().compareTo(o.getElement());
		}
	}

	
	
	/**
	 * Checks to see if this {@link ITransitionSeries} is empty
	 * @return true if this {@link ITransitionSeries} is non-empty, false otherwise
	 */
	@Override
	public boolean hasTransitions() {
		return transitions.size() != 0;
	}

	/**
	 * Returns a list of all {@link Transition}s that this {@link ITransitionSeries} is composed of
	 * @return a list of constituent {@link Transition}s
	 */
	@Override
	public List<Transition> getAllTransitions() {
		return new ArrayList<>(transitions);
	}
	
	/**
	 * Returns the strongest {@link Transition} for this {@link ITransitionSeries}.
	 * @return the most intense {@link Transition}
	 */
	@Override
	public Transition getStrongestTransition() {

		Optional<Transition> strongest = transitions.stream().reduce((Transition t1, Transition t2) -> {
			if (t1.relativeIntensity > t2.relativeIntensity) return t1;
			return t2;
		});
		
		return strongest.orElse(null);

	}
		

	/**
	 * Returns the number of filled {@link Transition}s in this TransitionSeries
	 * 
	 * @return the number of {@link Transition}s in this TransitionSeries
	 */
	@Override
	public int getTransitionCount() {
		return transitions.size();
	}


	/**
	 * Provides an Iterator of type {@link Transition} for iteration over the list of {@link Transition}s in this
	 * TransitionSeries
	 * 
	 * @return an iterator of type {@link Transition}
	 */
	@Override
	public Iterator<Transition> iterator() {
		return transitions.iterator();
	}


	@Override
	public boolean isVisible() {
		return visible;
	}


	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}





	@Override
	public TransitionShell getShell() {
		return shell;
	}


	@Override
	public Element getElement() {
		return element;
	}


	public void addTransition(Transition t) {
		if (t == null) return;
		transitions.add(t);
	}


	@Override
	public List<Transition> escape(EscapePeakType type) {
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


	@Override
	public String toString() {
		return getElement().name() + " " + getShell().name();
	}


	@Override
	public String toIdentifierString() {
		return getElement().name() + ":" + getShell().name();
	}


	@Override
	public List<ITransitionSeries> getPrimaryTransitionSeries() {
		List<ITransitionSeries> list = new ArrayList<>();
		list.add(this);
		return list;
	}

	@Override
	public TransitionSeriesMode getMode() {
		return TransitionSeriesMode.PRIMARY;
	}

}
