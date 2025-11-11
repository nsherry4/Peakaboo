package org.peakaboo.framework.accent.numeric;

import org.peakaboo.framework.accent.Sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a range of integer values including a step size. The representation
 * of the range is inclusive of the upper bound value.
 * <br><br>
 * While convention is to make the upper bound exclusive, this does not work as well
 * when step sizes are introduced. A range of 1..5 representing [1, 2, 3, 4] may
 * work well, but a range of 3..9:3 representing only [3, 6] and not [3, 6, 9] makes
 * the exclusive upper bound a less appealing notation in this case.
 *
 * @author Nathaniel Sherry
 *
 */

public class Range extends Sequence<Integer> {
	
	private int start, stop, step;
	
	
	/**
	 * Creates an empty range representing no values. This is useful
	 * when attempting to create a a range which does not overlap
	 * with any other range.
	 * <br><br>
	 * Note: Because the upper bound is inclusive to prevent odd
	 * notation when dealing with step sizes other than 1, range(0,0)
	 * will represent 0<=x<=0 rather than the more conventional
	 * 0<=x<0, and so range(0,0) will contain 0 as its only value.
	 */
	public Range() {}
	
	/**
	 * Creates a new range from start to stop inclusive. While convention
	 * is to make the upper bound exclusive, this does not work as well
	 * when step sizes are introduced. A range of 1..5 representing
	 * [1, 2, 3, 4] may work well, but a range of 3..9:3 representing only
	 * [3, 6] and not [3, 6, 9] makes the exclusive upper bound a less
	 * appealing notation in this case.
	 * <br><br>
	 * Note: Because the upper bound is inclusive to prevent odd
	 * notation when dealing with step sizes other than 1, range(0,0)
	 * will represent 0<=x<=0 rather than the more conventional
	 * 0<=x<0, and so range(0,0) will contain 0 as its only value.
	 * @param start the starting value of this range
	 * @param stop the stopping value of this range (inclusive)
	 */
	public Range(int start, int stop) {
		this(start, stop, 1);
	}
	
	/**
	 * Creates a new range from start to stop inclusive. While convention
	 * is to make the upper bound exclusive, this does not work as well
	 * when step sizes are introduced. A range of 1..5 representing
	 * [1, 2, 3, 4] may work well, but a range of 3..9:3 representing only
	 * [3, 6] and not [3, 6, 9] makes the exclusive upper bound a less
	 * appealing notation in this case.
	 * <br><br>
	 * Note: Because the upper bound is inclusive to prevent odd
	 * notation when dealing with step sizes other than 1, range(0,0)
	 * will represent 0<=x<=0 rather than the more conventional
	 * 0<=x<0, and so range(0,0) will contain 0 as its only value.
	 * @param start the starting value of this range
	 * @param stop the stopping value of this range (inclusive)
	 * @param step the step size of this range
	 */
	public Range(int start, final int stop, final int step) {
		super();
		
		if (step == 0) {
			throw new IllegalArgumentException("Step size cannot be zero");
		}
		if (stop < start && step > 0) {
			throw new IllegalArgumentException("Stop cannot be less than start with a positive step size");
		}
		if (stop > start && step < 0) {
			throw new IllegalArgumentException("Stop cannot be greater than start with a negative step size");
		}
		
		this.start = start;
		this.stop = stop;
		this.step = step;
		
		super.setStartValue(this.start);
		super.setNextFunction(element -> {
			if (element == null) return null;
			Integer next = element+Range.this.step;
			
			if (step < 0) {
				return next >= Range.this.stop ? next : null;
			} else {
				return next <= Range.this.stop ? next : null;
			}
		});
		
	}
	
	
	/**
	 * Creates a new range based on the length of the range
	 * rather than the upper bound value. fromLength(1, 3) will
	 * create a range representing the values [1, 2, 3]
	 * @param start the starting value of the range
	 * @param length the length of the range
	 * @return a new Range representing the parameters given
	 */
	public static Range fromLength(int start, int length) {
		return new Range(start, start + length - 1);
	}
	
	/**
	 * Creates a new range based on the length of the range
	 * rather than the upper bound value. fromLength(1, 5, 2) will
	 * create a range representing the values [1, 3, 5]
	 * @param start the starting value of the range
	 * @param length the length of the range
	 * @param stepsize the stepsize of the range
	 * @return a new Range representing the parameters given
	 */
	public static Range fromLength(int start, int length, int stepsize) {
		return new Range(start, start + length - 1, stepsize);
	}
	
	
	/**
	 * The integer span of this {@link Range}. Note that for step sizes greater than 1, this is not the same as the number of 
	 * elements in the range. To determine that value, call {@link Range#elementCount()}
	 * @return the size of the span of the {@link Range}
	 */
	public int size() {
		return Math.abs(stop - start) + 1;
	}
	
	/**
	 * The absolute step size
	 * @return abs(step)
	 */
	public int stepSize() {
		return Math.abs(step);
	}
	
	/**
	 * Returns a phase value representing the start value modulo the step size.
	 * Ranges starting at 0 will always have a phase valeu of 0
	 */
	public int phase() {
		return this.start % this.step;
	}
	
	/**
	 * Calculates the last value from this range. This is not the step size, but
	 * rather the last value returned by an iterator or by
	 * {@link Range#asList()}
	 */
	public int last() {
		return start + (elementCount()-1) * step;
	}
	
