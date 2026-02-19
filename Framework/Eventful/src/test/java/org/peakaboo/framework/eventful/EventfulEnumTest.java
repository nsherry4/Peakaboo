package org.peakaboo.framework.eventful;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class EventfulEnumTest {

	enum TestState {
		IDLE, RUNNING, STOPPED
	}

	private EventfulEnum<TestState> eventful;

	@Before
	public void setUp() {
		eventful = new EventfulEnum<>();
	}

	@Test
	public void testEnumListener() {
		List<TestState> receivedStates = new ArrayList<>();

		eventful.addListener((TestState state) -> receivedStates.add(state));

		eventful.updateListeners(TestState.IDLE);
		eventful.updateListeners(TestState.RUNNING);
		eventful.updateListeners(TestState.STOPPED);

		assertEquals(3, receivedStates.size());
		assertEquals(TestState.IDLE, receivedStates.get(0));
		assertEquals(TestState.RUNNING, receivedStates.get(1));
		assertEquals(TestState.STOPPED, receivedStates.get(2));
	}

	@Test
	public void testEnumListenerInterface() {
		List<TestState> receivedStates = new ArrayList<>();

		// Test that EventfulEnumListener works (extends EventfulTypeListener)
		EventfulEnumListener<TestState> listener = state -> receivedStates.add(state);

		eventful.addListener(listener);
		eventful.updateListeners(TestState.RUNNING);

		assertEquals(1, receivedStates.size());
		assertEquals(TestState.RUNNING, receivedStates.get(0));
	}

	@Test
	public void testSimpleListenerWithEnum() {
		AtomicInteger callCount = new AtomicInteger(0);

		eventful.addListener(() -> callCount.incrementAndGet());

		eventful.updateListeners(TestState.IDLE);
		eventful.updateListeners(TestState.RUNNING);

		assertEquals(2, callCount.get());
	}

	@Test
	public void testRemoveEnumListener() {
		List<TestState> states = new ArrayList<>();
		EventfulEnumListener<TestState> listener = state -> states.add(state);

		eventful.addListener(listener);
		eventful.updateListeners(TestState.IDLE);
		assertEquals(1, states.size());

		eventful.removeListener(listener);
		eventful.updateListeners(TestState.RUNNING);

		// Should still be 1
		assertEquals(1, states.size());
	}

	@Test
	public void testInheritedBehavior() {
		// Verify that EventfulEnum properly inherits EventfulType behavior
		List<TestState> enumStates = new ArrayList<>();
		AtomicInteger simpleCount = new AtomicInteger(0);

		eventful.addListener((TestState state) -> enumStates.add(state));
		eventful.addListener(() -> simpleCount.incrementAndGet());

		eventful.updateListeners(TestState.RUNNING);

		assertEquals(1, enumStates.size());
		assertEquals(TestState.RUNNING, enumStates.get(0));
		assertEquals(1, simpleCount.get());
	}
}