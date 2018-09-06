package eventful;

import java.util.LinkedList;
import java.util.List;

public class EventfulEnum<T extends Enum<T>> implements IEventfulEnum<T>
{

	protected final List<EventfulEnumListener<T>>	listeners;
	
	
	public EventfulEnum() {
		listeners = new LinkedList<EventfulEnumListener<T>>();
	}
	
	public synchronized void addListener(EventfulEnumListener<T> l)
	{
		listeners.add(l);
	}


	//Done on the event thread on purpose
	public synchronized void removeListener(final EventfulEnumListener<T> l)
	{
		EventfulConfig.uiThreadRunner.accept(() -> { 
			synchronized(EventfulEnum.this) { 
					listeners.remove(l);
			}
		});
	}

	//Done on the event thread on purpose
	public synchronized void removeAllListeners()
	{
		EventfulConfig.uiThreadRunner.accept(() -> { 
			synchronized(EventfulEnum.this) { 
					listeners.clear();
			}
		});
	}

	
	public void updateListeners(final T message)
	{

		if (listeners.size() == 0) return;

		EventfulConfig.uiThreadRunner.accept(() -> { 
			synchronized(EventfulEnum.this) {	
				for (EventfulEnumListener<T> l : listeners) {		
					l.change(message);
				}
			}
		});

	}
	
}
