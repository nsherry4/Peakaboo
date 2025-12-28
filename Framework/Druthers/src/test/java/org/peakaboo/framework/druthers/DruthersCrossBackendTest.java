package org.peakaboo.framework.druthers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.peakaboo.framework.druthers.DruthersBackendCompatibilityTest.NestedObject;
import org.peakaboo.framework.druthers.DruthersBackendCompatibilityTest.ObjectWithCollections;
import org.peakaboo.framework.druthers.DruthersBackendCompatibilityTest.SimpleObject;
import org.peakaboo.framework.druthers.serialize.DruthersJacksonBackend;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSnakeYamlBackend;

/**
 * Cross-backend compatibility tests for Druthers serialization.
 * <p>
 * These tests verify that YAML produced by one backend can be correctly
 * deserialized by another backend. This is critical for cross-platform
 * compatibility, particularly for Android (Jackson) ↔ Desktop (SnakeYAML).
 * <p>
 * Both directions must work:
 * <ul>
 *   <li>SnakeYAML → Jackson (desktop-created files read on Android)</li>
 *   <li>Jackson → SnakeYAML (Android-created files read on desktop)</li>
 * </ul>
 *
 */
public class DruthersCrossBackendTest {

	private final DruthersSerializerBackend snakeYaml = new DruthersSnakeYamlBackend();
	private final DruthersSerializerBackend jackson = new DruthersJacksonBackend();

	// ========================================================================
	// SnakeYAML → Jackson Cross-Compatibility
	// ========================================================================

	@Test
	public void testSnakeYamlToJackson() throws DruthersLoadException {
		// Create complex test object with collections and nested structures
		ObjectWithCollections original = new ObjectWithCollections(
			"org.peakaboo.crossbackend/v1",
			List.of(1, 2, 3, 5, 8, 13),
			List.of("alpha", "beta", "gamma", "delta"),
			Map.of("key1", "value1", "key2", "value2", "key3", "value3"),
			List.of(
				new SimpleObject("first", 1, true),
				new SimpleObject("second", 2, false),
				new SimpleObject("third", 3, true)
			)
		);

		// Serialize with SnakeYAML
		String yaml = snakeYaml.serialize(original);

		// Verify clean YAML (no type tags)
		assertFalse("SnakeYAML output should not contain !! type tags", yaml.contains("!!"));
		assertFalse("SnakeYAML output should not contain @type annotations", yaml.contains("@type"));

		// Deserialize with Jackson
		ObjectWithCollections deserialized = jackson.deserialize(
			yaml, false, null, ObjectWithCollections.class
		);

		// Verify round-trip
		assertEquals("Jackson should correctly deserialize YAML produced by SnakeYAML",
			original, deserialized);
	}

	// ========================================================================
	// Jackson → SnakeYAML Cross-Compatibility
	// ========================================================================

	@Test
	public void testJacksonToSnakeYaml() throws DruthersLoadException {
		// Create nested test object to verify complex structure handling
		NestedObject original = new NestedObject(
			"parent-object",
			new SimpleObject("child-object", 100, false),
			Map.of(
				"first", new SimpleObject("one", 1, true),
				"second", new SimpleObject("two", 2, false),
				"third", new SimpleObject("three", 3, true)
			)
		);

		// Serialize with Jackson
		String yaml = jackson.serialize(original);

		// Verify clean YAML (no type information)
		assertFalse("Jackson output should not contain !! type tags", yaml.contains("!!"));
		assertFalse("Jackson output should not contain @type annotations", yaml.contains("@type"));
		assertFalse("Jackson output should not contain @class annotations", yaml.contains("@class"));

		// Deserialize with SnakeYAML
		NestedObject deserialized = snakeYaml.deserialize(
			yaml, false, null, NestedObject.class
		);

		// Verify round-trip
		assertEquals("SnakeYAML should correctly deserialize YAML produced by Jackson",
			original, deserialized);
	}

}
