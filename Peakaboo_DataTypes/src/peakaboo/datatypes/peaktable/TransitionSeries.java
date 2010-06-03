package peakaboo.datatypes.peaktable;


import java.util.Iterator;
import java.util.Map;

import peakaboo.datatypes.DataTypeFactory;

/**
 * 
 * This class is a representation of all the {@link Transition}s for a given {@link Element} that fall into a specific {@link TransitionSeriesType}
 * 
 * @author Nathaniel Sherry
 *
 */

public class TransitionSeries implements Iterable<Transition>, Comparable<TransitionSeries>
{

	
	/**
	 * The {@link Element} that this TransitionSeries represents
	 */
	public Element							element;
	
	/**
	 * The {@link TransitionSeriesType} that this TransitionSeries represents
	 */
	public TransitionSeriesType				type;
	
	private Map<TransitionType, Transition>	transitions;
	
	/**
	 * The general intensity of this TransitionSeries
	 */
	public double							intensity;
	
	/**
	 * Toggle for the visibility of this TransitionSeries
	 */
	public boolean							visible;


	/**
	 * Is this TransitionSeries visible?
	 * @return visibility
	 */
	public boolean isVisible()
	{
		return visible;
	}


	/**
	 * Sets the visibility of this TransitionSeries
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}


	/**
	 * Creates a new TransitionSeries for the given {@link Element} and {@link TransitionSeriesType}
	 * @param element
	 * @param seriesType
	 */
	public TransitionSeries(Element element, TransitionSeriesType seriesType)
	{
		this.element = element;
		this.type = seriesType;
		intensity = 1.0;
		visible = true;
		
		transitions = DataTypeFactory.<TransitionType, Transition> map();
	}


	/**
	 * Retrieves a {@link Transition} from this TransitionSeries based on the provided {@link TransitionType}
	 * @param transitionType the type of {@link Transition} to retrieve
	 * @return the {@link Transition} for the given {@link TransitionType}
	 */
	public Transition getTransition(TransitionType transitionType)
	{
		return transitions.get(transitionType);
	}


	/**
	 * Sets the {@link Transition} for the given {@link TransitionType}
	 * @param type the {@link TransitionType} to fill
	 * @param t the {@link Transition}
	 */
	public void setTransition(TransitionType type, Transition t)
	{
		if (t == null) return;
		transitions.put(type, t);
	}


	/**
	 * Returns the number of filled {@link Transition}s in this TransitionSeries
	 * @return the number of {@link Transition}s in this TransitionSeries
	 */
	public int getTransitionCount()
	{
		return transitions.size();
	}


	/**
	 * Provides an Iterator of type {@link Transition} for iteration over the list of {@link Transition}s in this TransitionSeries
	 * @return an iterator of type {@link Transition}
	 */
	public Iterator<Transition> iterator()
	{
		return transitions.values().iterator();
	}


	/**
	 * Calculates how close this TransitionSeries is to a given energy by examining the energy point of each {@link Transition} in this TransitionSeries
	 * @param energy the energy to compare to
	 * @return the minimum distance of any {@link Transition} in this TransitionSeries to energy
	 */
	public double getProximityToEnergy(double energy)
	{

		double nearest = Math.abs(transitions.values().iterator().next().energyValue - energy);
		double current;

		for (Transition t : transitions.values()) {

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
	 * @return the element-name string
	 */
	public String toElementString()
	{
		return element.name() + " (" + type.toString() + ")";
	}
	
	public double getLowestEnergyValue()
	{
		double lowest = Double.MAX_VALUE;
		for (Transition t : transitions.values()){
			if (t.energyValue < lowest) lowest = t.energyValue;
		}
		return lowest;
	}


	public int compareTo(TransitionSeries otherTS) {
		
		if (otherTS.element == element)
		{
			return type.compareTo(otherTS.type);
		}
		else
		{
			return element.compareTo(otherTS.element);
		}
		
	}

}
