package plural;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import plural.executor.AbstractExecutor;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;

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
	
}
