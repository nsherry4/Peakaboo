package org.peakaboo.framework.eventful;


/**
 * Something which generates events of type T
 *
 * @param <T>
 */

public interface IEventfulType<T>
{

	void addListener(EventfulTypeListener<T> l);


	void removeListener(final EventfulTypeListener<T> l);


	void removeAllListeners();


	void updateListeners(final T message);


	void addListener(EventfulListener l);


	void removeListener(EventfulListener l);
	
}