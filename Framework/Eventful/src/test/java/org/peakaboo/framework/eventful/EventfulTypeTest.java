package org.peakaboo.framework.eventful;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class EventfulTypeTest {

	private EventfulType<String> eventful;

	@Before
	public void setUp() {
		eventful = new EventfulType<>();
	}

	@Test
	public void testTypedListener() {
		List<String> receivedMessages = new ArrayList<>();

		eventful.addListener((String msg) -> receivedMessages.add(msg));

		eventful.updateListeners("hello");
		eventful.updateListeners("world");

		assertEquals(2, receivedMessages.size());
		assertEquals("hello", receivedMessages.get(0));
		assertEquals("world", receivedMessages.get(1));
	}

	@Test
	public void testSimpleListener() {
		AtomicInteger callCount = new AtomicInteger(0);

		eventful.addListener(() -> callCount.incrementAndGet());

		eventful.updateListeners("message1");
		eventful.updateListeners("message2");

		assertEquals(2, callCount.get());
	}

	@Test
	public void testBothListenerTypes() {
		List<String> typedMessages = new ArrayList<>();
		AtomicInteger simpleCount = new AtomicInteger(0);

		eventful.addListener((String msg) -> typedMessages.add(msg));
		eventful.addListener(() -> simpleCount.incrementAndGet());

		eventful.updateListeners("test");

		assertEquals(1, typedMessages.size());
		assertEquals("test", typedMessages.get(0));
		assertEquals(1, simpleCount.get());
	}

	@Test
	public void testRemoveTypedListener() {
		List<String> messages = new ArrayList<>();
		EventfulTypeListener<String> listener = msg -> messages.add(msg);

		eventful.addListener(listener);
		eventful.updateListeners("first");
		assertEquals(1, messages.size());

		eventful.removeListener(listener);
		eventful.updateListeners("second");

		// Should still be 1
		assertEquals(1, messages.size());
	}

	@Test
	public void testRemoveSimpleListener() {
		AtomicInteger count = new AtomicInteger(0);
		EventfulListener listener = () -> count.incrementAndGet();

		eventful.addListener(listener);
		eventful.updateListeners("first");
		assertEquals(1, count.get());

		eventful.removeListener(listener);
		eventful.updateListeners("second");

		// Should still be 1
		assertEquals(1, count.get());
	}

	@Test
	public void testRemoveAllListeners() {
		List<String> typedMessages = new ArrayList<>();
		AtomicInteger simpleCount = new AtomicInteger(0);

		eventful.addListener((String msg) -> typedMessages.add(msg));
		eventful.addListener(() -> simpleCount.incrementAndGet());

		eventful.updateListeners("first");
		assertEquals(1, typedMessages.size());
		assertEquals(1, simpleCount.get());

		eventful.removeAllListeners();
		eventful.updateListeners("second");

		// Should still be 1
		assertEquals(1, typedMessages.size());
		assertEquals(1, simpleCount.get());
	}

	@Test
	public void testExceptionInTypedListener() {
		List<String> messages = new ArrayList<>();

		eventful.addListener((String msg) -> messages.add("first"));
		eventful.addListener((String msg) -> {
			throw new RuntimeException("Test exception");
		});
		eventful.addListener((String msg) -> messages.add("second"));

		eventful.updateListeners("test");

		// Both non-throwing listeners should have been called
		assertEquals(2, messages.size());
		assertEquals("first", messages.get(0));
		assertEquals("second", messages.get(1));
	}

	@Test
	public void testExceptionInSimpleListener() {
		AtomicInteger count1 = new AtomicInteger(0);
		AtomicInteger count2 = new AtomicInteger(0);

		eventful.addListener(() -> count1.incrementAndGet());
		eventful.addListener(() -> {
			throw new RuntimeException("Test exception");
		});
		eventful.addListener(() -> count2.incrementAndGet());

		eventful.updateListeners("test");

		assertEquals(1, count1.get());
		assertEquals(1, count2.get());
	}

	@Test
	public void testNullMessage() {
		List<String> receivedMessages = new ArrayList<>();

		eventful.addListener((String msg) -> receivedMessages.add(msg));

		eventful.updateListeners(null);

		assertEquals(1, receivedMessages.size());
		assertNull(receivedMessages.get(0));
	}

	@Test
	public void testListenerSnapshot() {
		List<Integer> callOrder = new ArrayList<>();

		eventful.addListener((String msg) -> {
			callOrder.add(1);
			// Try to add another listener during notification
			eventful.addListener((String m) -> callOrder.add(3));
		});

		eventful.addListener((String msg) -> callOrder.add(2));

		eventful.updateListeners("test");

		// Only first two listeners should be called
		assertEquals(2, callOrder.size());
		assertEquals(Integer.valueOf(1), callOrder.get(0));
		assertEquals(Integer.valueOf(2), callOrder.get(1));

		// New listener should be called on next update
		eventful.updateListeners("test2");
		assertEquals(5, callOrder.size()); // 1, 2, then 1, 2, 3
	}

	@Test
	public void testNoListeners() {
		// Should not crash
		eventful.updateListeners("test");
		assertTrue(true);
	}
}