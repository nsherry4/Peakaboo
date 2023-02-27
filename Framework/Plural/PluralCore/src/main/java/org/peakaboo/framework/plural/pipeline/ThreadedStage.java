package org.peakaboo.framework.plural.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class ThreadedStage<S, T> implements Stage<S, T> {

	private ThreadPoolExecutor pool;
	private Function<S, T> function;
	private Stage<T, ?> next;
	
	public ThreadedStage(String name, int threads, Function<S, T> function) {
		this.function = function;
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
		
		
	}

	@Override
	public void accept(S input) {
		this.pool.execute(() -> {
			T output = function.apply(input);
			if (next == null) return;
			next.accept(output);
		});
	}

	@Override
	public <O> Pipeline<S, O> then(Stage<T, O> next) {
		this.next = next;
		return new Pipeline<>(this, next);
	}

	@Override
	public void link(Stage<T, ?> next) {
		this.next = next;
	}

	@Override
	public Stage<T, ?> next() {
		return next;
	}

	@Override
	public void finish() {
		this.pool.shutdown();
		try {
			this.pool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static <S, T> Stage<S, T> of(String name, int threads, Function<S, T> function) {
		return new ThreadedStage<>(name, threads, function);
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
