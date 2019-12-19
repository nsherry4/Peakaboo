package org.peakaboo.framework.scratch.list.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Borrows heavily from SciTypes RangeSet
 * @author NAS
 *
 */


public class LongRangeSet implements Serializable
{

	private List<LongRange> ranges;
	
	/**
	 * Create a new RangeSet containing no {@link LongRange}s
	 */
	public LongRangeSet()
	{
		ranges = new ArrayList<>();
	}
	

	/**
	 * Add the given {@link LongRange}
	 * @param range the LongRange to add to this LongRangeSet
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
	 * Add all of the {@link LongRange}s from the given LongRangeSet to this LongRangeSet
	 * @param rangeset the LongRangeSet to add the elements from
	 */
	public void addRangeSet(LongRangeSet rangeset)
	{
		for (LongRange r : rangeset.getRanges())
		{
			addRange(r);
		}
	}
	
	/**
	 * Removes this {@link LongRange} from the LongRangeSet. This does not simply remove the given LongRange object, rather it 
	 * modifies the collection of LongRanges so that none of the elements contained in the given LongRange are contained in this 
	 * LongRangeSet anymore. Eg: [1..20:2,6..30:2].removeRange(2..29:1).toSink() => [1, 30] 
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
	 * Determines if this LongRangeSet is touching the given {@link LongRange}. For a definition of touching, see {@link LongRange#isTouching(LongRange)}
	 * @param other the LongRange to compare
	 * @return true if the given Range is touching this LongRangeSet, false otherwise
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
	 * Determines if this LongRangeSet is touching the given other LongRangeSet. For a definition of touching, see {@link LongRange#isTouching(LongRange)}
	 * @param other the LongRangeSet to compare
	 * @return true if the given LongRangeSet is touching this LongRangeSet, false otherwise
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
	 * Determines if this RangeSet is overlapping the given {@link LongRange}. For a definition of overlapping, see {@link LongRange#isOverlapping(LongRange)}
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
	 * Determines if this LongRangeSet is overlapping the given other LongRangeSet. For a definition of overlapping, see {@link LongRange#isOverlapping(LongRange)}
	 * @param other the LongRangeSet to compare
	 * @return true if the given other LongRangeSet is overlapping this LongRangeSet, false otherwise
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
	 * Get a list of the {@link LongRange}s included in this LongRangeSet
	 * @return a list of {@link LongRange}s making up this LongRangeSet
	 */
	public List<LongRange> getRanges()
	{
		return new ArrayList<>(ranges);
	}
	
}
