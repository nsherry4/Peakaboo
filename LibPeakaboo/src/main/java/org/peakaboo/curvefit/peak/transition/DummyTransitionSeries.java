package org.peakaboo.curvefit.peak.transition;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.peakaboo.curvefit.peak.table.Element;

public class DummyTransitionSeries implements ITransitionSeries {

	private String name;
	
	public DummyTransitionSeries() {
		this("Nothing");
	}
	
	public DummyTransitionSeries(String name) {
		this.name = name;
	}
	
	@Override
	public Iterator<Transition> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public int compareTo(ITransitionSeries o) {
		return 1;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		//NOOP
	}

	@Override
	public TransitionShell getShell() {
		return TransitionShell.COMPOSITE;
	}

	@Override
	public Element getElement() {
		return null;
	}

	@Override
	public List<Transition> getAllTransitions() {
		return Collections.emptyList();
	}

	@Override
	public Transition getStrongestTransition() {
		return null;
	}

	@Override
	public boolean hasTransitions() {
		return false;
	}

	@Override
	public int getTransitionCount() {
		return 0;
	}

	@Override
	public TransitionSeriesMode getMode() {
		return TransitionSeriesMode.PRIMARY;
	}

	@Override
	public String toIdentifierString() {
		return null;
	}

	@Override
	public List<ITransitionSeries> getPrimaryTransitionSeries() {
		return null;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
