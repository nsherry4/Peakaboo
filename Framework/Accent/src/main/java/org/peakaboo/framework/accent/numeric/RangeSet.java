package org.peakaboo.framework.accent.numeric;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RangeSet implements Serializable, Iterable<Integer> {

	private List<Range> ranges;
	
	/**
	 * Create a new RangeSet containing no {@link Range}s
	 */
	public RangeSet() {
		ranges = new ArrayList<>();
	}
	
	public RangeSet(RangeSet copy) {
		this();
		ranges.addAll(copy.ranges);
	}
	

	/**
	 * Add the given {@link Range}
	 * @param range the Range to add to this RangeSet
	 */
	public void addRange(Range range) {
		Iterator<Range> i;
		Range r;
		
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
	public void addRangeSet(RangeSet rangeset) {
		for (var r : rangeset.getRanges()) {
			addRange(r);
		}
	}
	
	/**
	 * Removes this {@link Range} from the RangeSet. This does not simply remove the given Range object, rather it 
	 * modifies the collection of Ranges so that none of the elements contained in the given Range are contained in this 
	 * RangeSet anymore. Eg: [1..20:2,6..30:2].removeRange(2..29:1).toSink() => [1, 30] 
	 * @param range the Range the elements of which should be removed from this RangeSet
	 */
	public void removeRange(Range range) {
		Iterator<Range> i;
		RangeSet difference = new RangeSet();
		Range r;
		
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
	 * Remove all {@link Range}s from this RangeSet
	 */
	public void clear() {
		ranges.clear();
	}
	
	
	public Stream<Integer> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED + Spliterator.IMMUTABLE + Spliterator.NONNULL), false);
	}
	
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			Iterator<Range> rangeIterator = ranges.iterator();
			Iterator<Integer> numIterator;
			
			@Override
			public boolean hasNext() {
				while (numIterator == null || !numIterator.hasNext()){
					if (! rangeIterator.hasNext()) return false;
					numIterator = rangeIterator.next().iterator();
				}
				return numIterator.hasNext();
			}

			@Override
			public Integer next() {
				while (numIterator == null || !numIterator.hasNext()){
					if (! rangeIterator.hasNext()) throw new IndexOutOfBoundsException();
					numIterator = rangeIterator.next().iterator();
				}
				return numIterator.next();				
			}

			@Override
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
	 * Determines if this RangeSet is touching the given {@link Range}. For a definition of touching, see {@link Range#isTouching(Range)}
	 * @param other the Range to compare
	 * @return true if the given Range is touching this RangeSet, false otherwise
	 */
	public boolean isTouching(Range other) {
		for (var r : ranges) {		
			if (r.isTouching(other)) return true;
		}
		return false;		
	}
	
	/**
	 * Determines if this RangeSet is touching the given other RangeSet. For a definition of touching, see {@link Range#isTouching(Range)}
	 * @param other the RangeSet to compare
	 * @return true if the given RangeSet is touching this RangeSet, false otherwise
	 */
	public boolean isTouching(RangeSet other) {
		for (var r : other.ranges) {
			if (isTouching(r)) return true;
		}
		return false;
	}
	
	/**
	 * Determines if this RangeSet is overlapping the given {@link Range}. For a definition of overlapping, see {@link Range#isOverlapping(Range)}
	 * @param other the Range to compare
	 * @return true if the given Range is overlapping this RangeSet, false otherwise
	 */
	public boolean isCoincident(Range other) {
		for (var r : ranges) {		
			if (r.isCoincident(other)) return true;
		}
		return false;
	}
	
	/**
	 * Determines if this RangeSet is overlapping the given other RangeSet. For a definition of overlapping, see {@link Range#isOverlapping(Range)}
	 * @param other the RangeSet to compare
	 * @return true if the given other RangeSet is overlapping this RangeSet, false otherwise
	 */
	public boolean isCoincident(RangeSet other) {
		for (var r : other.ranges) {
			if (isCoincident(r)) return true;
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		return ranges.toString();
	}

	
	
	/**
	 * Get a list of the {@link Range}s included in this RangeSet
	 * @return a list of {@link Range}s making up this RangeSet
	 */
	public List<Range> getRanges() {
		return new ArrayList<>(ranges);
	}
	
	
	
}
