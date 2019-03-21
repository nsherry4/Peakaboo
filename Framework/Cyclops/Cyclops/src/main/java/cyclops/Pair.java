package cyclops;

import java.io.Serializable;



/**
 * 
 * This class provides a generic method of storing a pair of values. These two values do not need to be of the
 * same type. This is also useful as a workaround for Java's lack of tuples.
 * 
 * @author Nathaniel Sherry, 2009
 * @param <T> Type of first value in Pair
 * @param <S> Type of second value in Pair
 */

public class Pair<T, S> implements Serializable
{

	public T	first;
	public S	second;
	
	/**
	 * Constructor to create a Pair with preset values
	 * @param first
	 * @param second
	 */
	public Pair(T first, S second)
	{	
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Constructor to create an empty Pair
	 */
	public Pair()
	{
		first = null;
		second = null;
	}
	
	/**
	 * Converts this Pair into a human-readable String representation
	 * @return a human-readable String representation of this Pair
	 */
	public String show()
	{
		String firstString, secondString;
		firstString = first.toString();
		secondString = second.toString();
		return "(" + firstString + "," + secondString + ")";
	}
	
	
}
