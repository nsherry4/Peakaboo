package org.peakaboo.framework.eventful;

import java.util.EventListener;

/**
 *
 * Basic listener for a simple Model/View/Controller system. Receives an update when a change occurs.
 * This is a specialization of EventfulTypeListener for enum-based events.
 *
 * @author Nathaniel Sherry, 2009
 *
 */

@FunctionalInterface
public interface EventfulEnumListener<T extends Enum<T>> extends EventfulTypeListener<T> {

	// Inherits: void change(T message);

}