package org.peakaboo.framework.scratch.list.file;


import java.io.Serializable;


/**
 * Borrows heavily from SciTypes Range
 * 
 * Represents a range of integer values including a step size. The representation
 * of the range is inclusive of the upper bound value. 
 * <br><br>
 * While convention is to make the upper bound exclusive, this does not work as well 
 * when step sizes are introduced. A range of 1..5 representing [1, 2, 3, 4] may 
 * work well, but a range of 3..9:3 representing only [3, 6] and not [3, 6, 9] makes 
 * the exclusive upper bound a less appealing notation in this case.
 * 
 * @author NAS
 *
 */

public class LongRange implements Serializable
{

	private long start, stop, step;

	
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
	public LongRange()
	{
		
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
	 */
	public LongRange(long start, long stop)
	{
		this(start, stop, 1l);
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
	public LongRange(long start, final long stop, final long step)
	{
		
		super();
		
		//always trust the step size -- reverse the numbers if they don't agree with the step size
		if (stop < start && step > 0)
		{
			//if the numbers are backwards, and we're not counting down
			this.start = stop;
			this.stop = start;
			
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
			
			
		

		
		
	}

	
	/**
	 * Creates a new range based on the length of the range 
	 * rather than the upper bound value. fromLength(1, 3) will
	 * create a range representing the values [1, 2, 3]
	 * @param start the starting value of the range
	 * @param length the length of the range
	 * @return a new Range representing the parameters given
	 */
	public static LongRange fromLength(long start, long length)
	{
		return new LongRange(start, start + length - 1l);
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
	public static LongRange fromLength(long start, long length, long stepsize)
	{
		return new LongRange(start, start + length - 1l, stepsize);
	}

	
	/**
	 * The integer span of this Range. Note that for step sizes greater than 1, this is not the same as the number of 
	 * elements in the range. To determine that value, call {@link LongRange#elementCount()}
	 * @return the size of the span of the Range
	 */
	public long size()
	{
		return stop - start + 1l;
	}

	/**
	 * The number of elements contained in this LongRange. Note that for step sizes greater than 1, this is not the same 
	 * as the size (span) of the LongRange.
	 * @return the number of long values included in this LongRange
	 */
	public long elementCount()
	{
		return (long)Math.ceil(size() / (float)step);
	}

	
	/**
	 * Determines if this LongRange occupies the same area as another LongRange. Overlapped ranges do not need to share any points, 
	 * it is sufficient that their start->end spans overlap in some way.
	 * @param other the other LongRange to compare against
	 * @return true if the two LongRange occupy some of the same area, false otherwise 
	 */
	public boolean isOverlapped(LongRange other) {
		
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
	 * Determines if this LongRange shares some or all of it's points with another LongRange. LongRanges which overlap by start and end position, 
	 * but which have different step sizes, or have the same step size, but out of phase, are considered to be not 
	 * truly coincident. This allows for the creation of more complex patterns using {@link LongRangeSet}, such as 
	 * joining two ranges: eg 1..10:3 => [1, 4, 7, 10] and 2..11:3 => [2, 5, 8, 11] to produce [1, 2, 4, 5, 7, 8, 10, 11] 
	 * @param other the other LongRange to compare against
	 * @return true if the two Ranges contain common elements with a common step size, false otherwise 
	 */
	public boolean isCoincident(LongRange other)
	{
		
		if (!isOverlapped(other)) return false;
		
		//if their step sizes aren't the same, they don't really overlap
		if (other.step != step) return false;
		
		//if they overlap, with the same step size, but are out of phase, they don't really overlap
		if (other.start % step != start % step) return false;
		
		
		//looks like they overlap
		return true;

	}
	
	/**
	 * Determines if two LongRange are adjacent. LongRanges are considered adjacent if 
	 * <ul>
	 * <li>Their step sizes match</li>
	 * <li>They are in phase</li>
	 * <li>The starting value of one is one step after the stopping value of the other</li>
	 * </ul>
	 * 
	 * @param other the other LongRange to examine
	 * @return true if the two LongRanges are one step apart from each other, with the same step and phase, false otherwise
	 */
	public boolean isAdjacent(LongRange other)
	{
		if (other.step != step) return false;
		if (other.start % step != start % step) return false;
		return (other.start == stop + step || other.stop + step == start);
	}
	
	/**
	 * Equivalent to {@link LongRange#isCoincident(LongRange)} OR {@link LongRange#isAdjacent(LongRange)}
	 * @param other the LongRange to check this LongRange against
	 * @return true if the two ranges are touching, false otherwise
	 */
	public boolean isTouching(LongRange other)
	{
		return isCoincident(other) || isAdjacent(other);
	}
	
	
	/**
	 * Merges two LongRanges for which {@link LongRange#isTouching(LongRange)} returns true. Returns null if the LongRanges do not 
	 * satisfy this requirement.
	 * @param other the other LongRange to merge this Range with
	 * @return a new LongRange representing the union of the elements of both
	 */
	public LongRange merge(LongRange other)
	{
		
		if (! isTouching(other) ) return null;
		
		return new LongRange(Math.min(other.start, start), Math.max(other.stop, stop), step);
		
	}
	
	/**
	 * Returns a LongRangeSet representing this Range with the elements in the other LongRange removed. The LongRanges must satisfy 
	 * {@link LongRange#isOverlapping(LongRange)}. If the Ranges are not overlapping, then the returned LongRangeSet will simply 
	 * represent this LongRange
	 * @param other the LongRange to remove from this Range
	 * @return a LongRangeSet representing elements in this Range which are not in the other LongRange
	 */
	public LongRangeSet difference(LongRange other)
	{
		LongRangeSet result = new LongRangeSet();
		
		if (! isCoincident(other))
		{
			result.addRange(this);
			return result;
		}
		
		//other.start-1 because the range class uses an inclusive upper bound
		//other.stop+1 for similar reasons
		if (this.start < other.start) result.addRange(new LongRange(start, other.start-1, step));
		if (this.stop > other.stop) result.addRange(new LongRange(other.stop+1, stop, step));
		
		return result;
		
	}
	
	
	@Override
	public String toString()
	{
		return "[" + start + ".." + stop + (step == 1 ? "" : ":" + step) + "]";
	}	

	
	/**
	 * Get the start value of this LongRange
	 * @return the lower bound and first element in this LongRange
	 */
	public long getStart() {
		return start;
	}


	/**
	 * Get the stop value of this LongRange
	 * @return the upper bound of this LongRange
	 */
	public long getStop() {
		return stop;
	}


	/**
	 * Get the step size of this LongRange
	 * @return the interval between consecutive elements in this LongRange 
	 */
	public long getStep() {
		return step;
	}
	
}