	/**
	 * Returns the values from this range as a {@link List}
	 */
	public List<Integer> asList() {
		List<Integer> ints = new ArrayList<>();
		for (int value : this) {
			ints.add(value);
		}
		return ints;
	}
	
	/**
	 * The number of elements contained in this {@link Range}. Note that for step sizes greater than 1, this is not the same 
	 * as the size (span) of the {@link Range}.
	 * @return the number of integer values included in this {@link Range}
	 */
	public int elementCount() {
		return (int)Math.floor((stop-start)/(double)step) + 1;
	}
	
	//TODO: this could be much faster
	public boolean contains(int value) {
		for (int i : this) {
			if (i == value) { return true; }
		}
		return false;
	}
	
	/**
	 * Determines if this {@link Range} occupies the same area as another {@link Range}. Overlapped ranges do not need to share any points, 
	 * it is sufficient that their start->end spans overlap in some way.
	 * @param other the other {@link Range} to compare against
	 * @return true if the two {@link Range} occupy some of the same area, false otherwise 
	 */
	public boolean isOverlapped(Range other) {
		
		//if neither of their ends lies within our ends, and none of our ends lies within theirs
		return
				// check if their stop or start in contained in our range
				(other.stop >= start && other.stop <= stop)
						||
						(other.start >= start && other.start <= stop)
						||
						//check if the other range completely engulfs us, since then neither of their ends would be in our range
						(other.start < start && other.stop > stop);
	}
	
	/**
	 * Determines if this {@link Range} shares some or all of it's points with another {@link Range}. {@link Range} which overlap by start and end position, 
	 * but which have different step sizes, or have the same step size, but out of phase, are considered to be not 
	 * truly coincident. This allows for the creation of more complex patterns using {@link RangeSet}, such as 
	 * joining two ranges: eg 1..11:3 => [1, 4, 7, 10] and 2..12:3 => [2, 5, 8, 11] to produce [1, 2, 4, 5, 7, 8, 10, 11] 
	 * @param other the other Range to compare against
	 * @return true if the two Range contain common elements with a common step size, false otherwise
	 */
	public boolean isCoincident(Range other)
	{
		
		if (!isOverlapped(other)) return false;
		
		//if their step sizes aren't the same, they don't really overlap
		if (other.step != step) return false;
		
		//if they overlap, with the same step size, but are out of phase, they don't really overlap
		if (other.phase() != this.phase()) return false;
		
		
		//looks like they overlap
		return true;
		
	}
	
	/**
	 * Determines if two Range are adjacent. Ranges are considered adjacent if
	 * <ul>
	 * <li>Their step sizes match</li>
	 * <li>They are in phase</li>
	 * <li>The starting value of one is one step after the stopping value of the other</li>
	 * </ul>
	 *
	 * @param other the other Range to examine
	 * @return true if the two Ranges are one step apart from each other, with the same step and phase, false otherwise
	 */
	public boolean isAdjacent(Range other)
	{
		//step size must match
		if (other.step != this.step) return false;
		
		//must be in phase
		if (other.phase() != this.phase()) return false;
		
		//first value of one must be one step after the last value of the other
		return (other.start == this.last() + this.step || this.start == other.last() + this.step);
		
	}
	
	/**
	 * Equivalent to {@link Range#isCoincident(Range)} OR {@link Range#isAdjacent(Range)}
	 * @param other the Range to check this Range against
	 * @return true if the two ranges are touching, false otherwise
	 */
	public boolean isTouching(Range other)
	{
		return isCoincident(other) || isAdjacent(other);
	}
	
	
	/**
	 * Merges two Range for which {@link Range#isTouching(Range)} returns true. Returns null if the Range do not
	 * satisfy this requirement.
	 * @param other the other Range to merge this Range with
	 * @return a new Range representing the union of the elements of both
	 */
	public Range merge(Range other)
	{
		if (! isTouching(other) ) return null;
		return new Range(Math.min(other.start, start), Math.max(other.stop, stop), step);
	}
	
	/**
	 * Returns a RangeSet representing this Range with the elements in the other Range removed. The Ranges must satisfy 
	 * {@link Range#isOverlapping(Range)}. If the Ranges are not overlapping, then the returned RangeSet will simply 
	 * represent this Range
	 * @param other the Range to remove from this Range
	 * @return a RangeSet representing elements in this Range which are not in the other Range
	 */
	public RangeSet difference(Range other)
	{
		var result = new RangeSet();
		
		if (! isCoincident(other)) {
			result.addRange(this);
			return result;
		}
		
		//other.start-1 because the range class uses an inclusive upper bound
		//other.stop+1 for similar reasons
		if (this.start < other.start) result.addRange(new Range(start, other.start-1, step));
		if (this.stop > other.stop) result.addRange(new Range(other.stop+1, stop, step));
		
		return result;
		
	}
	
	
	@Override
	public String toString()
	{
		return "[" + start + ".." + stop + (step == 1 ? "" : ":" + step) + "]";
	}
	
	
	/**
	 * Get the start value of this Range
	 * @return the lower bound and first element in this Range
	 */
	public int getStart() {
		return start;
	}
	
	
	/**
	 * Get the stop value of this Range
	 * @return the upper bound of this Range
	 */
	public int getStop() {
		return stop;
	}
	
	
	/**
	 * Get the step size of this Range
	 * @return the interval between consecutive elements in this Range 
	 */
	public int getStep() {
		return step;
	}
}
