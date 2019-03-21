package cyclops.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class ListOps {

	public static <T1, T2> List<T2> concatMap(final List<T1> list, Function<T1, List<T2>> f)
	{
		return list.stream().flatMap(a -> f.apply(a).stream()).collect(toList());
	}
	
	//////////////////////////////////////////////////////////
	// GROUP
	//////////////////////////////////////////////////////////
	
	public static <T1> List<List<T1>> group(final Iterable<T1> list)
	{
		return groupBy(list, (a, b) -> a.equals(b));
	}
	
	public static <T1> List<List<T1>> groupBy(final Iterable<T1> list, final BiFunction<T1, T1, Boolean> f)
	{

		//function f determines if two elements belong in the same group;
		//use that function to create a 'unique' list where there is only
		//one element from each group remaining
		List<T1> uniques = uniqueBy(list, f);

		//map the list into a list of lists
		return uniques.stream().map(o1 -> {
			return StreamSupport.stream(list.spliterator(), false).filter(o2 -> f.apply(o1,  o2)).collect(toList());
		}).collect(toList());
		//return map(uniques, o1 -> filter(list, o2 -> f.apply(o1, o2)));

	}
	
	
	//////////////////////////////////////////////////////////
	// UNIQUE
	//////////////////////////////////////////////////////////
	public static <T1> List<T1> unique(Iterable<T1> list)
	{

		List<T1> newlist = new ArrayList<>();
		Set<T1> hash = new LinkedHashSet<T1>();
		
		for (T1 t : list){
			if (!hash.contains(t)) hash.add(t);
		}
		newlist.addAll(hash);
		return newlist;

	}


	public static <T1> List<T1> uniqueBy(Iterable<T1> list, BiFunction<T1, T1, Boolean> f)
	{
		List<T1> newlist = new ArrayList<>();
		boolean inlist;
		for (T1 elem : list)
		{
			inlist = false;
			for (T1 newelem : newlist)
			{
				inlist |= f.apply(elem, newelem);
				if (inlist) break;
			}

			if (!inlist) newlist.add(elem);

		}

		return newlist;
	}
	
	
	//////////////////////////////////////////////////////////
	// ZIP
	//////////////////////////////////////////////////////////
	public static <T1, T2, T3> List<T3> zipWith(List<T1> l1, List<T2> l2, BiFunction<T1, T2, T3> f)
	{
		List<T3> target = new ArrayList<>();
		if (l1 == null || l2 == null) return null;

		Iterator<T1> t1i = l1.iterator();
		Iterator<T2> t2i = l2.iterator();
		
		while (t1i.hasNext() && t2i.hasNext())
		{
			target.add(f.apply(t1i.next(), t2i.next()));
		}

		return target;
	}
	
	//////////////////////////////////////////////////////////
	// PERMUTATIONS
	//////////////////////////////////////////////////////////
	//thanks https://codeforces.com/blog/entry/3980
	public static <T extends Comparable<T>> Iterable<List<T>> permutations(List<T> items) {
		
		
		
		return new Iterable<List<T>>() {

			@Override
			public Iterator<List<T>> iterator() {
				
				return new Iterator<List<T>>() {

					List<T> copy = new ArrayList<>(items);
					List<T> next = new ArrayList<>(copy);
					
					@Override
					public boolean hasNext() {
						return next != null;
					}

					@Override
					public List<T> next() {
						List<T> current = next;
						List<T> newNext = nextPermutation(copy);
						if (newNext != null) {
							next = new ArrayList<>(newNext);
						} else {
							next = null;
						}
						return current;
					}
					
					private List<T> nextPermutation( final List<T> c ) {
						// 1. finds the largest k, that c[k] < c[k+1]
						int first = getFirst( c );
						if ( first == -1 ) return null; // no greater permutation
						// 2. find last index toSwap, that c[k] < c[toSwap]
						int toSwap = c.size() - 1;
						while ( c.get(first).compareTo( c.get(toSwap) ) >= 0 )
							--toSwap;
						// 3. swap elements with indexes first and last
						swap( c, first++, toSwap );
						// 4. reverse sequence from k+1 to n (inclusive) 
						toSwap = c.size() - 1;
						while ( first < toSwap )
							swap( c, first++, toSwap-- );
						return c;
					}
					
					// finds the largest k, that c[k] < c[k+1]
					// if no such k exists (there is not greater permutation), return -1
					private int getFirst( final List<T> list ) {
						for ( int i = list.size() - 2; i >= 0; --i ) {
							if ( list.get(i).compareTo( list.get(i+1)) < 0 ) {
								return i;
							}
						}
						return -1;
					}
					
					// swaps two elements (with indexes i and j) in array 
					private void swap( final List<T> c, final int i, final int j ) {
						final T tmp = c.get(i);
						c.set(i, c.get(j));
						c.set(j,  tmp);
					}
				};
			}};
		
		
		
	}
	
	
}
