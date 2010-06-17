package peakaboo.datatypes.functional.stock;



import java.util.List;

import peakaboo.datatypes.Pair;
import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Function2;



public class Functions
{

	public static Function2<String, String, String> concat()
	{
		return new Function2<String, String, String>() {

			public String f(String s1, String s2)
			{
				return s1.toString() + s2;
			}
		};
	}


	public static <T1 extends Object> Function2<T1, String, String> concatObj()
	{
		return new Function2<T1, String, String>() {

			public String f(T1 s1, String s2)
			{
				return s1.toString() + s2;
			}
		};
	}


	public static Function2<String, String, String> concat(final String separator)
	{
		return new Function2<String, String, String>() {

			public String f(String s1, String s2)
			{
				return s1 + separator + s2;
			}
		};
	}


	public static <T1> Function1<T1, Boolean> equiv(final T1 item)
	{
		return new Function1<T1, Boolean>() {

			public Boolean f(T1 s1)
			{
				return item.equals(s1);
			}
		};
	}


	public static <T1, T2> Function1<Pair<T1, T2>, T1> first()
	{
		return new Function1<Pair<T1, T2>, T1>() {

			public T1 f(Pair<T1, T2> element)
			{
				return element.first;
			}
		};
	}


	public static <T1, T2> Function1<Pair<T1, T2>, T2> second()
	{
		return new Function1<Pair<T1, T2>, T2>() {

			public T2 f(Pair<T1, T2> element)
			{
				return element.second;
			}
		};
	}


	public static <T1> Function1<T1, Boolean> bTrue()
	{
		return new Function1<T1, Boolean>() {

			public Boolean f(T1 element)
			{
				return true;
			}

		};
	}


	public static <T1> Function1<T1, Boolean> bFalse()
	{
		return new Function1<T1, Boolean>() {

			public Boolean f(T1 element)
			{
				return false;
			}

		};
	}


	public static <T1> Function1<T1, T1> id()
	{
		return new Function1<T1, T1>() {

			public T1 f(T1 element)
			{
				return element;
			}

		};
	}


	public static <T1> Function1<T1, Boolean> notNull()
	{
		return new Function1<T1, Boolean>() {

			public Boolean f(T1 element)
			{
				return element != null;
			}

		};
	}


	public static <T1> Function2<List<T1>, List<T1>, List<T1>> listConcat()
	{

		return new Function2<List<T1>, List<T1>, List<T1>>() {

			public List<T1> f(List<T1> l1, List<T1> l2)
			{
				l1.addAll(l2);
				return l1;
			}

		};

	}


	public static <T1> Function2<T1, T1, Boolean> equiv()
	{
		return new Function2<T1, T1, Boolean>() {

			public Boolean f(T1 o1, T1 o2)
			{
				return o1.equals(o2);
			}
		};
	}
	
	public static Function2<Boolean, Boolean, Boolean> and()
	{
		return new Function2<Boolean, Boolean, Boolean>() {

			public Boolean f(Boolean b1, Boolean b2)
			{
				return b1 && b2;
			}};
	}
	
	public static Function2<Boolean, Boolean, Boolean> or()
	{
		return new Function2<Boolean, Boolean, Boolean>() {

			public Boolean f(Boolean b1, Boolean b2)
			{
				return b1 || b2;
			}};
	}

}
