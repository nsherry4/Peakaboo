package org.peakaboo.framework.eventful;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class EventfulDispatchListenerTest {

	// Test event hierarchy
	static class BaseEvent {
		String message;
		BaseEvent(String message) { this.message = message; }
	}

	static class SpecificEventA extends BaseEvent {
		SpecificEventA(String message) { super(message); }
	}

	static class SpecificEventB extends BaseEvent {
		SpecificEventB(String message) { super(message); }
	}

	@Test
	public void testBasicDispatching() {
		List<String> handledEvents = new ArrayList<>();

		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>();
		dispatcher.forType(SpecificEventA.class, (SpecificEventA event) -> {
			handledEvents.add("A:" + event.message);
		});
		dispatcher.forType(SpecificEventB.class, (SpecificEventB event) -> {
			handledEvents.add("B:" + event.message);
		});

		dispatcher.change(new SpecificEventA("test1"));
		dispatcher.change(new SpecificEventB("test2"));

		assertEquals(2, handledEvents.size());
		assertEquals("A:test1", handledEvents.get(0));
		assertEquals("B:test2", handledEvents.get(1));
	}

	@Test
	public void testUnhandledEventWithFailFalse() {
		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>(false);
		dispatcher.forType(SpecificEventA.class, (SpecificEventA event) -> {
			// Handler for A only
		});

		// Sending a B event should not throw when failWhenUnhandled is false
		dispatcher.change(new SpecificEventB("test"));
		assertTrue(true); // If we get here, test passed
	}

	@Test(expected = RuntimeException.class)
	public void testUnhandledEventWithFailTrue() {
		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>(true);
		dispatcher.forType(SpecificEventA.class, (SpecificEventA event) -> {
			// Handler for A only
		});

		// Sending a B event should throw when failWhenUnhandled is true
		dispatcher.change(new SpecificEventB("test"));
	}

	@Test
	public void testNullEventHandling() {
		List<String> handledEvents = new ArrayList<>();

		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>();
		dispatcher.forType(null, (BaseEvent event) -> {
			handledEvents.add("null-handler");
		});

		dispatcher.change(null);

		assertEquals(1, handledEvents.size());
		assertEquals("null-handler", handledEvents.get(0));
	}

	@Test
	public void testMultipleHandlersForSameType() {
		List<String> handledEvents = new ArrayList<>();

		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>();
		dispatcher.forType(SpecificEventA.class, (SpecificEventA event) -> {
			handledEvents.add("handler1");
		});
		dispatcher.forType(SpecificEventA.class, (SpecificEventA event) -> {
			handledEvents.add("handler2");
		});

		dispatcher.change(new SpecificEventA("test"));

		// Both handlers should be called
		assertEquals(2, handledEvents.size());
		assertEquals("handler1", handledEvents.get(0));
		assertEquals("handler2", handledEvents.get(1));
	}

	@Test
	public void testBaseClassMatching() {
		List<String> handledEvents = new ArrayList<>();

		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>();
		dispatcher.forType(BaseEvent.class, (BaseEvent event) -> {
			handledEvents.add("base:" + event.message);
		});

		// Should match both base and derived classes
		dispatcher.change(new BaseEvent("test1"));
		dispatcher.change(new SpecificEventA("test2"));

		assertEquals(2, handledEvents.size());
		assertEquals("base:test1", handledEvents.get(0));
		assertEquals("base:test2", handledEvents.get(1));
	}

	@Test
	public void testFluentInterface() {
		List<String> handledEvents = new ArrayList<>();

		// Test that forType returns the dispatcher for chaining
		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<BaseEvent>()
			.forType(SpecificEventA.class, (SpecificEventA e) -> handledEvents.add("A"))
			.forType(SpecificEventB.class, (SpecificEventB e) -> handledEvents.add("B"));

		dispatcher.change(new SpecificEventA("test"));
		dispatcher.change(new SpecificEventB("test"));

		assertEquals(2, handledEvents.size());
	}

	@Test
	public void testNoHandlers() {
		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>(false);

		// Should not crash with no handlers
		dispatcher.change(new BaseEvent("test"));
		assertTrue(true);
	}

	@Test
	public void testUseAsRegularListener() {
		List<String> handledEvents = new ArrayList<>();

		EventfulType<BaseEvent> eventful = new EventfulType<>();

		EventfulDispatchListener<BaseEvent> dispatcher = new EventfulDispatchListener<>();
		dispatcher.forType(SpecificEventA.class, (SpecificEventA e) -> handledEvents.add("A:" + e.message));

		eventful.addListener(dispatcher);

		eventful.updateListeners(new SpecificEventA("test"));

		assertEquals(1, handledEvents.size());
		assertEquals("A:test", handledEvents.get(0));
	}
}