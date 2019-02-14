package plural;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

import plural.executor.AbstractExecutor;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.executor.filter.implementations.PluralFilterExecutor;
import plural.executor.fold.implementations.PluralFoldExecutor;
import plural.executor.map.implementations.PluralMapExecutor;

public class Plural {

	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}
	
	/**
	 * Builds a simple ExecutorSet with a single DummyExecutor to track work
	 * provided by a function.
	 */
	public static <T> ExecutorSet<T> build(String title, String taskname, BiFunction<ExecutorSet<T>, DummyExecutor, T> function) {
		
		DummyExecutor task = new DummyExecutor();
		task.setName(taskname);
		
		ExecutorSet<T> execset = new ExecutorSet<T>(title) {

			@Override
			protected T execute() {
				
				task.advanceState();
				task.setExecutorSet(this);
				T result = function.apply(this, task);
				task.advanceState();
				return result;
				
			}
		};
		
		execset.addExecutor(task);
		
		return execset;
		
	}
	
	/**
	 * Builds a simple ExecutorSet with a single AbstractExecutor to be executed as
	 * part of a provided work function.
	 */
	public static <T> ExecutorSet<T> build(String title, AbstractExecutor<T> task, BiFunction<ExecutorSet<T>, AbstractExecutor<T>, T> function) {
		ExecutorSet<T> execset = new ExecutorSet<T>(title) {

			@Override
			protected T execute() {
				
				task.setExecutorSet(this);
				T result = function.apply(this, task);
				return result;
				
			}
		};
		
		execset.addExecutor(task);
		
		return execset;
	}
	
	/**
	 * Builds a simple ExecutorSet with a single AbstractExecutor to be executed
	 * automatically.
	 */
	public static <T> ExecutorSet<T> build(String title, AbstractExecutor<T> task) {
		return build(title, task, ()->{}, result->result);
	}
	
	
	/**
	 * Builds a simple ExecutorSet with a single AbstractExecutor to be executed
	 * automatically, along with an optional pre and post callbacks. The post
	 * function is also capable of transforming the output if the Executor does not
	 * leave it in a finished state
	 */
	public static <T, S> ExecutorSet<T> build(String title, AbstractExecutor<S> task, Runnable pre, Function<S, T> post) {
		
		ExecutorSet<T> execset = new ExecutorSet<T>(title) {

			@Override
			protected T execute() {
				
				if (pre != null) pre.run();
				task.setExecutorSet(this);
				S partial = task.executeBlocking();
				T result = post.apply(partial);
				return result;
				
			}
		};
		
		execset.addExecutor(task);
		
		return execset;
		
	}
	
	
	
	
	

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
	
}
