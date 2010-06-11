package peakaboo.datatypes.peaktable;



import java.util.Iterator;
import java.util.List;
import java.util.Map;

import peakaboo.datatypes.DataTypeFactory;
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

public class TransitionSeries implements Iterable<Transition>, Comparable<TransitionSeries>
{

	/**
	 * The {@link Element} that this TransitionSeries represents
	 */
	public Element				element;

	/**
	 * The {@link TransitionSeriesType} that this TransitionSeries represents
	 */
	public TransitionSeriesType	type;
	public TransitionSeriesMode mode;
	
	private List<Transition>	transitions;
	
	

	/**
	 * The general intensity of this TransitionSeries
	 */
	public double				intensity;

	/**
	 * Toggle for the visibility of this TransitionSeries
	 */
	public boolean				visible;


	
	public String getDescription()
	{
		switch (mode){
			
			case PILEUP:
				
				return element.toString() + " Pile Up";
				
			case SUMMATION:
				
				return "";
				
			default:
				
				return element.toString() + ": " + type.toString();
				
		}
		
	}
	
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


	/**
	 * Returns a String representation in the form of "K Transition Series"
	 */
	@Override
	public String toString()
	{
		return type.toString() + " Transition Series";
	}


	/**
	 * Returns a String representation in the form of "Fe (K)"
	 * 
	 * @return the element-name string
	 */
	public String toElementString()
	{
		return element.name() + " (" + type.toString() + ")";
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


	public int compareTo(TransitionSeries otherTS)
	{

		if (otherTS.element == element)
		{
			return type.compareTo(otherTS.type);
		}
		else
		{
			return element.compareTo(otherTS.element);
		}

	}


	public TransitionSeries pileup()
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

		)

		;

		return newTransitionSeries;

	}
	
	public TransitionSeries summation(final TransitionSeries other)
	{

		// create the new TransitionSeries object
		final TransitionSeries newTransitionSeries = new TransitionSeries(element, TransitionSeriesType.COMPOSITE, TransitionSeriesMode.SUMMATION);

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

		)

		;

		return newTransitionSeries;

	}
	
}
