package cyclops;

import java.io.Serializable;

/**
 * 
 *  This class provides a method of storing an xy coordinate using any datatype
 *  The X and Y values are of the same type
 *
 * @author Nathaniel Sherry, 2009
 * @param <T> The type of data to be held in both slots of this coordinate pair
 */


public class Coord<T> implements Serializable {

	public T x, y;
	
	public Coord(T x, T y){
		this.x = x;
		this.y = y;
	}
	
	public Coord(Coord<T> copy)
	{
		this.x = copy.x;
		this.y = copy.y;
	}
	
	@Override
	public boolean equals(Object oother)
	{
		if (!(oother instanceof Coord)) return false;
		Coord<?> gother = (Coord<?>)oother;
		if (!x.getClass().equals(gother.x.getClass())) return false;
		@SuppressWarnings("unchecked")
		Coord<T> other = (Coord<T>)oother;
		return x.equals(other.x) && y.equals(other.y);
	}
	
	@Override
	public int hashCode()
	{
		return 0;
	}
	
	@Override
	public String toString()
	{
		return "(" + x.toString() + "," + y.toString() + ")";
	}
	
	
}
