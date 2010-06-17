package peakaboo.datatypes.functional;



import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.functional.stock.Functions;



public class Functional
{

	public static <T1, T2> List<T2> map(Iterable<T1> list, Function1<T1, T2> f)
	{

		if (list == null) return null;
		
		List<T2> newlist = DataTypeFactory.<T2> list();

		for (T1 element : list)
		{
			newlist.add(f.f(element));
		}
		return newlist;

	}


	public static <T1, T2> List<T2> map(T1 list[], Function1<T1, T2> f)
	{

		if (list == null) return null;
		
		List<T2> newlist = DataTypeFactory.<T2> list();

		for (T1 element : list)
		{
			newlist.add(f.f(element));
		}
		return newlist;

	}

	public static <T1> void each(T1[] list, Function1<T1, Object> f)
	{

		for (T1 element : list)
		{
			f.f(element);
		}

	}
	

	public static <T1> void each(Iterable<T1> list, Function1<T1, Object> f)
	{

		for (T1 element : list)
		{
			f.f(element);
		}

	}


	public static <T1> List<T1> map_target(List<T1> list, List<T1> target, Function1<T1, T1> f)
	{

		if (list == null) return null;
		
		for (int i = 0; i < list.size(); i++)
		{
			target.set(i, f.f(list.get(i)));
		}
		return list;

	}


	public static <T1, T2> List<T2> map_index(List<T1> list, Function1<Integer, T2> f)
	{
		if (list == null) return null;
		

		List<T2> newlist = DataTypeFactory.<T2> list();

		for (int i = 0; i < list.size(); i++)
		{
			newlist.add(f.f(i));
		}
		return newlist;

	}


	public static <T1, T2> List<T1> map_index_target(List<T1> list, List<T2> target, Function1<Integer, T2> f)
	{

		if (list == null) return null;
		
		target.clear();

		for (int i = 0; i < list.size(); i++)
		{
			target.add(f.f(i));
		}
		return list;

	}


	public static <T1> List<T1> filter(Collection<T1> list, Function1<T1, Boolean> f)
	{
		
		if (list == null) return null;

		List<T1> newlist = DataTypeFactory.<T1> list();

		for (T1 element : list)
		{
			if (f.f(element)) newlist.add(element);
		}
		return newlist;

	}


	public static <T1> List<T1> filter_index(List<T1> list, Function1<Integer, Boolean> f)
	{
		if (list == null) return null;
		
		List<T1> newlist = DataTypeFactory.<T1> list();

		for (int i = 0; i <= list.size(); i++)
		{
			if (f.f(i)) newlist.add(list.get(i));
		}
		return newlist;

	}


	public static <T1, T2> T2 fold(T1 list[], T2 base, Function2<T1, T2, T2> f)
	{
		T2 result = base;
		if (list == null) return base;

		for (T1 elem : list)
		{
			result = f.f(elem, result);
		}

		return result;
	}


	public static <T1> T1 fold(T1 list[], Function2<T1, T1, T1> f)
	{

		if (list == null) return null;

		T1 result = null;
		for (T1 elem : list)
		{
			result = elem;
			break;
		}

		for (T1 elem : list)
		{
			result = f.f(elem, result);
		}

		return result;
	}


	public static <T1, T2> T2 fold(Iterable<T1> list, T2 base, Function2<T1, T2, T2> f)
	{
		T2 result = base;
		if (list == null) return base;

		for (T1 elem : list)
		{
			result = f.f(elem, result);
		}

		return result;
	}


	public static <T1> T1 fold(Iterable<T1> list, Function2<T1, T1, T1> f)
	{

		if (list == null) return null;

		T1 result = null;
		for (T1 elem : list)
		{
			result = elem;
			break;
		}

		for (T1 elem : list)
		{
			result = f.f(elem, result);
		}

		return result;
	}


	public static <T1, T2> T2 foldr(List<T1> list, T2 base, Function2<T1, T2, T2> f)
	{
		T2 result = base;
		if (list == null) return base;

		// order matters for foldr/foldl so we use a counter variable instead of an iterator
		for (int i = 0; i < list.size(); i++)
		{
			result = f.f(list.get(i), result);
		}

		return result;
	}


