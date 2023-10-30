package org.peakaboo.framework.eventful;



public interface IEventfulEnum<T extends Enum<T>>
{

	void addListener(EventfulEnumListener<T> l);


	void removeListener(final EventfulEnumListener<T> l);


	void removeAllListeners();


	void updateListeners(final T message);


	void addListener(EventfulListener l);


	void removeListener(EventfulListener l);

}