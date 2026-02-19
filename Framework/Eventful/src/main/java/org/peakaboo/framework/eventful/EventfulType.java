package org.peakaboo.framework.eventful;

import org.peakaboo.framework.accent.log.OneLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class EventfulType<T> implements IEventfulType<T>
{

	protected final List<EventfulTypeListener<T>> listeners;
	protected final List<EventfulListener> simpleListeners;
	private Consumer<Runnable> uiThreadRunnerOverride = null;
	
	public EventfulType() {
		listeners = new LinkedList<>();
		simpleListeners = new LinkedList<>();
				
	}

	@Override
	public synchronized void addListener(EventfulListener l) {
		simpleListeners.add(l);
	}
	
	@Override
	public synchronized void addListener(final EventfulTypeListener<T> l) {
		listeners.add(l);
	}


	// Done on the event thread on purpose to ensure listeners receive all
	// events queued before removal (UI thread queue provides ordering guarantee)
	@Override
	public void removeListener(EventfulListener l) {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulType.this) { 
				simpleListeners.remove(l);
			}
		});
	}

	// Done on the event thread on purpose to ensure listeners receive all
	// events queued before removal (UI thread queue provides ordering guarantee)
	@Override
	public void removeListener(final EventfulTypeListener<T> l) {
		getUIThreadRunner().accept(() -> { 
			synchronized(EventfulType.this) { 
				listeners.remove(l);
			}
		});
	}


	// Done on the event thread on purpose to ensure listeners receive all
	// events queued before removal (UI thread queue provides ordering guarantee)
	@Override
	public void removeAllListeners() {
		getUIThreadRunner().accept(() -> {
			synchronized(EventfulType.this) {
				listeners.clear();
				simpleListeners.clear();
			}
		});
	}


	@Override
	public void updateListeners(final T message) {
		List<EventfulTypeListener<T>> listenersCopy;
		List<EventfulListener> simpleListenersCopy;

		synchronized(this) {
			if (listeners.isEmpty() && simpleListeners.isEmpty()) return;
			listenersCopy = new ArrayList<>(listeners);
			simpleListenersCopy = new ArrayList<>(simpleListeners);
		}

		getUIThreadRunner().accept(() -> {
			for (EventfulTypeListener<T> l : listenersCopy) {
				try {
					l.change(message);
				} catch (Exception e) {
					OneLog.log(Level.WARNING, "Exception in EventfulTypeListener", e);
				}
			}
			for (EventfulListener l : simpleListenersCopy) {
				try {
					l.change();
				} catch (Exception e) {
					OneLog.log(Level.WARNING, "Exception in EventfulListener", e);
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
