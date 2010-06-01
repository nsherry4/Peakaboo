package peakaboo.datatypes;


import java.io.Serializable;

/**
 * 
 *  This class provides a method of storing a range using any datatype
 *  The X and Y values are of the same type
 *
 * @author Nathaniel Sherry, 2009
 * @param <T> The type of data to be held in both slots of this range pair
 */


public class Range<T> implements Serializable {

	public T start, end;
	
	public Range(T start, T stop){
		this.start = start;
		this.end = stop;
	}
	
}