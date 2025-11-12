package org.peakaboo.framework.accent.numeric;

import org.peakaboo.framework.accent.Pair;

/**
 * Pair<Integer, Integer> can be slow to construct because of the autoboxing of
 * int values to Integers. This is especially noticeable when performed thousands
 * or even millions of times. IntPair provides a very similar but faster
 * construct
 *
 */
public class IntPair {

	public int first, second;

	/**
	 * Constructor to create a IntPair with preset values
	 * 
	 * @param first
	 * @param second
	 */
	public IntPair(int first, int second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Constructor to create an empty IntPair
	 */
	public IntPair() {
		first = 0;
		second = 0;
	}

	/**
	 * Converts this IntPair into a human-readable String representation
	 * 
	 * @return a human-readable String representation of this Pair
	 */
	public String show() {
		String firstString, secondString;
		firstString = first + "";
		secondString = second + "";
		return "(" + firstString + "," + secondString + ")";
	}

	/**
	 * For compatibility reasons, sometimes you just need a regular Pair object.
	 * Note that this creates a copy of this IntPair. Modifications to the Pair will
	 * not be reflected in this IntPair.
	 */
	public Pair<Integer, Integer> toPair() {
		return new Pair<>(first, second);
	}

}
