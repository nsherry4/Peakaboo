package peakaboo.datatypes.peaktable;



import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Function2;
import peakaboo.datatypes.functional.Functional;
import peakaboo.datatypes.functional.stock.Functions;



/**
 * This class is a representation of all the {@link Transition}s for a given {@link Element} that fall into a specific
 * {@link TransitionSeriesType}
 * 
 * @author Nathaniel Sherry
 */

public class TransitionSeries implements Serializable, Iterable<Transition>, Comparable<TransitionSeries>
{

	/**
	 * The {@link TransitionSeriesType} that this TransitionSeries represents
	 */
	public TransitionSeriesType		type;
	public TransitionSeriesMode		mode;

	/**
	 * If this is a compound TransitionSeries, this list contains the component TransitionSeries
	 */
	private List<TransitionSeries>	componentSeries;

	/**
	 * The {@link Element} that this TransitionSeries represents
	 */
	public Element					element;

	private List<Transition>		transitions;

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
	 * Creates a new TransitionSeries for the given {@link Element} and {@link TransitionSeriesType}
	 * 
	 * @param element
	 * @param seriesType
	 */
	public TransitionSeries(Element element, TransitionSeriesType seriesType, TransitionSeriesMode mode)
	{
		this.element = element;
		this.type = seriesType;
		this.mode = mode;
		intensity = 1.0;
		visible = true;

		transitions = DataTypeFactory.<Transition> list();
		componentSeries = DataTypeFactory.<TransitionSeries> list();
	}
	
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
		List<Transition> matches = Functional.filter(transitions, new Function1<Transition, Boolean>() {

			public Boolean f(Transition t)
			{
				return t.type == transitionType;
			}
		});

