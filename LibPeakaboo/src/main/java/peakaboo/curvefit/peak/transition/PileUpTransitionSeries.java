package peakaboo.curvefit.peak.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cyclops.util.ListOps;
import peakaboo.curvefit.peak.table.Element;

public class PileUpTransitionSeries implements ITransitionSeries {

	private List<ITransitionSeries> primaries;
	private boolean visible = true;
	private List<Transition> transitions = new ArrayList<>();
	
	PileUpTransitionSeries(List<ITransitionSeries> fromTSs) {
		this.primaries = new ArrayList<>();
		
		//just in case this list doesn't just contain primaries, we'll force the issue
		for (ITransitionSeries fromTS : fromTSs) {
			this.primaries.addAll(fromTS.getPrimaryTransitionSeries());
		}
		if (this.primaries.size() == 0) {
			throw new IllegalArgumentException("No Primary Transitions Found");
		}
		
		//for each primary, we generate new transitions by summing every existing transition against every primary transition
		//eg {t1, t2} x {t3, t4} = {t1+t3, t1+t4, t2+t3, t2+t4}
		for (ITransitionSeries fromTS : fromTSs) {
			List<Transition> summed = new ArrayList<>();
			for (Transition t1 : transitions) {
				for (Transition t2 : fromTS.getAllTransitions()) {
					summed.add(t1.summation(t2));
				}
			}
			this.transitions = summed;
		}
		
	}
	
	PileUpTransitionSeries(PileUpTransitionSeries other) {
		this.visible = other.visible;
		this.primaries = other.primaries.stream().map(ITransitionSeries::copy).collect(Collectors.toList());
		if (this.primaries.size() == 0) {
			throw new IllegalArgumentException("No Primary Transitions Found");
		}
		this.transitions = new ArrayList<>(other.transitions);
	}
	

	@Override
	public Iterator<Transition> iterator() {
		return transitions.iterator();
	}

	@Override
	public boolean equals(Object oother) {
		//even derived classes of BaseTransitionSeries can't be equal, because they all have different data.
		if (!(oother instanceof PileUpTransitionSeries)) return false;
		PileUpTransitionSeries other = (PileUpTransitionSeries) oother;
		
		//Don't modify state just for a comparison
		List<ITransitionSeries> mySeries = new ArrayList<>(getPrimaryTransitionSeries());
		List<ITransitionSeries> theirSeries = new ArrayList<>(other.getPrimaryTransitionSeries());
		Collections.sort(mySeries);
		Collections.sort(theirSeries);

		return ListOps.zipWith(mySeries, theirSeries, (a, b) -> a.equals(b)).stream().reduce(true, Boolean::logicalAnd);
	}
	
	@Override
	public int hashCode() {
		return getPrimaryTransitionSeries().stream().map(ts -> ts.hashCode()).reduce(0, (a, b) -> a + b);
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
		return TransitionShell.COMPOSITE;
	}

	@Override
	public Element getElement() {
		//return minimum element from primaries
		return primaries.stream()
				.map(p -> p.getElement())
				.min((a, b) -> Integer.compare(a.atomicNumber(), b.atomicNumber()))
				.get();
	}

	@Override
	public List<Transition> getAllTransitions() {
		return new ArrayList<>(transitions);
	}

	@Override
	public Transition getStrongestTransition() {
		Optional<Transition> strongest = transitions.stream().reduce((Transition t1, Transition t2) -> {
			if (t1.relativeIntensity > t2.relativeIntensity) return t1;
			return t2;
		});
		
		return strongest.orElse(null);
	}

	@Override
	public boolean hasTransitions() {
		return transitions.size() != 0;
	}


	@Override
	public int getTransitionCount() {
		return transitions.size();
	}

	@Override
	public String toIdentifierString() {
		return primaries.stream().map(ITransitionSeries::toIdentifierString).reduce((a, b) -> a + "+" + b).get();
	}

	@Override
	public List<ITransitionSeries> getPrimaryTransitionSeries() {
		return new ArrayList<>(primaries);
	}

	@Override
	public TransitionSeriesMode getMode() {
		return TransitionSeriesMode.SUMMATION;
	}
	
	
	public String toString() {
		return getPrimaryTransitionSeries().stream().map(ITransitionSeries::toString).reduce((p1, p2) -> p1 + " + " + p2).get();
	}
	
	
	
}
