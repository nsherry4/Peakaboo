package peakaboo.curvefit.peak.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.table.Element;

public class PileUpTransitionSeries implements TransitionSeriesInterface {

	private List<TransitionSeriesInterface> primaries;
	private boolean visible;
	private List<Transition> transitions = new ArrayList<>();
	
	public PileUpTransitionSeries(List<TransitionSeriesInterface> primaries) {
		this.primaries = new ArrayList<>();
		
		//just in case this list doesn't just contain primaries, we'll force the issue
		for (TransitionSeriesInterface primary : primaries) {
			this.primaries.addAll(primary.getPrimaryTransitionSeries());
		}
		
		//for each primary, we generate new transitions by summing every existing transition against every primary transition
		//eg {t1, t2} x {t3, t4} = {t1+t3, t1+t4, t2+t3, t2+t4}
		for (TransitionSeriesInterface primary : primaries) {
			List<Transition> summed = new ArrayList<>();
			for (Transition t1 : transitions) {
				for (Transition t2 : primary.getAllTransitions()) {
					summed.add(t1.summation(t2));
				}
			}
			this.transitions = summed;
		}
		
	}
	
	public PileUpTransitionSeries(PileUpTransitionSeries other) {
		this.visible = other.visible;
		this.primaries = other.primaries.stream().map(TransitionSeriesInterface::copy).collect(Collectors.toList());
		this.transitions = new ArrayList<>(other.transitions);
	}
	
	public PileUpTransitionSeries(TransitionSeriesInterface... primaries) {
		this(Arrays.asList(primaries));
	}

	@Override
	public Iterator<Transition> iterator() {
		return transitions.iterator();
	}

	@Override
	public int compareTo(TransitionSeriesInterface o) {
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
		return primaries.stream().map(TransitionSeriesInterface::toIdentifierString).reduce((a, b) -> a + "+" + b).get();
	}

	@Override
	public List<TransitionSeriesInterface> getPrimaryTransitionSeries() {
		return new ArrayList<>(primaries);
	}
	
	
	
	
}
