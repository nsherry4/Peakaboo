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
import peakaboo.curvefit.peak.table.Element;




/**
 * This class can represent: 1) a representation of all the {@link Transition}s for a given {@link Element} that fall into a specific
 * {@link TransitionShell}. 2) A representation of all of the {@link LegacyTransitionSeries} that are involved in the simultaneous 
 * detection of two or more X-Ray signals
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public class LegacyTransitionSeries implements Serializable, ITransitionSeries
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
	private final List<LegacyTransitionSeries> 	componentSeries;

	/**
	 * The {@link Element} that this TransitionSeries represents
	 */
	private final Element					element;

	private final List<Transition>			transitions;

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
	public LegacyTransitionSeries(LegacyTransitionSeries other) {
		this.type = other.getShell();
		this.mode = other.getMode();
		
		this.componentSeries = new ArrayList<>();
		for (LegacyTransitionSeries ts : other.componentSeries) {
			this.componentSeries.add(new LegacyTransitionSeries(ts));
		}

		this.element = other.getElement();
		this.transitions = other.transitions;
		this.setVisible(other.isVisible());
	}
	

	/**
	 * Creates a new blank TransitionSeries based on the given parameters
	 * 
	 * @param element the {@link Element} that this {@link LegacyTransitionSeries} represents (eg H, He, ...)
	 * @param seriesType the {@link TransitionShell} that this {@link LegacyTransitionSeries} represents (eg K, L, ...)
	 * @param mode the {@link TransitionSeriesMode} that this {@link LegacyTransitionSeries} represents (eg Primary, Pile-Up, ...)
	 */
	public LegacyTransitionSeries(Element element, TransitionShell seriesType, TransitionSeriesMode mode)
	{
		this.element = element;
		this.type = seriesType;
		this.mode = mode;
		setVisible(true);

		transitions = new ArrayList<Transition>();
		componentSeries = new ArrayList<LegacyTransitionSeries>();
	}
	
	/**
	 * Creates a new TransitionSeries with a {@link TransitionSeriesMode} of {@link TransitionSeriesMode#PRIMARY}
	 * 
	 * @param element the {@link Element} that this {@link LegacyTransitionSeries} represents (eg H, He, ...)
	 * @param seriesType the {@link TransitionShell} that this {@link LegacyTransitionSeries} represents (eg K, L, ...)
	 */
	public LegacyTransitionSeries(Element element, TransitionShell seriesType)
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

	
	/**
	 * Adds the {@link Transition} to the given {@link TransitionType}
	 * 
	 * @param type
	 *            the {@link TransitionType} to fill
	 * @param t
	 *            the {@link Transition}
	 */
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

				return componentSeries.stream().map(LegacyTransitionSeries::getDescription).collect(joining(" + "));

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
			return componentSeries.stream().map(LegacyTransitionSeries::toIdentifierString).reduce((a, b) -> a + "+" + b).get();
		}
		
		return getElement().name() + ":" + getShell().name();
		
	}

	/**
	 * Accepts a list of {@link LegacyTransitionSeries} and generates a composite TransitionSeries representing the occasional simultaneous detection of all of the given {@link LegacyTransitionSeries}
	 * @param tss list of {@link LegacyTransitionSeries}
	 * @return a Composite {@link LegacyTransitionSeries}
	 */
	public static LegacyTransitionSeries summation(final List<LegacyTransitionSeries> tss)
	{

		if (tss.size() == 0) return null;

		if (tss.size() == 1) return tss.get(0);
		
		//group the TransitionSeries by equality
		List<List<LegacyTransitionSeries>> tsGroups = ListOps.group(tss);

		//function for summing two TransitionSeries
		final BinaryOperator<LegacyTransitionSeries> tsSum = (ts1, ts2) -> ts1.summation(ts2);


		//turn the groups of primary transitionseries into a list of pile-up transitionseries
		List<LegacyTransitionSeries> pileups = tsGroups.stream().map(tsList -> tsList.stream().reduce(tsSum).get()).collect(toList());
	
		//sum the pileups
		LegacyTransitionSeries result = pileups.stream().reduce(tsSum).get();
		return result;

	}
	
	/**
	 * Convenience method for {@link #summation(List)}
	 */
	public static LegacyTransitionSeries summation(LegacyTransitionSeries... tss) {
		return summation(Arrays.asList(tss));
	}

	/**
	 * Creates a new {@link LegacyTransitionSeries} representing the effect of two {@link LegacyTransitionSeries} occasionally being detected simultaneously by a detector 
	 * @param other the other {@link LegacyTransitionSeries} being detected
	 * @return a Composite {@link LegacyTransitionSeries}
	 */
	public LegacyTransitionSeries summation(final LegacyTransitionSeries other)
	{
		
		// create the new TransitionSeries object
		final LegacyTransitionSeries newTransitionSeries = new LegacyTransitionSeries(
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
	public int compareTo(ITransitionSeries o)
	{

		if (!(o instanceof LegacyTransitionSeries)) {
			if (o.getElement() == getElement())
			{
				return -getShell().compareTo(o.getShell());
			}
			else
			{
				return -getElement().compareTo(o.getElement());
			}
		}
		
		LegacyTransitionSeries otherTS = (LegacyTransitionSeries) o;
		
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
				List<LegacyTransitionSeries> mySeries = new ArrayList<>(componentSeries);
				List<LegacyTransitionSeries> theirSeries = new ArrayList<>(otherTS.componentSeries);
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
			for (LegacyTransitionSeries ts: componentSeries) { sum += ts.hashCode(); }
			return sum;
		}
	}
	
	@Override
	public boolean equals(Object oother)
	{
	
		if (!(oother instanceof LegacyTransitionSeries)) return false;
		LegacyTransitionSeries other = (LegacyTransitionSeries) oother;
		
		if (getShell() != TransitionShell.COMPOSITE && other.getElement() != this.getElement()) return false;
		
		if (other.getShell() != this.getShell()) return false;	
		if (other.getMode() != this.getMode()) return false;

		if (getShell() == TransitionShell.COMPOSITE)
		{
			//Don't modify state just for a comparison
			List<LegacyTransitionSeries> mySeries = new ArrayList<>(componentSeries);
			List<LegacyTransitionSeries> theirSeries = new ArrayList<>(other.componentSeries);
			Collections.sort(mySeries);
			Collections.sort(theirSeries);

			return ListOps.zipWith(mySeries, theirSeries, (a, b) -> a.equals(b)).stream().reduce(true, Boolean::logicalAnd);
		}
		
		return true;

	}

	

	/**
	 * Returns a list of all primary {@link LegacyTransitionSeries} which compose this {@link LegacyTransitionSeries}. When this TransitionSeries is not a composite, it returns itself in a list.	 */
	@Deprecated
	public List<LegacyTransitionSeries> getBaseTransitionSeries()
	{
		List<LegacyTransitionSeries> list = null;

		switch (getShell())
		{
			case COMPOSITE:
				return ListOps.concatMap(componentSeries, LegacyTransitionSeries::getBaseTransitionSeries);

			default:
				list = new ArrayList<LegacyTransitionSeries>();
				list.add(this);
				return list;
		}

	}
	
	@Override
	public List<ITransitionSeries> getPrimaryTransitionSeries() {
		return new ArrayList<>(getBaseTransitionSeries());
	}

	/**
	 * Reads the identifier string and returns a new blank TransitionSeries, or null
	 * if the identifier string is invalid.
	 */
	public static LegacyTransitionSeries get(String identifier) {
		if (identifier.contains("+")) {
			List<LegacyTransitionSeries> tss = Arrays.asList(identifier.split("\\+")).stream().map(LegacyTransitionSeries::get).collect(Collectors.toList());
			if (tss.contains(null)) {
				throw new RuntimeException("Poorly formated TransitionSeries identifier string: " + identifier);
			}
			LegacyTransitionSeries sum = LegacyTransitionSeries.summation(tss);
			return sum;
		}
		String[] parts = identifier.split(":", 2);
		if (parts.length != 2) {
			return null;
		}
		Element e = Element.valueOf(parts[0]);
		TransitionShell tst = TransitionShell.fromTypeString(parts[1].trim());
		
		return new LegacyTransitionSeries(e, tst);
	}


	@Override
	public TransitionShell getShell() {
		return type;
	}

	@Override
	public TransitionSeriesMode getMode() {
		return mode;
	}


	@Override
	public Element getElement() {
		return element;
	}



}