		if (matches.size() > 0) return matches.get(0);
		return null;
	}


	public Transition getStrongestTransition()
	{

		return Functional.foldr(transitions, new Function2<Transition, Transition, Transition>() {

			public Transition f(Transition t1, Transition t2)
			{
				if (t1.relativeIntensity > t2.relativeIntensity) return t1;
				return t2;
			}
		});

	}


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
				if (count > 2) suffix += " x" + +count;

				return componentSeries.get(0).element.name() + " " + componentSeries.get(0).getBaseType().name()
						+ " Pile-Up" + suffix;

			case SUMMATION:

				Collections.sort(componentSeries);

				return Functional.foldr(Functional.map(componentSeries, new Function1<TransitionSeries, String>() {

					public String f(TransitionSeries ts)
					{
						return ts.getDescription();
					}
				}), Functions.concat(" ⊕ "));

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


	public double getLowestEnergyValue()
	{
		double lowest = Double.MAX_VALUE;
		for (Transition t : transitions)
		{
			if (t.energyValue < lowest) lowest = t.energyValue;
		}
		return lowest;
	}


	/*public TransitionSeries pileup()
	{	
		
		// make a copy of the transitions list
		final List<Transition> transitionList = Functional.map(transitions, Functions.<Transition> id());

		// create the new TransitionSeries object
		final TransitionSeries newTransitionSeries = new TransitionSeries(element, type, TransitionSeriesMode.PILEUP);

		if (transitions.size() == 0) return newTransitionSeries;

		List<List<Transition>> allPileupLists = Functional.map(
				transitions,
				new Function1<Transition, List<Transition>>() {

					// map each of the transitions

					public List<Transition> f(final Transition t1)
					{

						//
						//
						// For each transition in the outer map, map the list transitionList to a list of
						// pileup values
						transitionList.remove(t1);
						return Functional.map(transitionList, new Function1<Transition, Transition>() {

							public Transition f(Transition t2)
							{
								return t1.summation(t2);
							}
						});

					}
				});

		List<Transition> allPileups = Functional.foldr(allPileupLists, Functions.<Transition> listConcat());

		Functional.each(allPileups, new Function1<Transition, Object>() {

			public Object f(Transition t)
			{
				newTransitionSeries.setTransition(t);
				return null;
			}
		}

		);

		newTransitionSeries.componentSeries.add(this);
		newTransitionSeries.componentSeries.add(this);

		return newTransitionSeries;

	}*/

	public static TransitionSeries summation(final List<TransitionSeries> tss)
	{

		if (tss.size() == 0) return null;

		if (tss.size() == 1) return tss.get(0);
		
		//group the TransitionSeries by equality
		List<List<TransitionSeries>> tsGroups = Functional.groupBy(tss, Functions.<TransitionSeries> equiv());


		//function for summing two TransitionSeries
		final Function2<TransitionSeries, TransitionSeries, TransitionSeries> tsSum = new Function2<TransitionSeries, TransitionSeries, TransitionSeries>() {

			public TransitionSeries f(TransitionSeries ts1, TransitionSeries ts2)
			{
				return ts1.summation(ts2);
			}
		};


		//turn the groups of primary transitionseries into a list of pile-up transitionseries
		List<TransitionSeries> pileups = Functional.map(
				tsGroups,
				new Function1<List<TransitionSeries>, TransitionSeries>() {

					public TransitionSeries f(List<TransitionSeries> tsList)
					{
						return Functional.foldr(tsList, tsSum);
					}
				});

		//sum the pileups
		TransitionSeries result = Functional.foldr(pileups, tsSum);
		return result;

	}


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

		System.out.println(transitions.size() == 0);
		
		if (transitions.size() == 0) return newTransitionSeries;

		List<List<Transition>> allPileupLists = Functional.map(
				transitions,
				new Function1<Transition, List<Transition>>() {

					// map each of the transitions

					public List<Transition> f(final Transition t1)
					{

						//
						//
						// For each transition in the outer map, map the list transitionList to a list of
						// pileup values
						return Functional.map(other.transitions, new Function1<Transition, Transition>() {

							public Transition f(Transition t2)
							{
								return t1.summation(t2);
							}
						});

					}
				});

		List<Transition> allPileups = Functional.foldr(allPileupLists, Functions.<Transition> listConcat());

		Functional.each(allPileups, new Function1<Transition, Object>() {

			public Object f(Transition t)
			{
				newTransitionSeries.setTransition(t);
				return null;
			}
		}

		);

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

				List<Integer> differences =
						Functional.filter(Functional.zipWith(
								componentSeries,
								otherTS.componentSeries,
								new Function2<TransitionSeries, TransitionSeries, Integer>() {

									public Integer f(TransitionSeries ts1, TransitionSeries ts2)
							{
								return ts1.compareTo(ts2);
							}
								}),
								new Function1<Integer, Boolean>() {

									public Boolean f(Integer element)
							{
								return element != 0;
							}
								});

				if (differences.size() == 0) return 0;
				return differences.get(0);

		}

		return 0;

	}


	public boolean equals(Object oother)
	{

		if (!(oother instanceof TransitionSeries)) return false;
		TransitionSeries other = (TransitionSeries) oother;

		if (type != TransitionSeriesType.COMPOSITE)
		{
			if (other.element != this.element) return false;
		}

		if (other.type != this.type) return false;
		if (other.mode != this.mode) return false;

		if (type == TransitionSeriesType.COMPOSITE)
		{
			Collections.sort(componentSeries);
			Collections.sort(other.componentSeries);

			if (!Functional.foldr(
					Functional.zipWith(componentSeries, other.componentSeries, Functions.<TransitionSeries> equiv()),
					Functions.and()
				)) return false;
		}

		return true;

	}


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


	public List<TransitionSeries> getBaseTransitionSeries()
	{
		List<TransitionSeries> list = null;

		switch (type)
		{
			case COMPOSITE:

				list = Functional.flatten(Functional.map(componentSeries, new Function1<TransitionSeries, List<TransitionSeries>>() {

					public List<TransitionSeries> f(TransitionSeries ts)
					{
						return ts.getBaseTransitionSeries();
					}
				}));

				return list;

			default:
				list = DataTypeFactory.<TransitionSeries> list();
				list.add(this);
				return list;
		}

	}

	public Pair<String, String> toSerializablePair()
	{
		if (type == TransitionSeriesType.COMPOSITE) return null;
		return new Pair<String, String>(element.name(), type.name());
	}


}
