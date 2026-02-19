package org.peakaboo.framework.accent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class Lists {

	private Lists() {}
	
	//////////////////////////////////////////////////////////
	// UNIQUE
	//////////////////////////////////////////////////////////
	public static <T1> List<T1> unique(Iterable<T1> list)
	{

		List<T1> newlist = new ArrayList<>();
		Set<T1> hash = new LinkedHashSet<>();
		
		for (T1 t : list){
			if (!hash.contains(t)) hash.add(t);
		}
		newlist.addAll(hash);
		return newlist;

	}

	
	//////////////////////////////////////////////////////////
	// ZIP
	//////////////////////////////////////////////////////////
	public static <T1, T2, T3> List<T3> zipWith(List<T1> l1, List<T2> l2, BiFunction<T1, T2, T3> f)
	{
		List<T3> target = new ArrayList<>();
		if (l1 == null || l2 == null) return List.of();

		Iterator<T1> t1i = l1.iterator();
		Iterator<T2> t2i = l2.iterator();
		
		while (t1i.hasNext() && t2i.hasNext())
		{
			target.add(f.apply(t1i.next(), t2i.next()));
		}

		return target;
	}
	

}
