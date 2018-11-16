package peakaboo.curvefit.peak.transition;



import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import cyclops.util.ListOps;
import peakaboo.curvefit.peak.escape.EscapePeak;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.table.Element;




/**
 * This class can represent: 1) a representation of all the {@link Transition}s for a given {@link Element} that fall into a specific
 * {@link TransitionShell}. 2) A representation of all of the {@link TransitionSeries} that are involved in the simultaneous 
 * detection of two or more X-Ray signals
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public class TransitionSeries implements Serializable, TransitionSeriesInterface
{

	/**
	 * The {@link TransitionShell} that this TransitionSeries represents
	 */
	private final TransitionShell		type;
	
	/**
	 * the {@link TransitionSeriesMode} which describes this TransitionSeries.
	 */
	private final TransitionSeriesMode		mode;

	/**
	 * If this is a compound TransitionSeries, this list contains the component TransitionSeries
	 */
	private final List<TransitionSeries> 	componentSeries;

	/**
	 * The {@link Element} that this TransitionSeries represents
	 */
	private final Element					element;

	private final List<Transition>			transitions;

	/**
	 * The general intensity of this TransitionSeries
	 */
	private final double						intensity;

	/**
	 * Toggle for the visibility of this TransitionSeries
	 */
	private boolean							visible;


	
	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	
	/**
	 * Copy constructor. This is required because when working with two plots at the 
	 * same time, they must have copies so that things like visibility can be modified
	 * independently.
	 */
	public TransitionSeries(TransitionSeries other) {
		this.type = other.getShell();
		this.mode = other.getMode();
		
		this.componentSeries = new ArrayList<>();
		for (TransitionSeries ts : other.componentSeries) {
			this.componentSeries.add(new TransitionSeries(ts));
		}

		this.element = other.getElement();
		this.transitions = other.transitions;
		this.intensity = other.getIntensity();
		this.setVisible(other.isVisible());
	}
	

	/**
	 * Creates a new blank TransitionSeries based on the given parameters
	 * 
	 * @param element the {@link Element} that this {@link TransitionSeries} represents (eg H, He, ...)
	 * @param seriesType the {@link TransitionShell} that this {@link TransitionSeries} represents (eg K, L, ...)
	 * @param mode the {@link TransitionSeriesMode} that this {@link TransitionSeries} represents (eg Primary, Pile-Up, ...)
	 */
	public TransitionSeries(Element element, TransitionShell seriesType, TransitionSeriesMode mode)
	{
		this.element = element;
		this.type = seriesType;
		this.mode = mode;
		intensity = 1.0;
		setVisible(true);

		transitions = new ArrayList<Transition>();
		componentSeries = new ArrayList<TransitionSeries>();
	}
	
	/**
	 * Creates a new TransitionSeries with a {@link TransitionSeriesMode} of {@link TransitionSeriesMode#PRIMARY}
	 * 
	 * @param element the {@link Element} that this {@link TransitionSeries} represents (eg H, He, ...)
	 * @param seriesType the {@link TransitionShell} that this {@link TransitionSeries} represents (eg K, L, ...)
	 */
	public TransitionSeries(Element element, TransitionShell seriesType)
	{
		this(element, seriesType, TransitionSeriesMode.PRIMARY);
	}




	
	@Override
	public List<Transition> getAllTransitions()
	{
		return new ArrayList<>(transitions);
	}
	
	@Override
	public Transition getStrongestTransition()
	{

		Optional<Transition> strongest = transitions.stream().reduce((Transition t1, Transition t2) -> {
			if (t1.relativeIntensity > t2.relativeIntensity) return t1;
			return t2;
		});
		
		return strongest.orElse(null);

	}

	@Override
	public boolean hasTransitions()
	{
		return transitions.size() != 0;
	}

	@Override
	public void addTransition(Transition t)
	{
		if (t == null) return;
		transitions.add(t);
	}

	@Override
	public int getTransitionCount()
	{
		return transitions.size();
	}
	
	@Override
	public Iterator<Transition> iterator()
	{
		return transitions.iterator();
	}

	
	private String getDescription()
	{
		switch (getMode())
		{


			case SUMMATION:

				Collections.sort(componentSeries);

				return componentSeries.stream().map(TransitionSeries::getDescription).collect(joining(" + "));

			default:

				return getElement().name() + " " + getShell().name();

		}

	}


	/**
	 * Alias for getDescription
	 */
	@Override
	public String toString()
	{
		return getDescription();
	}




	@Override
	public String toIdentifierString() {
		if (getShell() == TransitionShell.COMPOSITE) {
			return componentSeries.stream().map(TransitionSeries::toIdentifierString).reduce((a, b) -> a + "+" + b).get();
		}
		
		return getElement().name() + ":" + getShell().name();
		
	}

	/**
	 * Accepts a list of {@link TransitionSeries} and generates a composite TransitionSeries representing the occasional simultaneous detection of all of the given {@link TransitionSeries}
	 * @param tss list of {@link TransitionSeries}
	 * @return a Composite {@link TransitionSeries}
	 */
	public static TransitionSeries summation(final List<TransitionSeries> tss)
	{

		if (tss.size() == 0) return null;

		if (tss.size() == 1) return tss.get(0);
		
		//group the TransitionSeries by equality
		List<List<TransitionSeries>> tsGroups = ListOps.group(tss);

		//function for summing two TransitionSeries
		final BinaryOperator<TransitionSeries> tsSum = (ts1, ts2) -> ts1.summation(ts2);


		//turn the groups of primary transitionseries into a list of pile-up transitionseries
		List<TransitionSeries> pileups = tsGroups.stream().map(tsList -> tsList.stream().reduce(tsSum).get()).collect(toList());
	
		//sum the pileups
		TransitionSeries result = pileups.stream().reduce(tsSum).get();
		return result;

	}
	
	/**
	 * Convenience method for {@link #summation(List)}
	 */
	public static TransitionSeries summation(TransitionSeries... tss) {
		return summation(Arrays.asList(tss));
	}

	/**
	 * Creates a new {@link TransitionSeries} representing the effect of two {@link TransitionSeries} occasionally being detected simultaneously by a detector 
	 * @param other the other {@link TransitionSeries} being detected
	 * @return a Composite {@link TransitionSeries}
	 */
	public TransitionSeries summation(final TransitionSeries other)
	{
		
		// create the new TransitionSeries object
		final TransitionSeries newTransitionSeries = new TransitionSeries(
			getElement(),
			TransitionShell.COMPOSITE,
			TransitionSeriesMode.SUMMATION);
		
		if (transitions.size() > 0 && other.transitions.size() > 0) {
			//For each transition in the outer map, map the list transitionList to a list of pileup values
			List<List<Transition>> allPileupLists = transitions.stream()
					.map(t1 -> other.transitions.stream().map(t2 ->t1.summation(t2)).collect(toList()))
					.collect(toList());
	
			List<Transition> allPileups = new ArrayList<>();
			for (List<Transition> l : allPileupLists) {
				allPileups.addAll(l);
			}
			allPileups.forEach(newTransitionSeries::addTransition);
		}
		
		newTransitionSeries.componentSeries.add(this);
		newTransitionSeries.componentSeries.add(other);

		return newTransitionSeries;

	}

	@Override
	public int compareTo(TransitionSeriesInterface o)
	{

		if (!(o instanceof TransitionSeries)) {
			if (o.getElement() == getElement())
			{
				return -getShell().compareTo(o.getShell());
			}
			else
			{
				return -getElement().compareTo(o.getElement());
			}
		}
		
		TransitionSeries otherTS = (TransitionSeries) o;
		
		switch (getMode())
		{

			case PRIMARY:

				if (otherTS.getElement() == getElement())
				{
					return -getShell().compareTo(otherTS.getShell());
				}
				else
				{
					return -getElement().compareTo(otherTS.getElement());
				}

			case SUMMATION:
				
				//Don't modify state just for a comparison
				List<TransitionSeries> mySeries = new ArrayList<>(componentSeries);
				List<TransitionSeries> theirSeries = new ArrayList<>(otherTS.componentSeries);
				Collections.sort(mySeries);
				Collections.sort(theirSeries);
				
				List<Integer> differences = ListOps.zipWith(mySeries, theirSeries, (ts1, ts2) -> ts1.compareTo(ts2)).stream()
												.filter(a -> a.equals(0))
												.collect(toList());

				if (differences.size() == 0) return 0;
				return differences.get(0);

		}

		return 0;

	}

	/**
	 * Hash function returns the type's ordinal * the element's ordinal for non-composite
	 * series. For composite series, it returns the sum of it's components hashes
	 * @return
	 */
	@Override
	public int hashCode()
	{
		if (getShell() != TransitionShell.COMPOSITE) 
		{
			return (1+getShell().ordinal()) * (1+getElement().ordinal());
		}
		else
		{
			int sum = 0;
			for (TransitionSeries ts: componentSeries) { sum += ts.hashCode(); }
			return sum;
		}
	}
	
	@Override
	public boolean equals(Object oother)
	{
	
		if (!(oother instanceof TransitionSeries)) return false;
		TransitionSeries other = (TransitionSeries) oother;
		
		if (getShell() != TransitionShell.COMPOSITE && other.getElement() != this.getElement()) return false;
		
		if (other.getShell() != this.getShell()) return false;	
		if (other.getMode() != this.getMode()) return false;

		if (getShell() == TransitionShell.COMPOSITE)
		{
			//Don't modify state just for a comparison
			List<TransitionSeries> mySeries = new ArrayList<>(componentSeries);
			List<TransitionSeries> theirSeries = new ArrayList<>(other.componentSeries);
			Collections.sort(mySeries);
			Collections.sort(theirSeries);

			return ListOps.zipWith(mySeries, theirSeries, (a, b) -> a.equals(b)).stream().reduce(true, Boolean::logicalAnd);
		}
		
		return true;

	}

	

	/**
	 * Returns a list of all primary {@link TransitionSeries} which compose this {@link TransitionSeries}. When this TransitionSeries is not a composite, it returns itself in a list.	 */
	@Deprecated
	public List<TransitionSeries> getBaseTransitionSeries()
	{
		List<TransitionSeries> list = null;

		switch (getShell())
		{
			case COMPOSITE:
				return ListOps.concatMap(componentSeries, TransitionSeries::getBaseTransitionSeries);

			default:
				list = new ArrayList<TransitionSeries>();
				list.add(this);
				return list;
		}

	}
	
	@Override
	public List<TransitionSeriesInterface> getPrimaryTransitionSeries() {
		return new ArrayList<>(getBaseTransitionSeries());
	}

	/**
	 * Reads the identifier string and returns a new blank TransitionSeries, or null
	 * if the identifier string is invalid.
	 */
	public static TransitionSeries get(String identifier) {
		if (identifier.contains("+")) {
			List<TransitionSeries> tss = Arrays.asList(identifier.split("\\+")).stream().map(TransitionSeries::get).collect(Collectors.toList());
			if (tss.contains(null)) {
				throw new RuntimeException("Poorly formated TransitionSeries identifier string: " + identifier);
			}
			TransitionSeries sum = TransitionSeries.summation(tss);
			return sum;
		}
		String[] parts = identifier.split(":", 2);
		if (parts.length != 2) {
			return null;
		}
		Element e = Element.valueOf(parts[0]);
		TransitionShell tst = TransitionShell.fromTypeString(parts[1].trim());
		
		return new TransitionSeries(e, tst);
	}


	@Override
	public double getIntensity() {
		return intensity;
	}


	@Override
	public TransitionShell getShell() {
		return type;
	}


	public TransitionSeriesMode getMode() {
		return mode;
	}


	@Override
	public Element getElement() {
		return element;
	}



}
