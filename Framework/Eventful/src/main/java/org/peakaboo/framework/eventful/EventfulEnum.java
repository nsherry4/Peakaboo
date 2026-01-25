package org.peakaboo.framework.eventful;

/**
 * EventfulEnum is a specialization of EventfulType for enum-based events.
 * It extends EventfulType and provides no additional functionality, serving only
 * as a type constraint to ensure T extends Enum. All listener management is
 * handled by the parent EventfulType class.
 */
public class EventfulEnum<T extends Enum<T>> extends EventfulType<T> implements IEventfulEnum<T>
{

	public EventfulEnum() {
		super();
	}

	// All methods inherited from EventfulType work directly with EventfulEnumListener
	// since EventfulEnumListener extends EventfulTypeListener

}