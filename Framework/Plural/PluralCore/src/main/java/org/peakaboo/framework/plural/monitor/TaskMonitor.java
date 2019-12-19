package org.peakaboo.framework.plural.monitor;

import java.util.Optional;

import org.peakaboo.framework.eventful.IEventfulEnum;

public interface TaskMonitor<T> extends IEventfulEnum<TaskMonitor.Event> {

	public enum State {
		RUNNING,
		ABORTED,
		COMPLETED,
	}
	
	//Events are like state transitions, rather than states themselves
	public enum Event {
		PROGRESS, //fired when progress tracker has changed
		ABORTED, //fired when task aborted
		COMPLETED //fired when task completed
	}
	
	String getName();

	void abort();

	void complete();

	Optional<T> getResult();

	State getState();
	
	int getCount();

	int getSize();
	
	default float getPercent() {
		float count = getCount();
		float size = getSize();
		return Math.min(1f, Math.max(0f, count / size));
	}
	
	void start();

}