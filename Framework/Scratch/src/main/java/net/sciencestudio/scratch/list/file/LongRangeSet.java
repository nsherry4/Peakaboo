package net.sciencestudio.scratch.list.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Borrows heavily from SciTypes RangeSet
 * @author NAS
 *
 */


public class LongRangeSet implements Serializable
{

	private List<LongRange> ranges;
	
	/**
	 * Create a new RangeSet containing no {@link Range}s
	 */
	public LongRangeSet()
	{
		ranges = new ArrayList<>();
	}
	

	/**
	 * Add the given {@link Range}
	 * @param range the Range to add to this RangeSet
	 */
	public void addRange(LongRange range)
	{
		Iterator<LongRange> i;
		LongRange r;
		
		if (range == null) return;
		
		
		i = ranges.iterator();
		
		while (i.hasNext())
		{
			r = i.next();
			if (r.isTouching(range))
			{			
				i.remove();
				range = range.merge(r);
			}
		}
		
		ranges.add(range);		
		
	}
		

	/**
	 * Add all of the {@link Range}s from the given RangeSet to this RangeSet
	 * @param rangeset the RangeSet to add the elements from
	 */
	public void addRangeSet(LongRangeSet rangeset)
	{
		for (LongRange r : rangeset.getRanges())
		{
			addRange(r);
		}
	}
	
	/**
	 * Removes this {@link LongRange} from the RangeSet. This does not simply remove the given Range object, rather it 
	 * modifies the collection of Ranges so that none of the elements contained in the given Range are contained in this 
	 * RangeSet anymore. Eg: [1..20:2,6..30:2].removeRange(2..29:1).toSink() => [1, 30] 
	 * @param range the Range the elements of which should be removed from this RangeSet
	 */
	public void removeRange(LongRange range)
	{
		Iterator<LongRange> i;
		LongRangeSet difference = new LongRangeSet();
		LongRange r;
		
		if (range == null) return;
		
		
		i = ranges.iterator();
		
		while (i.hasNext())
		{
			r = i.next();
			if (r.isCoincident(range))
			{			
				i.remove();
				difference.addRangeSet( r.difference(range) );
				
			}
		}
		
		addRangeSet(difference);
		
	}
	
	/**
	 * Remove all {@link LongRange}s from this RangeSet
	 */
	public void clear()
	{
		ranges.clear();
	}
	

	
	/**
	 * Determines if this RangeSet is touching the given {@link Range}. For a definition of touching, see {@link Range#isTouching(Range)}
	 * @param other the Range to compare
	 * @return true if the given Range is touching this RangeSet, false otherwise
	 */
	public boolean isTouching(LongRange other)
	{
		
		for (LongRange r : ranges)
		{		
			if (r.isTouching(other)) return true;
		}
		
		return false;
		
	}
	
	/**
	 * Determines if this RangeSet is touching the given other RangeSet. For a definition of touching, see {@link Range#isTouching(Range)}
	 * @param other the RangeSet to compare
	 * @return true if the given RangeSet is touching this RangeSet, false otherwise
	 */
	public boolean isTouching(LongRangeSet other)
	{
		
		for (LongRange r : other.ranges)
		{
			if (isTouching(r)) return true;
		}
		
		return false;
	}
	
	/**
	 * Determines if this RangeSet is overlapping the given {@link Range}. For a definition of overlapping, see {@link Range#isOverlapping(Range)}
	 * @param other the Range to compare
	 * @return true if the given Range is overlapping this RangeSet, false otherwise
	 */
	public boolean isCoincident(LongRange other)
	{
		
		for (LongRange r : ranges)
		{		
			if (r.isCoincident(other)) return true;
		}
		
		return false;
		
	}
	
	/**
	 * Determines if this RangeSet is overlapping the given other RangeSet. For a definition of overlapping, see {@link Range#isOverlapping(Range)}
	 * @param other the RangeSet to compare
	 * @return true if the given other RangeSet is overlapping this RangeSet, false otherwise
	 */
	public boolean isCoincident(LongRangeSet other)
	{
		
		for (LongRange r : other.ranges)
		{
			if (isCoincident(r)) return true;
		}
		
		return false;
	}
	
	
	@Override
	public String toString()
	{
		return ranges.toString();
	}

	
	
	/**
	 * Get a list of the {@link Range}s included in this RangeSet
	 * @return a list of {@link Range}s making up this RangeSet
	 */
	public List<LongRange> getRanges()
	{
		return new ArrayList<>(ranges);
	}
	
}
