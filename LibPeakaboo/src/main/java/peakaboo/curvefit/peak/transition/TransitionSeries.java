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

public class TransitionSeries implements Serializable, Iterable<Transition>, Comparable<TransitionSeries>
{

	/**
	 * The {@link TransitionShell} that this TransitionSeries represents
	 */
	public final TransitionShell		type;
	
	/**
	 * the {@link TransitionSeriesMode} which describes this TransitionSeries.
	 */
	public final TransitionSeriesMode		mode;

	/**
	 * If this is a compound TransitionSeries, this list contains the component TransitionSeries
	 */
	private final List<TransitionSeries> 	componentSeries;

	/**
	 * The {@link Element} that this TransitionSeries represents
	 */
	public final Element					element;

	private final List<Transition>			transitions;

	/**
	 * The general intensity of this TransitionSeries
	 */
	private final double						intensity;

	/**
	 * Toggle for the visibility of this TransitionSeries
	 */
	private boolean							visible;


	/**
	 * Is this TransitionSeries visible?
	 * 
	 * @return visibility
	 */
	public boolean isVisible()
	{
		return visible;
	}


	/**
	 * Sets the visibility of this TransitionSeries
	 * 
	 * @param visible
	 */
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
		this.type = other.type;
		this.mode = other.mode;
		
		this.componentSeries = new ArrayList<>();
		for (TransitionSeries ts : other.componentSeries) {
			this.componentSeries.add(new TransitionSeries(ts));
		}

		this.element = other.element;
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




	/**
	 * Returns a list of all {@link Transition}s that this {@link TransitionSeries} is composed of
	 * @return a list of constituent {@link Transition}s
	 */
	public List<Transition> getAllTransitions()
	{
		return transitions.stream().map(a -> a).collect(toList());
	}
	
	/**
	 * Returns the strongest {@link Transition} for this {@link TransitionSeries}.
	 * @return the most intense {@link Transition}
	 */
	public Transition getStrongestTransition()
	{

		Optional<Transition> strongest = transitions.stream().reduce((Transition t1, Transition t2) -> {
			if (t1.relativeIntensity > t2.relativeIntensity) return t1;
			return t2;
		});
		
		return strongest.orElse(null);

	}


	/**
	 * Checks to see if this {@link TransitionSeries} is empty
	 * @return true if this {@link TransitionSeries} is non-empty, false otherwise
	 */
	public boolean hasTransitions()
	{
		return transitions.size() != 0;
	}


	/**
	 * Sets the {@link Transition} for the given {@link TransitionType}
	 * 
	 * @param type
	 *            the {@link TransitionType} to fill
	 * @param t
	 *            the {@link Transition}
	 */
	public void setTransition(Transition t)
	{
		if (t == null) return;
		transitions.add(t);
	}


	/**
	 * Returns the number of filled {@link Transition}s in this TransitionSeries
	 * 
	 * @return the number of {@link Transition}s in this TransitionSeries
	 */
	public int getTransitionCount()
	{
		return transitions.size();
	}


	/**
	 * Provides an Iterator of type {@link Transition} for iteration over the list of {@link Transition}s in this
	 * TransitionSeries
	 * 
	 * @return an iterator of type {@link Transition}
	 */
	public Iterator<Transition> iterator()
	{
		return transitions.iterator();
	}




	/**
	 * Returns a description of this {@link TransitionSeries}, including the {@link Element} and the {@link TransitionShell}. If the {@link TransitionSeries} is a pile-up or summation, it will be reflected in the description
	 * @return
	 */
	public String getDescription()
	{
		return getDescription(true);
	}


