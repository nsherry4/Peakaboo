package eventful;


import java.util.LinkedList;
import java.util.List;


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
		EventfulConfig.runThread.accept(() -> { 
			synchronized(Eventful.this) { 
					listeners.remove(l);
			}
		});
	}

	//Done on the event thread on purpose
	public synchronized void removeAllListeners()
	{
		EventfulConfig.runThread.accept(() -> { 
			synchronized(Eventful.this) { 
					listeners.clear();
			}
		});
	}

	
	/**
	 * @see eventful.IEventful#updateListeners()
	 */
	public void updateListeners()
	{

		if (listeners.size() == 0) return;

		EventfulConfig.runThread.accept(() -> { 
			synchronized(Eventful.this) {
				for (EventfulListener l : listeners) {
					l.change();
				}
			}
		});

	}


}
