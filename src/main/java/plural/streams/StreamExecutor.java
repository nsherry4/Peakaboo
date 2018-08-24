package plural.streams;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.swing.JFrame;

import eventful.EventfulEnum;
import plural.streams.swing.StreamExecutorPanel;
import plural.streams.swing.StreamExecutorView;
import swidget.Swidget;

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
			return stream.filter(this);
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
	

	public static void main(String[] args) throws InterruptedException {
		
		Swidget.initialize(() -> {
			int size = 10000;
			
			List<Integer> ints = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				ints.add(i);
			}
			System.out.println("Starting...");
			
			
			StreamExecutor<List<Integer>> e1 = new StreamExecutor<>("s1");
			e1.setSize(size);
			
			e1.addListener((event) -> {
				if (event == Event.PROGRESS) {
					System.out.println("E1 Processed: " + e1.getCount());
				}
				
				if (event == Event.COMPLETED) {
					System.out.println("E1 Done!");
				}
			});
			
			e1.setTask(() -> {
				return e1.observe(ints.stream()).map(v -> {
					float f = v;
					for (int i = 0; i < 100000; i++) {
						f = (int)Math.pow(f, 1.0001);
					}
					return (int)f;
				}).collect(Collectors.toList());
			});
			
	
			
			
			StreamExecutor<List<Integer>> e2 = new StreamExecutor<>("s2");
			e2.setSize(size);
			
			e2.addListener((event) -> {
				if (event == Event.PROGRESS) {
					System.out.println("E2 Processed: " + e2.getCount());
				}
				
				if (event == Event.COMPLETED) {
					System.out.println("E2 Done!");
				}
			});
			
			e2.setTask(() -> {
				return e2.observe(e1.getResult().get().stream()).map(v -> {
					float f = v;
					for (int i = 0; i < 100000; i++) {
						f = (int)Math.pow(f, 1.0001);
					}
					return (int)f;
				}).collect(Collectors.toList());
			});
			
			e1.then(e2);
			
			
			StreamExecutorView v1 = new StreamExecutorView(e1);
			StreamExecutorView v2 = new StreamExecutorView(e2);
			
			JFrame frame = new JFrame();
			StreamExecutorPanel panel = new StreamExecutorPanel("Two Tasks", v1, v2);
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(panel, BorderLayout.CENTER);
	
			e2.addListener((event) -> {
				if (event != Event.PROGRESS) {
					frame.setVisible(false);
				}
			});
			
		
			frame.pack();
			frame.setVisible(true);
			
			e1.start();
		}, "Test");

		

		
		
		Thread.currentThread().sleep(5000);
		
	}
	
}
