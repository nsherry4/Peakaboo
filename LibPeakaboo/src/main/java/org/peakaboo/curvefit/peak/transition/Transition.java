package peakaboo.curvefit.peak.transition;

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
	public final float energyValue;
	
	/**
	 * The relative intensity of this peak compared to the a1 peak of the same {@link ITransitionSeries}
	 */
	public final float relativeIntensity;
	
	public final String name;

	/**
	 * Create a new Transition
	 * @param energyValue energy value of this Transition
	 * @param relativeIntensity relative intensity of this Transition
	 */
	public Transition(float energyValue, float relativeIntensity, String name){

		this.energyValue = energyValue;
		this.relativeIntensity = relativeIntensity;
		this.name = name;
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
	
	/**
	 * Creates a new Transition representing the effect of a detector recording both this Transition and the other given Transition simultaneously
	 * @param other the other Transition
	 * @return a new Transition representing simultaneous detection of both
	 */
	public Transition summation(Transition other)
	{
		return new Transition(energyValue + other.energyValue, relativeIntensity * other.relativeIntensity, "(" + this.name + " + " + other.name + ")");
	}

	
	@Override
	public String toString()
	{
		return "Transition '" + this.name + "': " + energyValue + "keV @ " + relativeIntensity;
	}
	
}
