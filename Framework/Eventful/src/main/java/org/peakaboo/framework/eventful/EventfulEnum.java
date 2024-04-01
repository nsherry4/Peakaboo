package org.peakaboo.framework.eventful;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EventfulEnum<T extends Enum<T>> implements IEventfulEnum<T>
{

	protected final List<EventfulEnumListener<T>> listeners;
	protected final List<EventfulListener> simpleListeners;
	private Consumer<Runnable> uiThreadRunnerOverride = null;
	
	public EventfulEnum() {
		listeners = new LinkedList<>();
		simpleListeners = new LinkedList<>();
	}
	
	
	@Override
	public synchronized void addListener(EventfulListener l) {
		simpleListeners.add(l);
	}
	
	@Override
	public synchronized void addListener(EventfulEnumListener<T> l) {
		listeners.add(l);
	}



	//Done on the event thread on purpose
	@Override
	public synchronized void removeListener(EventfulListener l) {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulEnum.this) { 
					simpleListeners.remove(l);
			}
		});
	}
	
	//Done on the event thread on purpose
	@Override
	public synchronized void removeListener(final EventfulEnumListener<T> l) {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulEnum.this) { 
					listeners.remove(l);
			}
		});
	}

	//Done on the event thread on purpose
	@Override
	public synchronized void removeAllListeners() {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulEnum.this) { 
					listeners.clear();
			}
		});
	}

	
	@Override
	public void updateListeners(final T message) {

		if (listeners.isEmpty()) return;

		getUIThreadRunner().accept(() -> {
			synchronized(EventfulEnum.this) {
				for (EventfulEnumListener<T> l : listeners) {		
					l.change(message);
				}
				for (EventfulListener l : simpleListeners) {		
					l.change();
				}
			}
		});

	}
	
	private Consumer<Runnable> getUIThreadRunner() {
		if (uiThreadRunnerOverride == null) {
			return EventfulConfig::deliver;
		}
		return uiThreadRunnerOverride;
	}
	
	/**
	 * Sets a thread runner to override {@link EventfulConfig#uiThreadRunner} for
	 * this Eventful object only.
	 */
	public void setUIThreadRunnerOverride(Consumer<Runnable> override) {
		uiThreadRunnerOverride = override;
	}





	
}
