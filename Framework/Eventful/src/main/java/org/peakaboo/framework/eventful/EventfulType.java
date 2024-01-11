package org.peakaboo.framework.eventful;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EventfulType<T> implements IEventfulType<T>
{

	protected final List<EventfulTypeListener<T>> listeners;
	protected final List<EventfulListener> simpleListeners;
	private Consumer<Runnable> uiThreadRunnerOverride = null;
	
	public EventfulType() {
		listeners = new LinkedList<EventfulTypeListener<T>>();
		simpleListeners = new LinkedList<EventfulListener>();
				
	}

	@Override
	public void addListener(EventfulListener l) {
		simpleListeners.add(l);
	}
	
	@Override
	public synchronized void addListener(final EventfulTypeListener<T> l) {
		listeners.add(l);
	}

	
	//Done on the event thread on purpose
	@Override
	public void removeListener(EventfulListener l) {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulType.this) { 
				simpleListeners.remove(l);
			}
		});
	}
	
	//Done on the event thread on purpose
	@Override
	public synchronized void removeListener(final EventfulTypeListener<T> l) {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulType.this) { 
				listeners.remove(l);
			}
		});
	}
	

	//Done on the event thread on purpose
	@Override
	public synchronized void removeAllListeners() {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulType.this) { 
				listeners.clear();
			}
		});
	}


	@Override
	public void updateListeners(final T message) {

		if (listeners.size() == 0) return;

		getUIThreadRunner().accept(() -> {
			synchronized(EventfulType.this){
				for (EventfulTypeListener<T> l : listeners) {
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