	private String getDescription(boolean isShort)
	{
		switch (mode)
		{

			case PILEUP:


				int count = getPileupCount();
				String suffix = "";
				if (count > 2) suffix += " x" + count;

				return componentSeries.get(0).element.name() + " " + componentSeries.get(0).getBaseType().name()
						+ " Pile-Up" + suffix;

			case SUMMATION:

				Collections.sort(componentSeries);

				return componentSeries.stream().map(TransitionSeries::getDescription).collect(joining(" + "));

			default:

				if (isShort) return element.name() + " " + type.name();
				else return element.toString() + " " + type.name();

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


	/**
	 * Alias for getDescription(true)
	 * 
	 * @return the element-name string
	 */
	public String toElementString()
	{
		return getDescription(true);
	}
	

	/**
	 * Generates an identifier string of the form He:K which can be used to uniquely
	 * identify the TransitionSeries.
	 */
	public String toIdentifierString() {
		if (type == TransitionShell.COMPOSITE) {
			return componentSeries.stream().map(TransitionSeries::toIdentifierString).reduce((a, b) -> a + "+" + b).get();
		}
		
		return element.name() + ":" + type.name();
		
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
		
		TransitionSeriesMode newmode = TransitionSeriesMode.SUMMATION;

		if (this.equals(other)) newmode = TransitionSeriesMode.PILEUP;
		if (this.mode == TransitionSeriesMode.PILEUP && this.element.equals(other.element)) newmode = TransitionSeriesMode.PILEUP;
		if (other.mode == TransitionSeriesMode.PILEUP && other.element.equals(this.element)) newmode = TransitionSeriesMode.PILEUP;

		// create the new TransitionSeries object
		final TransitionSeries newTransitionSeries = new TransitionSeries(
			element,
			TransitionShell.COMPOSITE,
			newmode);
		
		if (transitions.size() > 0 && other.transitions.size() > 0) {
			//For each transition in the outer map, map the list transitionList to a list of pileup values
			List<List<Transition>> allPileupLists = transitions.stream()
					.map(t1 -> other.transitions.stream().map(t2 ->t1.summation(t2)).collect(toList()))
					.collect(toList());
	
			List<Transition> allPileups = new ArrayList<>();
			for (List<Transition> l : allPileupLists) {
				allPileups.addAll(l);
			}
			allPileups.forEach(newTransitionSeries::setTransition);
		}
		
		newTransitionSeries.componentSeries.add(this);
		newTransitionSeries.componentSeries.add(other);

		return newTransitionSeries;

	}

	public List<Transition> escape(EscapePeakType type) {
		if (! type.get().hasOffset()) {
			return new ArrayList<>();
		}
		
		List<Transition> escapePeaks = new ArrayList<>();
		for (Transition t : this) {
			for (Transition o : type.get().offset()) {
				escapePeaks.add(new Transition(t.energyValue - o.energyValue, t.relativeIntensity * o.relativeIntensity * EscapePeak.intensity(this.element), t.name + " Escape"));
			}
		}
		return escapePeaks;
	}
	

	public int compareTo(TransitionSeries otherTS)
	{

		switch (mode)
		{

			case PRIMARY:
			case PILEUP:

				if (otherTS.element == element)
				{
					return -type.compareTo(otherTS.type);
				}
				else
				{
					return -element.compareTo(otherTS.element);
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
		if (type != TransitionShell.COMPOSITE) 
		{
			return (1+type.ordinal()) * (1+element.ordinal());
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
		
		if (type != TransitionShell.COMPOSITE && other.element != this.element) return false;
		
		if (other.type != this.type) return false;	
		if (other.mode != this.mode) return false;

		if (type == TransitionShell.COMPOSITE)
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
	 * Returns the number of times the base {@link Element} {@link TransitionSeries} appears duplicated in this {@link TransitionSeries}. If this {@link TransitionSeries} is not a pile-up, the result is 1
	 * @return the number of times the base {@link TransitionSeries} has been piled-up
	 */
	public int getPileupCount()
	{
		int count = 0;
		if (mode == TransitionSeriesMode.PILEUP)
		{
			for (TransitionSeries ts : componentSeries)
			{
				count += ts.getPileupCount();
			}

		}
		else if (mode == TransitionSeriesMode.PRIMARY)
		{
			count = 1;
		}

		return count;
	}


	/**
	 * Returns the base {@link TransitionShell} for this {@link TransitionSeries}
	 * @return the base {@link TransitionShell}
	 */
	public TransitionShell getBaseType()
	{

		if (mode == TransitionSeriesMode.PILEUP)
		{
			return componentSeries.get(0).getBaseType();

		}
		else
		{
			return type;
		}
	}


	/**
	 * Returns a list of all primary {@link TransitionSeries} which compose this {@link TransitionSeries}
	 * @return a list of all primary {@link TransitionSeries} represented by this
	 */
	public List<TransitionSeries> getBaseTransitionSeries()
	{
		List<TransitionSeries> list = null;

		switch (type)
		{
			case COMPOSITE:
				return ListOps.concatMap(componentSeries, TransitionSeries::getBaseTransitionSeries);

			default:
				list = new ArrayList<TransitionSeries>();
				list.add(this);
				return list;
		}

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


	public double getIntensity() {
		return intensity;
	}


}
