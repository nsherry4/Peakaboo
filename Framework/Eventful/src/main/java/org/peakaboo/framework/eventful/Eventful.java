package org.peakaboo.framework.eventful;


import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;


/**
 * 
 * A controller for a simple Model/View/Controller system. Contains the mechanism for notifying
 * views of a change to the model via the controller.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */


public class Eventful implements IEventful
{

	protected final List<EventfulListener>	listeners;
	private Consumer<Runnable> uiThreadRunnerOverride = null;
	

	public Eventful()
	{
		listeners = new LinkedList<EventfulListener>();
	}


	public synchronized void addListener(EventfulListener l)
	{
		listeners.add(l);
	}



	//Done on the event thread on purpose
	public synchronized void removeListener(final EventfulListener l)
	{
		getUIThreadRunner().accept(() -> { 
			synchronized(Eventful.this) { 
					listeners.remove(l);
			}
		});
	}

	//Done on the event thread on purpose
	public synchronized void removeAllListeners()
	{
		getUIThreadRunner().accept(() -> { 
			synchronized(Eventful.this) { 
					listeners.clear();
			}
		});
	}

	private Consumer<Runnable> getUIThreadRunner() {
		if (uiThreadRunnerOverride == null) {
			return EventfulConfig.uiThreadRunner;
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
	
	
	/**
	 * @see org.peakaboo.framework.eventful.IEventful#updateListeners()
	 */
	public void updateListeners()
	{

		if (listeners.size() == 0) return;

		getUIThreadRunner().accept(() -> { 
			synchronized(Eventful.this) {
				for (EventfulListener l : listeners) {
					l.change();
				}
			}
		});

	}


}
