package org.peakaboo.framework.cyclops;

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
 * 
 * @author Nathaniel Sherry, 2010-2011
 *
 */


public class InclusiveRangeSet implements Serializable, Iterable<Integer>
{

	private List<InclusiveRange> ranges;
	
	/**
	 * Create a new RangeSet containing no {@link InclusiveRange}s
	 */
	public InclusiveRangeSet() {
		ranges = new ArrayList<>();
	}
	
	public InclusiveRangeSet(InclusiveRangeSet copy) {
		this();
		ranges.addAll(copy.ranges);
	}
	

	/**
	 * Add the given {@link InclusiveRange}
	 * @param range the Range to add to this RangeSet
	 */
	public void addRange(InclusiveRange range) {
		Iterator<InclusiveRange> i;
		InclusiveRange r;
		
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
	 * Add all of the {@link InclusiveRange}s from the given RangeSet to this RangeSet
	 * @param rangeset the RangeSet to add the elements from
	 */
	public void addRangeSet(InclusiveRangeSet rangeset) {
		for (InclusiveRange r : rangeset.getRanges()) {
			addRange(r);
		}
	}
	
	/**
	 * Removes this {@link InclusiveRange} from the RangeSet. This does not simply remove the given Range object, rather it 
	 * modifies the collection of Ranges so that none of the elements contained in the given Range are contained in this 
	 * RangeSet anymore. Eg: [1..20:2,6..30:2].removeRange(2..29:1).toSink() => [1, 30] 
	 * @param range the Range the elements of which should be removed from this RangeSet
	 */
	public void removeRange(InclusiveRange range) {
		Iterator<InclusiveRange> i;
		InclusiveRangeSet difference = new InclusiveRangeSet();
		InclusiveRange r;
		
		if (range == null) return;
		
		i = ranges.iterator();
		while (i.hasNext()) {
			r = i.next();
			if (r.isCoincident(range)) {			
				i.remove();
				difference.addRangeSet( r.difference(range) );
			}
		}
		
		addRangeSet(difference);
		
	}
	
	/**
	 * Remove all {@link InclusiveRange}s from this RangeSet
	 */
	public void clear() {
		ranges.clear();
	}
	
	
	public Stream<Integer> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED + Spliterator.IMMUTABLE + Spliterator.NONNULL), false);
	}
	
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			Iterator<InclusiveRange> rangeIterator = ranges.iterator();
			Iterator<Integer> numIterator;
			
			
			public boolean hasNext() {
				while (numIterator == null || !numIterator.hasNext()){
					if (! rangeIterator.hasNext()) return false;
					numIterator = rangeIterator.next().iterator();
				}
				return numIterator.hasNext();
			}

			public Integer next() {
				while (numIterator == null || !numIterator.hasNext()){
					if (! rangeIterator.hasNext()) throw new IndexOutOfBoundsException();
					numIterator = rangeIterator.next().iterator();
				}
				return numIterator.next();				
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
		
	}
	
	public List<Integer> asList() {
		List<Integer> l = new ArrayList<>();
		for (int i : this) {
			l.add(i);
		}
		return l;
	}
	
	/**
	 * Calculates the number of distinct elements in this RangeSet. Note that
	 * because constituent ranges may contain duplicate elements, this method takes
	 * O(n) time.
	 */
	public int size() {
		int size = 0;
		for (int i : this) {
			size++;
		}
		return size;
	}
	
	//TODO: this could be faster
	public boolean contains(int value) {
		for (int i : this) {
			if (i == value) { return true; }
		}
		return false;
	}
	
	/**
	 * Determines if this RangeSet is touching the given {@link InclusiveRange}. For a definition of touching, see {@link InclusiveRange#isTouching(InclusiveRange)}
	 * @param other the Range to compare
	 * @return true if the given Range is touching this RangeSet, false otherwise
	 */
	public boolean isTouching(InclusiveRange other)
	{
		
		for (InclusiveRange r : ranges)
		{		
			if (r.isTouching(other)) return true;
		}
		
		return false;
		
	}
	
	/**
	 * Determines if this RangeSet is touching the given other RangeSet. For a definition of touching, see {@link InclusiveRange#isTouching(InclusiveRange)}
	 * @param other the RangeSet to compare
	 * @return true if the given RangeSet is touching this RangeSet, false otherwise
	 */
	public boolean isTouching(InclusiveRangeSet other)
	{
		
		for (InclusiveRange r : other.ranges)
		{
			if (isTouching(r)) return true;
		}
		
		return false;
	}
	
	/**
	 * Determines if this RangeSet is overlapping the given {@link InclusiveRange}. For a definition of overlapping, see {@link InclusiveRange#isOverlapping(InclusiveRange)}
	 * @param other the Range to compare
	 * @return true if the given Range is overlapping this RangeSet, false otherwise
	 */
	public boolean isCoincident(InclusiveRange other) {
		
		for (InclusiveRange r : ranges) {		
			if (r.isCoincident(other)) return true;
		}
		
		return false;
		
	}
	
	/**
	 * Determines if this RangeSet is overlapping the given other RangeSet. For a definition of overlapping, see {@link InclusiveRange#isOverlapping(InclusiveRange)}
	 * @param other the RangeSet to compare
	 * @return true if the given other RangeSet is overlapping this RangeSet, false otherwise
	 */
	public boolean isCoincident(InclusiveRangeSet other) {
		
		for (InclusiveRange r : other.ranges) {
			if (isCoincident(r)) return true;
		}
		
		return false;
	}
	
	
	@Override
	public String toString() {
		return ranges.toString();
	}

	
	
	/**
	 * Get a list of the {@link InclusiveRange}s included in this RangeSet
	 * @return a list of {@link InclusiveRange}s making up this RangeSet
	 */
	public List<InclusiveRange> getRanges() {
		return new ArrayList<>(ranges);
	}
	
	public static void main(String[] args) {
		
		InclusiveRange r1 = new InclusiveRange(1, 10, 2);
		InclusiveRange r2 = new InclusiveRange(6, 20, 2);
		
		assert(r1.isOverlapped(r2));
		assert(!r1.isCoincident(r2));
		assert(!r1.isAdjacent(r2));
		assert(!r1.isTouching(r2));
		
		System.out.println(r1.stream().collect(Collectors.toList()));
		System.out.println(r2.stream().collect(Collectors.toList()));
		
		
		
		InclusiveRangeSet rs1 = new InclusiveRangeSet();
		rs1.addRange(r1);
		rs1.addRange(r2);
		System.out.println(rs1.stream().collect(Collectors.toList()));
		
		
	}
	
}
