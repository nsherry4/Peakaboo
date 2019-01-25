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
	 * Builds a simple ExecutorSet with a single DummyExecutor to performn work provided by a function.
	 */
	public static <T> ExecutorSet<T> build(String title, String taskname, BiFunction<ExecutorSet<T>, AbstractExecutor, T> function) {
		
		DummyExecutor task = new DummyExecutor();
		task.setName(taskname);
		
		ExecutorSet<T> execset = new ExecutorSet<T>(title) {

			@Override
			protected T execute() {
				
				task.advanceState();
				T result = function.apply(this, task);
				task.advanceState();
				return result;
				
			}
		};
		
		execset.addExecutor(task);
		
		return execset;
		
	}
	
}
