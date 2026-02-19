package org.peakaboo.framework.eventful.cache;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.peakaboo.framework.eventful.EventfulListener;

public class EventfulCacheTest {

	@Test
	public void testSoftCacheBasicOperation() {
		AtomicInteger regenerateCount = new AtomicInteger(0);
		EventfulSoftCache<String> cache = new EventfulSoftCache<>(() -> {
			regenerateCount.incrementAndGet();
			return "value" + regenerateCount.get();
		});

		// First getValue should regenerate
		String value1 = cache.getValue();
		assertEquals("value1", value1);
		assertEquals(1, regenerateCount.get());

		// Second getValue should return cached value
		String value2 = cache.getValue();
		assertEquals("value1", value2);
		assertEquals(1, regenerateCount.get()); // Still 1, not regenerated
	}

	@Test
	public void testSoftCacheInvalidation() {
		AtomicInteger regenerateCount = new AtomicInteger(0);
		EventfulSoftCache<String> cache = new EventfulSoftCache<>(() -> {
			regenerateCount.incrementAndGet();
			return "value" + regenerateCount.get();
		});

		String value1 = cache.getValue();
		assertEquals("value1", value1);

		cache.invalidate();

		// After invalidation, getValue should regenerate
		String value2 = cache.getValue();
		assertEquals("value2", value2);
		assertEquals(2, regenerateCount.get());
	}

	@Test
	public void testSoftCacheInvalidationNotifiesListeners() {
		AtomicInteger listenerCallCount = new AtomicInteger(0);
		EventfulSoftCache<String> cache = new EventfulSoftCache<>(() -> "value");

		cache.addListener(() -> listenerCallCount.incrementAndGet());

		cache.invalidate();

		assertEquals(1, listenerCallCount.get());
	}

	@Test
	public void testNullableCacheBasicOperation() {
		AtomicInteger regenerateCount = new AtomicInteger(0);
		EventfulNullableCache<String> cache = new EventfulNullableCache<>(() -> {
			regenerateCount.incrementAndGet();
			return "value" + regenerateCount.get();
		});

		String value1 = cache.getValue();
		assertEquals("value1", value1);
		assertEquals(1, regenerateCount.get());

		// Second getValue should return cached value
		String value2 = cache.getValue();
		assertEquals("value1", value2);
		assertEquals(1, regenerateCount.get());
	}

	@Test
	public void testNullableCacheCanStoreNull() {
		AtomicInteger regenerateCount = new AtomicInteger(0);
		EventfulNullableCache<String> cache = new EventfulNullableCache<>(() -> {
			regenerateCount.incrementAndGet();
			return null;
		});

		String value1 = cache.getValue();
		assertNull(value1);
		assertEquals(1, regenerateCount.get());

		// Second getValue should return cached null without regenerating
		String value2 = cache.getValue();
		assertNull(value2);
		assertEquals(1, regenerateCount.get()); // Should still be 1
	}

	@Test
	public void testNullableCacheInvalidation() {
		AtomicInteger regenerateCount = new AtomicInteger(0);
		EventfulNullableCache<String> cache = new EventfulNullableCache<>(() -> {
			regenerateCount.incrementAndGet();
			return "value" + regenerateCount.get();
		});

		String value1 = cache.getValue();
		assertEquals("value1", value1);

		cache.invalidate();

		String value2 = cache.getValue();
		assertEquals("value2", value2);
		assertEquals(2, regenerateCount.get());
	}

	@Test
	public void testNullableCacheInvalidationNotifiesListeners() {
		AtomicInteger listenerCallCount = new AtomicInteger(0);
		EventfulNullableCache<String> cache = new EventfulNullableCache<>(() -> "value");

		cache.addListener(() -> listenerCallCount.incrementAndGet());

		cache.invalidate();

		assertEquals(1, listenerCallCount.get());
	}

	@Test
	public void testCacheSupplierDeterminism() {
		// This tests the documented contract that suppliers should be deterministic
		AtomicInteger callCount = new AtomicInteger(0);
		EventfulSoftCache<Integer> cache = new EventfulSoftCache<>(() -> {
			return callCount.incrementAndGet();
		});

		// In this test, we're violating the determinism contract on purpose
		// to verify that regeneration uses the supplier
		int value1 = cache.getValue();
		assertEquals(1, value1);

		cache.invalidate();
		int value2 = cache.getValue();
		assertEquals(2, value2); // Different value because supplier isn't deterministic
	}

	@Test
	public void testCacheDependencyChain() {
		// Test that cache invalidation propagates through dependencies
		AtomicInteger listener1Calls = new AtomicInteger(0);
		AtomicInteger listener2Calls = new AtomicInteger(0);

		EventfulSoftCache<String> cache1 = new EventfulSoftCache<>(() -> "base");
		EventfulSoftCache<String> cache2 = new EventfulSoftCache<>(() -> cache1.getValue() + "-derived");

		// Make cache2 depend on cache1
		cache1.addListener(() -> {
			listener1Calls.incrementAndGet();
			cache2.invalidate();
		});

		cache2.addListener(() -> listener2Calls.incrementAndGet());

		// Invalidating cache1 should trigger invalidation of cache2
		cache1.invalidate();

		assertEquals(1, listener1Calls.get());
		assertEquals(1, listener2Calls.get());
	}
}