package cyclops;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Produces a sequence of values by taking an initial value and a {@link Function} to produce the next value 
 * from the current one. If the function returns null, it indicates the end of the sequence.
 * 
 * @author Nathaniel Sherry, 2010-2017
 *
 * @param <T>
 */

public class Sequence<T> implements Iterable<T> 
{

	private Function<T, T> f;
	private T start;
	
	
	/**
	 * Creates a new Sequence with the given starting value and {@link Function} to compute the following values
	 * @param start the initial value
	 * @param f the sequence function to compute further values
	 */
	public Sequence(T start, Function<T, T> f) {
		
		this.f = f;
		this.start = start;
		
	}
	
	
	/**
	 * Applies the function for generating new elements to the given value, and returns the result 
	 * @return the next value in the sequence as determined by the sequence function
	 */
	public T next(T current)
	{
		if (current == null) { throw new NoSuchElementException(); }
		T newValue = f.apply(current);
		current = newValue;
		return current;
	}
	

	/**
	 * Empty constructor for subclassing
	 */
	protected Sequence()
	{
		
	}
	
	/**
	 * Sets the sequence function for generating new values from the current one
	 * @param next
	 */
	protected void setNextFunction(Function<T, T> next)
	{
		f = next;
	}
	
	/**
	 * Sets the start value for generating new values
	 * @param start
	 */
	protected void setStartValue(T start)
	{
		this.start = start;
	}
	
	
	public Stream<T> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED + Spliterator.IMMUTABLE + Spliterator.NONNULL), false);
	}
	
	
	public Iterator<T> iterator() {
		
		return new Iterator<T>() {

			private T current = start;
			
			
			
			public boolean hasNext() {
				
				return (current != null);
				
			}

			public T next() {
				
				//store the current value so that we can return it
				T cur = current;
												
				//the next element becomes the new current element
				current = f.apply(current);

				//return the current value
				return cur;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}


}
