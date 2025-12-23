package org.peakaboo.framework.plural.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import org.peakaboo.framework.accent.log.OneLog;

public class ThreadedStage<S, T> extends AbstractStage<S, T> {

	private ThreadPoolExecutor pool;
	
	public ThreadedStage(String name, int threads, Function<S, T> function) {
		super(name, function);
		/*
		 * Gross! This is required because of a limitation of ThreadPoolExecutor where
		 * it cannot block when its input queue is full, only reject the input. It has a
		 * number of rejection policies, but none of them let us block upstream workers
		 * like we're doing here. Note that we must set the core threads and max threads
		 * to be the same value in the ThreadPoolExecutor, or this will fail
		 */
		BlockingQueue<Runnable> poolQueue = new LinkedBlockingQueue<>(50) {
			@Override
			public boolean offer(Runnable r) {
				try {
					this.put(r);
					return true;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				return false;
			};
		};
		this.pool = new ThreadPoolExecutor(threads, threads, 10, TimeUnit.SECONDS, poolQueue);
		this.pool.setThreadFactory(new ThreadFactory() {
			//Create daemon threads with a pretty name
			private int count = 1;
			@Override
			public Thread newThread(Runnable r) {
				var t = new Thread(r, name + "-" + count++);
				t.setDaemon(true);
				return t;
			}
		});
		
		this.setState(State.OPERATING);
		
	}

	@Override
	public void accept(S input) {
		if (waitUntilOperating()) {
			if (getState() == State.QUIESCING) {
				// Jobs shouldn't be received once this stage has been told to begin shutting down
				throw new IllegalStateException("Stage '" + getName() + "' is already quiescing.");
			}
			this.pool.execute(() -> {
				super.accept(input);
			});
		} else {
			if (getState() == State.COMPLETED) {
				// Jobs shouldn't be received after a clean shutdown.
				throw new IllegalStateException("Stage '" + getName() + "' is already closed.");
			}
		}
	}

	@Override
	public boolean finish() {
		if (this.getState() != State.OPERATING && this.getState() != State.STARTING) {
			// Once we've moved past the operating stage, don't accept any new shutdown requests
			return false;
		}
		
		// Set the state to quiescing to keep us from placing any more jobs in the queue
		// for the thread pool
		setState(State.QUIESCING);
		
		// Instruct the thread pool to shut down and then wait for it to finish any jobs
		this.pool.shutdown();
		try {
			this.pool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			OneLog.log(Level.WARNING, "Failed to shut down pipeline threads", e);
		}
		
		// Finally set the state to completed, which will also prevent the superclass's
		// accept method from processing anything out of the thread queue 
		return setState(State.COMPLETED);
	}
	
	@Override
	public boolean abort() {
		boolean result = super.abort();
		this.pool.shutdownNow();
		try {
			this.pool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			OneLog.log(Level.WARNING, "Failed to abort pipeline threads", e);
		}
		return result;
	}

	
	public static <S, T> Stage<S, T> of(String name, int threads, Function<S, T> function) {
		return new ThreadedStage<>(name, threads, function);
	}

	public static <S> Stage<S, S> noop(String name, int threads) {
		return new ThreadedStage<>(name, threads, s -> s);
	}
	
	public static <S> Stage<S, S> visit(String name, int threads, Consumer<S> function) {
		return new ThreadedStage<>(name, threads, s -> {
			function.accept(s);
			return s;
		});
	}
	
	public static <S> Stage<S, Void> sink(String name, int threads, Consumer<S> function) {
		return new ThreadedStage<>(name, threads, s -> {
			function.accept(s);
			return null;
		});
	}
	
}
