package org.peakaboo.framework.eventful;



public interface IEventfulType<T>
{

	void addListener(EventfulTypeListener<T> l);


	void removeListener(final EventfulTypeListener<T> l);


	void removeAllListeners();


	void updateListeners(final T message);

}