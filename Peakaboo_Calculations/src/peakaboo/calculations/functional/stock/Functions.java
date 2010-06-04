package peakaboo.calculations.functional.stock;



import peakaboo.calculations.functional.Function1;
import peakaboo.calculations.functional.Function2;
import peakaboo.datatypes.Pair;



public class Functions
{

	public static Function2<String, String, String> concat()
	{
		return new Function2<String, String, String>() {

			@Override
			public String f(String s1, String s2)
			{
				return s1.toString() + s2;
			}
		};
	}
	
	public static <T1 extends Object> Function2<T1, String, String> concatObj()
	{
		return new Function2<T1, String, String>() {

			@Override
			public String f(T1 s1, String s2)
			{
				return s1.toString() + s2;
			}
		};
	}
	
	public static Function2<String, String, String> concat(final String separator)
	{
		return new Function2<String, String, String>() {

			@Override
			public String f(String s1, String s2)
			{
				return s1 + separator + s2;
			}
		};
	}


	public static <T1> Function1<T1, Boolean> equiv(final T1 item)
	{
		return new Function1<T1, Boolean>() {

			@Override
			public Boolean f(T1 s1)
			{
				return item.equals(s1);
			}
		};
	}


	public static <T1, T2> Function1<Pair<T1, T2>, T1> first()
	{
		return new Function1<Pair<T1, T2>, T1>() {

			@Override
			public T1 f(Pair<T1, T2> element)
			{
				return element.first;
			}
		};
	}
	
	public static <T1, T2> Function1<Pair<T1, T2>, T2> second()
	{
		return new Function1<Pair<T1, T2>, T2>() {

			@Override
			public T2 f(Pair<T1, T2> element)
			{
				return element.second;
			}
		};
	}
	
	public static <T1> Function1<T1, Boolean> bTrue()
	{
		return new Function1<T1, Boolean>(){

			@Override
			public Boolean f(T1 element)
			{
				return true;
			}
			
		};
	}
	
	public static <T1> Function1<T1, Boolean> bFalse()
	{
		return new Function1<T1, Boolean>(){

			@Override
			public Boolean f(T1 element)
			{
				return false;
			}
			
		};
	}
	
	public static <T1> Function1<T1, T1> id()
	{
		return new Function1<T1, T1>(){

			@Override
			public T1 f(T1 element)
			{
				return element;
			}
			
		};
	}
	
}
