package org.peakaboo.framework.plural.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.peakaboo.framework.eventful.EventfulEnum;

public class StreamExecutor<T> extends EventfulEnum<StreamExecutor.Event> implements Predicate<Object>{

	public enum State {
		RUNNING,
		ABORTED,
		COMPLETED,
	}
	
	//Events are like state transitions, rather than states themselves
	public enum Event {
		PROGRESS,
		ABORTED,
		COMPLETED
	}
	
	
	private Thread thread;
	private StreamExecutor<?> next;
	private boolean parallel = true;
	
	private int count = 0;
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
	public synchronized boolean test(Object t) {
		count++;
		if (count % interval == 0) {
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
	
	
	
	public String getName() {
		return name;
	}

	public void abort() {
		if (state == State.RUNNING) {
			state = State.ABORTED;
			updateListeners(Event.ABORTED);
			removeAllListeners();
		}
		
	}
	
	public void complete() {
		
		if (state == State.RUNNING) {
			state = State.COMPLETED;
			updateListeners(Event.COMPLETED);
			removeAllListeners();
		}
	}
	
	
	public State getState() {
		return state;
	}
	
	public int getCount() {
		return count;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Optional<T> getResult() {
		return result;
	}

	public void setResult(T result) {
		if (state == State.RUNNING) {
			this.result = Optional.ofNullable(result);
			complete();
		}

	}

	
	public <S> StreamExecutor<S> then(StreamExecutor<S> next) {
		setNext(next);
		return next;
	}
	
	public void setNext(StreamExecutor<?> next) {
		this.next = next;
	}
	
	public StreamExecutor<?> getNext() {
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
			if (source instanceof Collection<?>) {
				setSize(((Collection<?>) source).size());
			} else {
				int count = 0;
				for (S s : source) {
					count++;
				}
				setSize(count);
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
				if (source instanceof Collection<?>) {
					setSize(((Collection<?>) source).size());
				} else {
					int count = 0;
					for (S s : source) {
						count++;
					}
					setSize(count);
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
