package org.peakaboo.framework.cyclops;

import java.util.ArrayList;
import java.util.List;

public class Range extends Sequence<Integer> {

	private int	start, stop, step;

	
	/**
	 * Creates an empty range representing no values. This is useful
	 * when attempting to create a a range which does not overlap 
	 * with any other range.
	 * <br><br>
	 * Note: The upper bound is exclusive, so range(0,0)
	 * will represent 0<=x<0
	 */
	public Range() {}
		
	/**
	 * Creates a new range from start to stop exclusive.
	 * <br><br> 
	 * @param start the starting value of this range
	 * @param stop the stopping value of this range (exclusive)
	 */
	public Range(int start, int stop) {
		this(start, stop, 1);
	}

	/**
	 * Creates a new range from start to stop exclusive. 
	 * <br><br> 
	 * @param start the starting value of this range
	 * @param stop the stopping value of this range (exclusive)
	 * @param step the step size of this range
	 */
	public Range(int start, final int stop, final int step) {
		super();
		
		//always trust the step size -- reverse the numbers if they don't agree with the step size
		if (stop < start && step > 0)
		{
			//if the numbers are backwards, and we're not counting down
			//but the stop value is exclusive and the start value is inclusive,
			//so we have to modify the values to make that work here
			this.start = stop+1;
			this.stop = start+1;
			
			this.step = step;
		}
		else if (stop > start && step < 0)
		{
			//we're counting down, but the numbers are in order
			this.start = stop;
			this.stop = start;
			
			this.step = step;
			
			
		} else {
			
			//if the numbers are forwards
			this.start = start;
			this.stop = stop;
			this.step = step;
		}
			
			
		
		
		super.setStartValue(this.start);
		super.setNextFunction(element -> {
			if (element == null) return null;
			Integer next = element+Range.this.step;			
			
			if (step < 0) {
				return next > Range.this.stop ? next : null;
			} else {
				return next < Range.this.stop ? next : null;	
			}
		});
		
		
	}

	/**
	 * The integer span of this {@link Range}. Note that for step sizes greater than 1, this is not the same as the number of 
	 * elements in the range. To determine that value, call {@link InclusiveRange#elementCount()}
	 * @return the size of the span of the {@link Range}
	 */
	public int size()
	{
		return stop - start;
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
		return this.stop - this.step + this.phase();
	}
	
	/**
	 * Returns the values from this range as a {@link List}
	 */
	public List<Integer> asList() {
		List<Integer> l = new ArrayList<>();
		for (int i : this) {
			l.add(i);
		}
		return l;
	}
	
	/**
	 * The number of elements contained in this {@link Range}. Note that for step sizes greater than 1, this is not the same 
	 * as the size (span) of the {@link Range}.
	 * @return the number of integer values included in this {@link Range}
	 */
	public int elementCount()
	{
		return (int)Math.ceil(size() / (float)step);
	}

	//TODO: this could be faster
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
				(other.stop > start && other.stop <= stop) 
				|| 
				(other.start >= start && other.start < stop)
				||
				//check if the other range completely engulfs us, since then neither of their ends would be in our range
				(other.start < start && other.stop > stop);
	}
	
	/**
	 * Determines if this {@link Range} shares some or all of it's points with another {@link Range}. {@link Range} which overlap by start and end position, 
	 * but which have different step sizes, or have the same step size, but out of phase, are considered to be not 
	 * truly coincident. This allows for the creation of more complex patterns using {@link RangeSet}, such as 
	 * joining two ranges: eg 1..11:3 => [1, 4, 7, 10] and 2..12:3 => [2, 5, 8, 11] to produce [1, 2, 4, 5, 7, 8, 10, 11] 
	 * @param other the other ExclusiveRange to compare against
	 * @return true if the two ExclusiveRange contain common elements with a common step size, false otherwise 
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
	 * Determines if two ExclusiveRange are adjacent. ExclusiveRange are considered adjacent if 
	 * <ul>
	 * <li>Their step sizes match</li>
	 * <li>They are in phase</li>
	 * <li>The first value of one is one step after the last value of the other</li>
	 * </ul>
	 * 
	 * @param other the other ExclusiveRange to examine
	 * @return true if the two ExclusiveRanges are one step apart from each other, with the same step and phase, false otherwise
	 */
	public boolean isAdjacent(Range other)
	{
		//step size must match
		if (other.step != this.step) return false;
		
		//must be in phase
		if (other.phase() != this.phase()) return false;
		
		//first value of one must be one step after the last value of the other
		return (other.start == this.last() + this.step || this.start == other.last() + other.step); 
		
	}
	
	/**
	 * Equivalent to {@link InclusiveRange#isCoincident(InclusiveRange)} OR {@link InclusiveRange#isAdjacent(InclusiveRange)}
	 * @param other the Range to check this Range against
	 * @return true if the two ranges are touching, false otherwise
	 */
	public boolean isTouching(Range other)
	{
		return isCoincident(other) || isAdjacent(other);
	}
	
	
	/**
	 * Merges two ExclusiveRange for which {@link Range#isTouching(Range)} returns true. Returns null if the ExclusiveRange do not 
	 * satisfy this requirement.
	 * @param other the other ExclusiveRange to merge this ExclusiveRange with
	 * @return a new ExclusiveRange representing the union of the elements of both
	 */
	public Range merge(Range other)
	{
		if (! isTouching(other) ) return null;
		return new Range(Math.min(other.start, start), Math.max(other.stop, stop), step);
	}
	
	/**
	 * Returns a RangeSet representing this Range with the elements in the other Range removed. The Ranges must satisfy 
	 * {@link InclusiveRange#isOverlapping(InclusiveRange)}. If the Ranges are not overlapping, then the returned RangeSet will simply 
	 * represent this Range
	 * @param other the Range to remove from this Range
	 * @return a RangeSet representing elements in this Range which are not in the other Range
	 */
	public RangeSet difference(Range other)
	{
		var result = new RangeSet();
		
		if (! isCoincident(other))
		{
			result.addRange(this);
			return result;
		}
		
		if (this.start < other.start) result.addRange(new Range(start, other.start, step));
		if (this.stop > other.stop) result.addRange(new Range(other.stop, stop, step));
		
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
