package org.peakaboo.framework.eventful;

import java.util.ArrayList;
import java.util.List;

public class EventfulDispatchListener<T> implements EventfulTypeListener<T> {

	private List<DispatchMapping<T, ? extends T>> handlerTable;
	private boolean failWhenUnhandled;
	
	public EventfulDispatchListener() {
		this(false);
	}
	
	public EventfulDispatchListener(boolean failWhenUnhandled) {
		this.handlerTable = new ArrayList<>();
		this.failWhenUnhandled = failWhenUnhandled;
	}
	
	public <S extends T> EventfulDispatchListener<T> forType(Class<S> cls, EventfulTypeListener<S> listener) {
		this.handlerTable.add(new DispatchMapping<>(cls, listener));
		return this;
	}

	@Override
	public void change(T message) {
		boolean handled = false;
		for (var handler : handlerTable) {
			handled |= handler.offer(message);
		}
		if (!handled && failWhenUnhandled) {
			throw new RuntimeException("Failed to handle event of type " + message.getClass().getSimpleName());
		}
	}
	
	public static class DispatchMapping<T, S extends T> {
		
		private Class<S> cls;
		private EventfulTypeListener<S> listener;
		
		public DispatchMapping(Class<S> cls, EventfulTypeListener<S> listener) {
			this.cls = cls;
			this.listener = listener;
		}
		
		@SuppressWarnings("unchecked")
		boolean offer(T message) {

			//Cases where message or cls are null
			if (cls == null && message == null) {
				//Handle case where class and message are null (it's a match)
				listener.change((S)message);
				return true;
			} else if (cls == null) {
				// If cls == null but the message isn't, it's not a match
				return false;
			} else if (message == null) {
				// If the message is null but cls isn't, we won't be matching this message
				return false;
			}
			
			//When neither are null, check for a match. If we find one, send the message on 
			if (cls.isInstance(message)) {
				listener.change((S)message);
				return true;
			}
			
			//No matches
			return false;
		}
		
	}
	
}


