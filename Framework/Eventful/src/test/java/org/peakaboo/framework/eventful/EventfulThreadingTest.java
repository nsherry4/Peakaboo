package org.peakaboo.framework.eventful;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class EventfulThreadingTest {

	@Test
	public void testUIThreadRunnerOverride() throws InterruptedException {
		EventfulBeacon beacon = new EventfulBeacon();
		List<String> executionOrder = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch(1);

		// Override to run on a different thread
		beacon.setUIThreadRunnerOverride(runnable -> {
			new Thread(() -> {
				executionOrder.add("background");
				runnable.run();
				latch.countDown();
			}).start();
		});

		AtomicInteger callCount = new AtomicInteger(0);
		beacon.addListener(() -> {
			executionOrder.add("listener");
			callCount.incrementAndGet();
		});

		executionOrder.add("before-update");
		beacon.updateListeners();
		executionOrder.add("after-update");

		// Wait for background thread
		assertTrue(latch.await(1, TimeUnit.SECONDS));

		assertEquals(1, callCount.get());
		// "before-update" is guaranteed to be first (added before updateListeners called)
		assertEquals("before-update", executionOrder.get(0));
		// "background" and "listener" are on the same thread, so "background" comes before "listener"
		int backgroundIndex = executionOrder.indexOf("background");
		int listenerIndex = executionOrder.indexOf("listener");
		assertTrue("background should come before listener", backgroundIndex < listenerIndex);
		// All four entries should be present
		assertTrue(executionOrder.contains("after-update"));
		assertEquals(4, executionOrder.size());
	}

	@Test
	public void testDefaultSynchronousExecution() {
		EventfulBeacon beacon = new EventfulBeacon();
		List<String> executionOrder = new ArrayList<>();

		beacon.addListener(() -> executionOrder.add("listener"));

		executionOrder.add("before");
		beacon.updateListeners();
		executionOrder.add("after");

		// With default synchronous execution, listener should run before "after"
		assertEquals("before", executionOrder.get(0));
		assertEquals("listener", executionOrder.get(1));
		assertEquals("after", executionOrder.get(2));
	}

	@Test
	public void testRemovalOrderingGuarantee() {
		EventfulBeacon beacon = new EventfulBeacon();
		List<String> events = new ArrayList<>();

		EventfulListener listener = () -> events.add("event");

		beacon.addListener(listener);

		// These operations are queued in order:
		beacon.updateListeners();      // 1. Notify listener
		beacon.removeListener(listener); // 2. Remove listener
		beacon.updateListeners();      // 3. Notify (listener already removed)

		// Listener should only receive the first event
		assertEquals(1, events.size());
	}

	@Test
	public void testSnapshotIsolation() {
		EventfulType<String> eventful = new EventfulType<>();
		List<String> receivedMessages = new ArrayList<>();

		eventful.addListener((String msg) -> {
			receivedMessages.add(msg);
			// Try to modify listener list during notification
			if (msg.equals("first")) {
				eventful.addListener((String m) -> receivedMessages.add("late:" + m));
			}
		});

		eventful.updateListeners("first");

		// Late listener should not receive "first" message
		assertEquals(1, receivedMessages.size());
		assertEquals("first", receivedMessages.get(0));

		eventful.updateListeners("second");

		// Now late listener should receive "second"
		assertEquals(3, receivedMessages.size());
		assertEquals("second", receivedMessages.get(1));
		assertEquals("late:second", receivedMessages.get(2));
	}

	@Test
	public void testConcurrentAddAndNotify() throws InterruptedException {
		EventfulBeacon beacon = new EventfulBeacon();
		AtomicInteger notifyCount = new AtomicInteger(0);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch addDone = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(2);

		// Thread 1: Add listeners
		Thread adder = new Thread(() -> {
			try {
				startLatch.await();
				for (int i = 0; i < 100; i++) {
					beacon.addListener(() -> notifyCount.incrementAndGet());
				}
				addDone.countDown();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				doneLatch.countDown();
			}
		});

		// Thread 2: Wait for some listeners to be added, then notify
		Thread notifier = new Thread(() -> {
			try {
				startLatch.await();
				// Wait for listeners to be added first
				addDone.await();
				for (int i = 0; i < 100; i++) {
					beacon.updateListeners();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				doneLatch.countDown();
			}
		});

		adder.start();
		notifier.start();
		startLatch.countDown(); // Start both threads

		assertTrue(doneLatch.await(5, TimeUnit.SECONDS));

		// All 100 listeners should have been called 100 times
		assertEquals(10000, notifyCount.get());
	}

	@Test
	public void testPerInstanceOverride() {
		EventfulBeacon beacon1 = new EventfulBeacon();
		EventfulBeacon beacon2 = new EventfulBeacon();

		List<String> execution = new ArrayList<>();

		beacon1.setUIThreadRunnerOverride(r -> {
			execution.add("custom");
			r.run();
		});

		// beacon2 uses default

		beacon1.addListener(() -> execution.add("beacon1"));
		beacon2.addListener(() -> execution.add("beacon2"));

		beacon1.updateListeners();
		beacon2.updateListeners();

		// beacon1 should use custom runner, beacon2 uses default
		assertTrue(execution.contains("custom"));
		assertTrue(execution.contains("beacon1"));
		assertTrue(execution.contains("beacon2"));
	}
}