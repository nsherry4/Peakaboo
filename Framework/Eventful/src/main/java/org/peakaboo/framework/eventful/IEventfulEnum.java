package org.peakaboo.framework.eventful;

/**
 * Interface for enum-based event sources. Extends IEventfulType to inherit
 * standard typed event functionality. Since EventfulEnumListener extends
 * EventfulTypeListener, the methods from the parent interface work directly.
 */
public interface IEventfulEnum<T extends Enum<T>> extends IEventfulType<T> {}