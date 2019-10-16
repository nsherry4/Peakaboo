package org.peakaboo.framework.plural.monitor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.peakaboo.framework.eventful.EventfulEnum;

public class SimpleTaskMonitor<T> extends EventfulEnum<TaskMonitor.Event> implements TaskMonitor<T> {

	private String name;
	private Supplier<T> supplier;
	private Consumer<Optional<T>> callback;
	private Optional<T> result = Optional.empty();
	private int percent;
	private State state = State.RUNNING;
	
	private Thread thread;
	

	public SimpleTaskMonitor(String name, Supplier<T> supplier) {
		this(name, supplier, null);
	}
	
	public SimpleTaskMonitor(String name, Supplier<T> supplier, Consumer<Optional<T>> callback) {
		this.supplier = supplier;
		this.name = name;
		this.callback = callback;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public State getState() {
		return state;
	}
	
	@Override
	public void abort() {
		state = State.ABORTED;
		updateListeners(Event.ABORTED);
		removeAllListeners();
		if (callback != null) {
			callback.accept(Optional.empty());
		}
		thread.interrupt();
	}

	@Override
	public void complete() {
		state = State.COMPLETED;
		updateListeners(Event.COMPLETED);
		removeAllListeners();
		if (callback != null) {
			callback.accept(result);
		}
	}

	@Override
	public Optional<T> getResult() {
		return result;
	}

	@Override
	public int getCount() {
		return percent;
	}

	@Override
	public int getSize() {
		return 100; // percent
	}
	
	public void setPercent(float percent) {
		float oldpercent = this.percent;
		this.percent = (int)(percent*100);
		if (this.percent != oldpercent) {
			updateListeners(Event.PROGRESS);
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		Runnable job = () -> {
			Optional<T> fnresult = Optional.ofNullable(supplier.get());
			if (state == State.ABORTED) {
				return;
			}
			result = fnresult;
			complete();
		};
		thread = new Thread(job);
		thread.start();
	}



}
