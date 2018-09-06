package eventful;

import java.util.LinkedList;
import java.util.List;

public class EventfulType<T> implements IEventfulType<T>
{

	protected final List<EventfulTypeListener<T>>	listeners;
	
	
	public EventfulType() {
		
		listeners = new LinkedList<EventfulTypeListener<T>>();
		
	}

	public synchronized void addListener(final EventfulTypeListener<T> l)
	{
		listeners.add(l);
	}


	//Done on the event thread on purpose
	public synchronized void removeListener(final EventfulTypeListener<T> l)
	{
		EventfulConfig.uiThreadRunner.accept(() -> { 
			synchronized(EventfulType.this) { 
				listeners.remove(l);
			}
		});
	}
	

	//Done on the event thread on purpose
	public synchronized void removeAllListeners()
	{
		EventfulConfig.uiThreadRunner.accept(() -> { 
			synchronized(EventfulType.this) { 
				listeners.clear();
			}
		});
	}



	public void updateListeners(final T message)
	{

		if (listeners.size() == 0) return;

		EventfulConfig.uiThreadRunner.accept(() -> {
			synchronized(EventfulType.this){
				for (EventfulTypeListener<T> l : listeners) {
					l.change(message);
				}
			}
		});

	}
	
}
