package plural.executor;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.executor.filter.implementations.PluralFilterExecutor;
import plural.executor.fold.implementations.PluralFoldExecutor;
import plural.executor.map.implementations.PluralMapExecutor;

public class Plural {

	public static <T1> T1 fold(List<T1> elements, BiFunction<T1, T1, T1> fold)
	{
		return new PluralFoldExecutor<T1>(elements, fold).executeBlocking();
	}

	/**
	 * Due to the nature of the parallelism used, the FnFold<T1, T1> fold operator
	 * must be associative and commutative.  
	 * @param <T1>
	 * @param elements
	 * @param base
	 * @param fold
	 * @return
	 */
	public static <T1> T1 fold(List<T1> elements, T1 base, BiFunction<T1, T1, T1> fold)
	{
		return new PluralFoldExecutor<>(elements, base, fold).executeBlocking();
	}
	
	
	/**
	 * Due to the nature of the parallelism used, the FnFold<T1, T1> fold operator
	 * must be associative and commutative.  
	 * @param <T1>
	 * @param elements
	 * @param base
	 * @param fold
	 * @return
	 */
	public static <T1> T1 fold(List<T1> elements, T1 base, BiFunction<T1, T1, T1> fold, int threads)
	{
		return new PluralFoldExecutor<>(elements, base, fold, threads).executeBlocking();
	}
	
	
	
	public static <T1, T2> List<T2> map(List<T1> elements, Function<T1, T2> map)
	{
		return new PluralMapExecutor<>(elements, map).executeBlocking();
	}
	
	public static <T1, T2> List<T2> map(List<T1> elements, Function<T1, T2> map, int threads)
	{
		return new PluralMapExecutor<>(elements, map, threads).executeBlocking();
	}
	
	
	public static void eachIndex(int size, Consumer<Integer> each)
	{
		new PluralEachIndexExecutor(size, each).executeBlocking();
	}
	
	public static void eachIndex(int size, Consumer<Integer> each, int threads)
	{
		new PluralEachIndexExecutor(size, each, threads).executeBlocking();
	}
	
	
	
	public static <T1> List<T1> filter(List<T1> elements, Predicate<T1> filter)
	{
		return new PluralFilterExecutor<>(elements, filter).executeBlocking();
	}
	
	public static <T1> List<T1> filter(List<T1> elements, Predicate<T1> filter, int threads)
	{
		return new PluralFilterExecutor<>(elements, filter, threads).executeBlocking();
	}
	
	
	/**
	 * Convenience method for {@link Runtime#availableProcessors()}
	 * @return
	 */
	public static int cores()
	{
		return Runtime.getRuntime().availableProcessors();
	}
	
	public static void main(String[] args) {
		
	}
	
}
