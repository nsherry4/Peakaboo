package org.peakaboo.framework.eventful;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class  EventfulBeaconTest {

	private EventfulBeacon beacon;

	@Before
	public void setUp() {
		beacon = new EventfulBeacon();
	}

	@Test
	public void testBasicNotification() {
		AtomicInteger callCount = new AtomicInteger(0);

		beacon.addListener(() -> callCount.incrementAndGet());
		beacon.updateListeners();

		assertEquals(1, callCount.get());
	}

	@Test
	public void testMultipleListeners() {
		AtomicInteger count1 = new AtomicInteger(0);
		AtomicInteger count2 = new AtomicInteger(0);
		AtomicInteger count3 = new AtomicInteger(0);

		beacon.addListener(() -> count1.incrementAndGet());
		beacon.addListener(() -> count2.incrementAndGet());
		beacon.addListener(() -> count3.incrementAndGet());

		beacon.updateListeners();

		assertEquals(1, count1.get());
		assertEquals(1, count2.get());
		assertEquals(1, count3.get());
	}

	@Test
	public void testMultipleNotifications() {
		AtomicInteger callCount = new AtomicInteger(0);

		beacon.addListener(() -> callCount.incrementAndGet());

		beacon.updateListeners();
		beacon.updateListeners();
		beacon.updateListeners();

		assertEquals(3, callCount.get());
	}

	@Test
	public void testRemoveListener() {
		AtomicInteger callCount = new AtomicInteger(0);
		EventfulListener listener = () -> callCount.incrementAndGet();

		beacon.addListener(listener);
		beacon.updateListeners();
		assertEquals(1, callCount.get());

		beacon.removeListener(listener);
		beacon.updateListeners();

		// Should still be 1, not 2
		assertEquals(1, callCount.get());
	}

	@Test
	public void testRemoveAllListeners() {
		AtomicInteger count1 = new AtomicInteger(0);
		AtomicInteger count2 = new AtomicInteger(0);

		beacon.addListener(() -> count1.incrementAndGet());
		beacon.addListener(() -> count2.incrementAndGet());

		beacon.updateListeners();
		assertEquals(1, count1.get());
		assertEquals(1, count2.get());

		beacon.removeAllListeners();
		beacon.updateListeners();

		// Should still be 1, not 2
		assertEquals(1, count1.get());
		assertEquals(1, count2.get());
	}

	@Test
	public void testExceptionIsolation() {
		AtomicInteger count1 = new AtomicInteger(0);
		AtomicInteger count2 = new AtomicInteger(0);

		beacon.addListener(() -> count1.incrementAndGet());
		beacon.addListener(() -> {
			throw new RuntimeException("Test exception");
		});
		beacon.addListener(() -> count2.incrementAndGet());

		beacon.updateListeners();

		// Both listeners should have been called despite the exception
		assertEquals(1, count1.get());
		assertEquals(1, count2.get());
	}

	@Test
	public void testListenerSnapshot() {
		List<Integer> callOrder = new ArrayList<>();

		// Add first listener
		beacon.addListener(() -> {
			callOrder.add(1);
			// Try to add another listener during notification
			beacon.addListener(() -> callOrder.add(3));
		});

		// Add second listener
		beacon.addListener(() -> callOrder.add(2));

		beacon.updateListeners();

		// Only listeners 1 and 2 should be called, not the one added during notification
		assertEquals(2, callOrder.size());
		assertEquals(Integer.valueOf(1), callOrder.get(0));
		assertEquals(Integer.valueOf(2), callOrder.get(1));

		// But the new listener should be called on the next update
		beacon.updateListeners();
		assertEquals(5, callOrder.size()); // 1, 2, then 1, 2, 3
	}

	@Test
	public void testNoListeners() {
		// Should not crash when no listeners registered
		beacon.updateListeners();
		assertTrue(true); // If we get here, test passed
	}

	@Test
	public void testRemovalOrdering() {
		List<String> events = new ArrayList<>();

		EventfulListener listener = () -> events.add("notification");

		beacon.addListener(listener);

		// Queue an event
		beacon.updateListeners();

		// Immediately queue a removal
		beacon.removeListener(listener);

		// Queue another event
		beacon.updateListeners();

		// The listener should receive the first event but not the second
		// because removal is queued to happen between them
		assertEquals(1, events.size());
		assertEquals("notification", events.get(0));
	}
}