package org.peakaboo.framework.eventful;



public interface IEventfulBeacon
{

	void addListener(EventfulListener l);


	void removeListener(final EventfulListener l);


	void removeAllListeners();


	void updateListeners();

}