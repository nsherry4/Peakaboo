package org.peakaboo.framework.plural.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.peakaboo.framework.eventful.EventfulEnum;
import org.peakaboo.framework.plural.monitor.TaskMonitor;

/**
 * A StreamExecutor captures a {@link Stream} and any surrounding context needed
 * to execute it. After construction, call the
 * {@link StreamExecutor#setTask(Iterable, Function)} method (or some comparable
 * alternative). Here you will pass an iterable and a {@link Function} which
 * accepts a stream as input and returns the result of the executor's task. This
 * stream is based on the iterable provided, preloaded with stream elements for
 * metrics and execution control. <br/>
 * <br/>
 * When the executor is run, the provided task function will be executed. It is
 * the responsibility of the task function to take the stream and execute it how
 * it sees fit. This allows for any setup and teardown work that may need to be
 * performed in the same function (or off the UI thread) <br/>
 * <br/>
 * Like the {@link Thread} class, a StreamExecutor implements both a run and start method. To run the task on the current thread and block until the result is available, call the run method. To run the task in a separate thread asynchronously, call the start method. <br/>
 * <br/>
 * To keep track of the execution, an ExecutorSet will propagate {@link Event}s to listeners. These events are generated when the progress counter is updated, the task is aborted, or the task is completed<br/>
 * <br/>
 * Contrived Example: <br/>
 * <pre>
 * {@code
 * var words = List.of("Hello", "World", "Here", "Are", "Some", "Words");
 * var bestWordFinder = new StreamExecutor<String>("Scoring Words");
 * 
 * bestWordFinder.setTask(words, wordStream -> {
 * 
 *   // Long time to construct, don't do this on the UI thread
 *   var scorer = new WordScorer();
 *   
 *   // Calculate the scores for all words (may be in parallel)
 *   // Assume scoring is long-running and benefits from parallelism
 *   List<String> scores = wordStream.map(word -> scorer.score(word)).toList();
 * 
 *   
 *   // Use a magic function to get the best word and keep this example short
 *   return findBestWordByScore(words, scores);
 *   
 * });
 * 
 * </pre>
 */

public class StreamExecutor<T> extends EventfulEnum<TaskMonitor.Event> implements Predicate<Object>, TaskMonitor<T>{

	private Thread thread;
	private StreamExecutor<?> next;
	private boolean parallel = true;
	
	private AtomicInteger count = new AtomicInteger();
	private int size = -1;
	private int interval = 100;
	Optional<T> result = Optional.empty();
	
	private State state = State.RUNNING;
	private String name;
	
	public StreamExecutor(String name) {
		this(name, 100);
	}
	
	public StreamExecutor(String name, int notificationInterval) {
		this.name = name;
		this.interval = notificationInterval;
	}
	
	
	
	
	public boolean isParallel() {
		return parallel;
	}

	public void setParallel(boolean parallel) {
		this.parallel = parallel;
	}

	@Override
	public boolean test(Object t) {
		if (count.incrementAndGet() % interval == 0) {
			updateListeners(Event.PROGRESS);
		}
		return state == State.RUNNING;
	}
	

	public <S> Stream<S> observe(Stream<S> stream) {
		if (parallel) {
			return stream.parallel().filter(this);
		} else {
			return stream.sequential().filter(this);
		}
	}
	
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void abort() {
		if (state == State.RUNNING) {
			state = State.ABORTED;
			updateListeners(Event.ABORTED);
			removeAllListeners();
		}
		
	}
	
	@Override
	public void complete() {
		
		if (state == State.RUNNING) {
			state = State.COMPLETED;
			updateListeners(Event.COMPLETED);
			removeAllListeners();
		}
	}
	
	@Override
	public State getState() {
		return state;
	}
	
	@Override
	public int getCount() {
		return count.get();
	}

	@Override
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public Optional<T> getResult() {
		return result;
	}

	private void setResult(T result) {
		if (state == State.RUNNING) {
			this.result = Optional.ofNullable(result);
			complete();
		}
	}

	
	public <S> TaskMonitor<S> then(StreamExecutor<S> next) {
		setNext(next);
		return next;
	}
	
	public void setNext(StreamExecutor<?> next) {
		this.next = next;
	}
	
	public TaskMonitor<?> getNext() {
		return this.next;
	}
	
	
	/**
	 * Sets the execution task to the given {@link Supplier} function. The stream used 
	 * by this function will have to be created manually and wrapped with the 
	 * {@link StreamExecutor#observe(Stream)} function. The size will also have to be
	 * set manually by calling {@link StreamExecutor#setSize(int)}
	 * @param task
	 */
	public void setTask(Supplier<T> task) {
		thread = new Thread(() -> {
			setResult(task.get());
			
			//If another StreamExecutor is specified to run after this is done, kick it off now
			if (this.next != null && state == State.COMPLETED) {
				next.start();
			}
		});		
	}
	
	/**
	 * Sets the execution task to the given {@link Function}. The stream to be used
	 * by this function will be supplied as the function's argument, constructed from 
	 * the {@link Iterable} source. 
	 * @param source The {@link Iterable} to stream over
	 * @param task The task to perform over the iterable's stream
	 */
	public <S> void setTask(Iterable<S> source, Function<Stream<S>, T> task) {
		
		//If size hasn't already been manually set, figure it out now
		if (size == -1) {
			if (source instanceof Collection<?> c) {
				setSize(c.size());
			} else {
				int itemCount = 0;
				for (S s : source) {
					itemCount++;
				}
				setSize(itemCount);
			}
		}
		
		setTask(() -> source, task);
	}
	
	/**
	 * Sets the execution task to the given {@link Function}. The stream to be used
	 * by this function will be supplied as the function's argument, constructed from 
	 * the {@link Iterable} source. 
	 * @param source A function to lazily provide the {@link Iterable} to stream over
	 * @param task The task to perform over the iterable's stream
	 */
	public <S> void setTask(Supplier<? extends Iterable<S>> sourceProvider, Function<Stream<S>, T> task) {

		thread = new Thread(() -> {
			
			Iterable<S> source = sourceProvider.get();
			
			//If size hasn't already been manually set, figure it out now
			if (size == -1) {
				if (source instanceof Collection<?> c) {
					setSize(c.size());
				} else {
					int itemCount = 0;
					for (S s : source) {
						itemCount++;
					}
					setSize(itemCount);
				}
			}
			
			setResult(
				task.apply(
					observe(
						StreamSupport.stream(source.spliterator(), true)
					)
				)
			);
			
			//If another StreamExecutor is specified to run after this is done, kick it off now
			if (this.next != null && state == State.COMPLETED) {
				next.start();
			}
		});		
	}

	/**
	 * Convenience method for {@link StreamExecutor#setTask(Iterable, Function)} to accept arrays as input
	 */
	public <S> void setTask(S[] source, Function<Stream<S>, T> task) {
		setTask(Arrays.asList(source), task);
	}
	
	/**
	 * Starts this task in a worker thread and returns asynchronously
	 */
	@Override
	public void start() {
		thread.start();
	}
	
	/**
	 * Runs this task in the current thread, blocks until finished, and returns the result directly
	 * @return
	 */
	public Optional<T> run() {
		thread.run();
		return result;
	}
	
	
}
