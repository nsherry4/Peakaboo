package peakaboo.curvefit.model.transitionseries;



import static fava.Fn.all;
import static fava.Fn.concat;
import static fava.Fn.concatMap;
import static fava.Fn.each;
import static fava.Fn.filter;
import static fava.Fn.foldr;
import static fava.Fn.group;
import static fava.Fn.map;
import static fava.Fn.zipWith;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import peakaboo.curvefit.model.transition.Transition;
import peakaboo.curvefit.model.transition.TransitionType;
import peakaboo.curvefit.peaktable.Element;
import fava.Functions;
import fava.functionable.FList;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;




/**
 * This class can represent: 1) a representation of all the {@link Transition}s for a given {@link Element} that fall into a specific
 * {@link TransitionSeriesType}. 2) A representation of all of the {@link TransitionSeries} that are involved in the simultaneous 
 * detection of two or more X-Ray signals
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public class TransitionSeries implements Serializable, Iterable<Transition>, Comparable<TransitionSeries>
{

	/**
	 * The {@link TransitionSeriesType} that this TransitionSeries represents
	 */
	public TransitionSeriesType		type;
	
	/**
	 * the {@link TransitionSeriesMode} which describes this TransitionSeries.
	 */
	public TransitionSeriesMode		mode;

	/**
	 * If this is a compound TransitionSeries, this list contains the component TransitionSeries
	 */
	private FList<TransitionSeries>	 componentSeries;

	/**
	 * The {@link Element} that this TransitionSeries represents
	 */
	public Element					element;

	private FList<Transition>		transitions;

	/**
	 * The general intensity of this TransitionSeries
	 */
	public double					intensity;

	/**
	 * Toggle for the visibility of this TransitionSeries
	 */
	public boolean					visible;


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
	 * Creates a new TransitionSeries based on the given parameters
	 * 
	 * @param element the {@link Element} that this {@link TransitionSeries} represents (eg H, He, ...)
	 * @param seriesType the {@link TransitionSeriesType} that this {@link TransitionSeries} represents (eg K, L, ...)
	 * @param mode the {@link TransitionSeriesMode} that this {@link TransitionSeries} represents (eg Primary, Pile-Up, ...)
	 */
	public TransitionSeries(Element element, TransitionSeriesType seriesType, TransitionSeriesMode mode)
	{
		this.element = element;
		this.type = seriesType;
		this.mode = mode;
		intensity = 1.0;
		visible = true;

		transitions = new FList<Transition>();
		componentSeries = new FList<TransitionSeries>();
	}
	
	/**
	 * Creates a new TransitionSeries with a {@link TransitionSeriesMode} of {@link TransitionSeriesMode#PRIMARY}
	 * 
	 * @param element the {@link Element} that this {@link TransitionSeries} represents (eg H, He, ...)
	 * @param seriesType the {@link TransitionSeriesType} that this {@link TransitionSeries} represents (eg K, L, ...)
	 */
	public TransitionSeries(Element element, TransitionSeriesType seriesType)
	{
		this(element, seriesType, TransitionSeriesMode.PRIMARY);
	}


	/**
	 * Retrieves a {@link Transition} from this TransitionSeries based on the provided {@link TransitionType}
	 * 
	 * @param transitionType
	 *            the type of {@link Transition} to retrieve
	 * @return the {@link Transition} for the given {@link TransitionType}
	 */
	public Transition getTransition(final TransitionType transitionType)
	{
		List<Transition> matches = transitions.stream().filter(t -> t.type == transitionType).collect(Collectors.toList());
		if (matches.size() > 0) return matches.get(0);
		return null;
	}


	/**
	 * Returns a list of all {@link Transition}s that this {@link TransitionSeries} is composed of
	 * @return a list of constituent {@link Transition}s
	 */
	public List<Transition> getAllTransitions()
	{
		return transitions.map(a -> a);
	}
	
	/**
	 * Returns the strongest {@link Transition} for this {@link TransitionSeries}.
	 * @return the most intense {@link Transition}
	 */
	public Transition getStrongestTransition()
	{

		return transitions.foldr((Transition t1, Transition t2) -> {
			if (t1.relativeIntensity > t2.relativeIntensity) return t1;
			return t2;
		});

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
	 * Calculates how close this TransitionSeries is to a given energy by examining the energy point of each
	 * {@link Transition} in this TransitionSeries
	 * 
	 * @param energy
	 *            the energy to compare to
	 * @return the minimum distance of any {@link Transition} in this TransitionSeries to energy
	 */
	public double getProximityToEnergy(double energy)
	{

		double nearest = Math.abs(transitions.iterator().next().energyValue - energy);
		double current;

		for (Transition t : transitions)
		{

			current = t.energyValue - energy;
			if (Math.abs(current) < Math.abs(nearest)) nearest = current;

		}

		return nearest;
	}
	
	/**
	 * Calculates a score representing how close this TransitionSeries is to a given energy by ranking all of the Transitions
	 * {@link Transition} in this TransitionSeries using {@link TransitionSeries#getProximityToEnergy}
	 * 
	 * @param energy
	 *            the energy to compare to
	 * @return the minimum distance of any {@link Transition} in this TransitionSeries to energy
	 */
	public double getProximityScore(final double energy, Double minEnergyDistance)
	{
		
		final double minDistance;
		if (minEnergyDistance == null)
		{
			minDistance = 0d;
		} else {
			minDistance = minEnergyDistance;
		}
		
		FList<Double> scores = transitions.map(t -> {
			return  t.relativeIntensity / (Math.max( Math.abs(t.energyValue - energy), minDistance ));
		});
		
		
		return 1 / scores.fold((a, b) -> a + b);

	}


	/**
	 * Returns a description of this {@link TransitionSeries}, including the {@link Element} and the {@link TransitionSeriesType}. If the {@link TransitionSeries} is a pile-up or summation, it will be reflected in the description
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

				return componentSeries.map(TransitionSeries::getDescription).foldr((a, b) -> a + " + " + b);

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
	 * Returns the lowest energy value of any {@link Transition} in this {@link TransitionSeries}
	 * @return lowest energy values
	 */
	public double getLowestEnergyValue()
	{
		double lowest = Double.MAX_VALUE;
		for (Transition t : transitions)
		{
			if (t.energyValue < lowest) lowest = t.energyValue;
		}
		return lowest;
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
		List<List<TransitionSeries>> tsGroups = group(tss);


		//function for summing two TransitionSeries
		final BiFunction<TransitionSeries, TransitionSeries, TransitionSeries> tsSum = (ts1, ts2) -> ts1.summation(ts2);


		//turn the groups of primary transitionseries into a list of pile-up transitionseries
		List<TransitionSeries> pileups = map(tsGroups,
			tsList -> {
				return foldr(tsList, tsSum);
			}
		);

		//sum the pileups
		TransitionSeries result = foldr(pileups, tsSum);
		return result;

	}

	/**
	 * Creates a new {@link TransitionSeries} representing the effect of two {@link TransitionSeries} occasionally being detected simultaneously by a detector 
	 * @param other the other {@link TransitionSeries} being detected
	 * @return a Composite {@link TransitionSeries}
	 */
	public TransitionSeries summation(final TransitionSeries other)
	{

		//one of these should be a primary TS
		//if (mode != TransitionSeriesMode.PRIMARY && other.mode != TransitionSeriesMode.PRIMARY) return null;

		TransitionSeriesMode newmode = TransitionSeriesMode.SUMMATION;

		if (this.equals(other)) newmode = TransitionSeriesMode.PILEUP;
		if (this.mode == TransitionSeriesMode.PILEUP && this.element.equals(other.element)) newmode = TransitionSeriesMode.PILEUP;
		if (other.mode == TransitionSeriesMode.PILEUP && other.element.equals(this.element)) newmode = TransitionSeriesMode.PILEUP;

		// create the new TransitionSeries object
		final TransitionSeries newTransitionSeries = new TransitionSeries(
			element,
			TransitionSeriesType.COMPOSITE,
			newmode);
		
		if (transitions.size() == 0) return newTransitionSeries;

		List<List<Transition>> allPileupLists = map(transitions,
			// map each of the transitions
			t1 -> {
				// For each transition in the outer map, map the list transitionList to a list of pileup values
				return map(other.transitions, t2 ->t1.summation(t2));
			}
		);

		List<Transition> allPileups = concat(allPileupLists);

		each(allPileups, t -> newTransitionSeries.setTransition(t));

		newTransitionSeries.componentSeries.add(this);
		newTransitionSeries.componentSeries.add(other);

		return newTransitionSeries;

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

				Collections.sort(componentSeries);
				Collections.sort(otherTS.componentSeries);

				List<Integer> differences = filter(zipWith(componentSeries, otherTS.componentSeries, (ts1, ts2) -> ts1.compareTo(ts2)), a -> a.equals(0));

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
		if (type != TransitionSeriesType.COMPOSITE) 
		{
			return type.ordinal() * element.ordinal();
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
		
		if (type != TransitionSeriesType.COMPOSITE && other.element != this.element) return false;
		
		if (other.type != this.type) return false;	
		if (other.mode != this.mode) return false;

		if (type == TransitionSeriesType.COMPOSITE)
		{
			Collections.sort(componentSeries);
			Collections.sort(other.componentSeries);

			if ( !all(zipWith(componentSeries, other.componentSeries, (a, b) -> a.equals(b))) ) return false;
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
	 * Returns the base {@link TransitionSeriesType} for this {@link TransitionSeries}
	 * @return the base {@link TransitionSeriesType}
	 */
	public TransitionSeriesType getBaseType()
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
				return concatMap(componentSeries, TransitionSeries::getBaseTransitionSeries);

			default:
				list = new ArrayList<TransitionSeries>();
				list.add(this);
				return list;
		}

	}

	/**
	 * Returns a list containing the {@link Element} name and {@link TransitionSeriesType} for this {@link TransitionSeries}. if this {@link TransitionSeries} is not a primary one, null is returned 
	 * @return
	 */
	public List<String> toSerializableList()
	{
		if (type == TransitionSeriesType.COMPOSITE) return null;
		return new FList<String>(element.name(), type.name());
	}


}
