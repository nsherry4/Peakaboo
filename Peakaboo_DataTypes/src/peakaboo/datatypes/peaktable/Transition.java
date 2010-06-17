package peakaboo.datatypes.peaktable;

import java.io.Serializable;

/**
 * 
 *  This class stores the information about a single transition as defined in the peak table
 *
 * @author Nathaniel Sherry, 2009
 */


public class Transition implements Serializable, Comparable<Transition>{

	/**
	 * The energy value of this transition -- where the peak will be centred
	 */
	public float energyValue;
	
	public TransitionType type;
	
	/**
	 * The relative intensity of this peak compared to the a1 peak of the same {@link TransitionSeriesType}
	 */
	public float relativeIntensity;

	/**
	 * Create a new Transition
	 * @param value energy value of this Transition
	 * @param relativeIntensity relative intensity of this Transition
	 */
	public Transition(float value, float relativeIntensity, TransitionType type){

		this.energyValue = value;
		this.relativeIntensity = relativeIntensity;
		this.type = type;
	}


	/**
	 * Compares this Transition to the given other transition
	 * @param other the Transition to compare against this one
	 * @return <0 if this energy value is less than other's, 0 if they're equal, or >0 if this energy value is greater than other's
	 */
	public int compareTo(Transition other) {

		float myValue = Math.abs(energyValue);
		float itsValue = Math.abs(other.energyValue);

		if (myValue < itsValue) return -1;
		if (myValue > itsValue) return 1;
		
		return 0;
	}
	
	public Transition summation(Transition other)
	{
		return new Transition(energyValue + other.energyValue, relativeIntensity * other.relativeIntensity, TransitionType.pileup);
	}
	
}
