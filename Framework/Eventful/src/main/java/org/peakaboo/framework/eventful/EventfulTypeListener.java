package org.peakaboo.framework.eventful;

import java.util.EventListener;

/**
 * 
 * Basic listener for a simple Model/View/Controller system. Receives an update when a change occurs.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

@FunctionalInterface
public interface EventfulTypeListener<T> extends EventListener {

	void change(T message);
	
}