	public static <T1> T1 foldr(List<T1> list, Function2<T1, T1, T1> f)
	{
		if (list == null) return null;
		if (list.size() == 0) return null;

		T1 result = list.get(0);

		// order matters for foldr/foldl so we use a counter variable instead of an iterator
		for (int i = 1; i < list.size(); i++)
		{
			result = f.f(list.get(i), result);
		}

		return result;
	}


	public static <T1, T2> T2 foldl(List<T1> list, T2 base, Function2<T1, T2, T2> f)
	{
		T2 result = base;

		if (list == null) return base;

		// order matters for foldr/foldl so we use a counter variable instead of an iterator
		for (int i = list.size() - 1; i >= 0; i--)
		{
			result = f.f(list.get(i), result);
		}

		return result;
	}


	public static <T1> T1 foldl(List<T1> list, Function2<T1, T1, T1> f)
	{
		if (list == null) return null;
		if (list.size() == 0) return null;

		T1 result = list.get(list.size() - 1);

		// order matters for foldr/foldl so we use a counter variable instead of an iterator
		for (int i = list.size() - 2; i >= 0; i--)
		{
			result = f.f(list.get(i), result);
		}

		return result;
	}


	public static <T1> boolean include(List<T1> list, T1 item)
	{
		if (item == null) return false;
		if (list == null) return false;
		
		return Functional.foldr(
					Functional.map(list, Functions.equiv(item)), false,
					Functions.or()
				);
	}


	public static <T1> boolean include(List<T1> list, Function1<T1, Boolean> f)
	{
		return foldr(map(list, f), Functions.or());
	}


	public static <T1, T2, T3> List<T3> zipWith(List<T1> l1, List<T2> l2, Function2<T1, T2, T3> f)
	{

		if (l1 == null || l2 == null) return null;
		int maxSize = Math.min(l1.size(), l2.size());

		List<T3> l3 = DataTypeFactory.<T3> list();

		for (int i = 0; i < maxSize; i++)
		{
			l3.add(f.f(l1.get(i), l2.get(i)));
		}

		return l3;

	}
	
	public static <T1, T2, T3> List<T3> zipWith(T1[] l1, T2[] l2, Function2<T1, T2, T3> f)
	{

		if (l1 == null || l2 == null) return null;
		int maxSize = Math.min(l1.length, l2.length);

		List<T3> l3 = DataTypeFactory.<T3> list();

		for (int i = 0; i < maxSize; i++)
		{
			l3.add(f.f(l1[i], l2[i]));
		}

		return l3;

	}


	public static <T1, T2> List<T1> zipWith_inplace(List<T1> l1, List<T2> l2, Function2<T1, T2, T1> f)
	{

		if (l1 == null || l2 == null) return null;
		int maxSize = Math.min(l1.size(), l2.size());

		for (int i = 0; i < maxSize; i++)
		{
			l1.set(i, f.f(l1.get(i), l2.get(i)));
		}

		return l1;

	}


	public static <T1> List<T1> unique(Collection<T1> list)
	{

		List<T1> newlist = DataTypeFactory.<T1> list();

		boolean inlist;
		for (T1 elem : list)
		{
			inlist = false;
			for (T1 newelem : newlist)
			{
				inlist |= elem.equals(newelem);
			}

			if (!inlist) newlist.add(elem);

		}

		return newlist;

	}


	public static <T1> List<T1> unique(Collection<T1> list, Function2<T1, T1, Boolean> f)
	{

		List<T1> newlist = DataTypeFactory.<T1> list();

		boolean inlist;
		for (T1 elem : list)
		{
			inlist = false;
			for (T1 newelem : newlist)
			{
				inlist |= f.f(elem, newelem);
			}

			if (!inlist) newlist.add(elem);

		}

		return newlist;

	}


	public static <T1, T2> void sortBy(List<T1> list, final Comparator<T2> c, final Function1<T1, T2> f)
	{

		Collections.sort(list, new Comparator<T1>() {

			public int compare(T1 o1, T1 o2)
			{
				return c.compare(f.f(o1), f.f(o2));
			}
		});

	}

}
