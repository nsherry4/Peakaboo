package org.peakaboo.framework.druthers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSnakeYamlBackend;

/**
 * Compatibility tests for DruthersSerializerBackend implementations.
 * <p>
 * These tests verify that backend implementations:
 * <ul>
 *   <li>Produce clean YAML without class tags or unnecessary anchors</li>
 *   <li>Support round-trip serialization</li>
 *   <li>Handle collections, nulls, and nested objects correctly</li>
 *   <li>Respect strict and non-strict modes</li>
 *   <li>Can read "golden" YAML samples produced by other implementations</li>
 * </ul>
 * <p>
 * Tests are designed to run against a single backend at a time. Once both
 * SnakeYAML and Jackson backends pass these tests individually, cross-compatibility
 * tests can verify they produce mutually readable YAML.
 */
public class DruthersBackendCompatibilityTest {

	// ========================================================================
	// Test Data Classes
	// ========================================================================

	static class SimpleObject {
		public String name;
		public int value;
		public boolean flag;

		public SimpleObject() {}

		public SimpleObject(String name, int value, boolean flag) {
			this.name = name;
			this.value = value;
			this.flag = flag;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof SimpleObject other)) return false;
			return name.equals(other.name) && value == other.value && flag == other.flag;
		}

		@Override
		public String toString() {
			return "SimpleObject{name='" + name + "', value=" + value + ", flag=" + flag + "}";
		}
	}

	static class ObjectWithCollections {
		public String format;
		public List<Integer> numbers;
		public List<String> strings;
		public Map<String, String> dictionary;
		public List<SimpleObject> objects;

		public ObjectWithCollections() {}

		public ObjectWithCollections(String format, List<Integer> numbers, List<String> strings,
		                             Map<String, String> dictionary, List<SimpleObject> objects) {
			this.format = format;
			this.numbers = numbers;
			this.strings = strings;
			this.dictionary = dictionary;
			this.objects = objects;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ObjectWithCollections other)) return false;
			return format.equals(other.format)
				&& numbers.equals(other.numbers)
				&& strings.equals(other.strings)
				&& dictionary.equals(other.dictionary)
				&& objects.equals(other.objects);
		}
	}

	static class NestedObject {
		public String name;
		public SimpleObject nested;
		public Map<String, SimpleObject> nestedMap;

		public NestedObject() {}

		public NestedObject(String name, SimpleObject nested, Map<String, SimpleObject> nestedMap) {
			this.name = name;
			this.nested = nested;
			this.nestedMap = nestedMap;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof NestedObject other)) return false;
			return name.equals(other.name)
				&& nested.equals(other.nested)
				&& nestedMap.equals(other.nestedMap);
		}
	}

	static class ObjectWithNulls {
		public String notNull;
		public String isNull;
		public List<String> nullList;

		public ObjectWithNulls() {}

		public ObjectWithNulls(String notNull) {
			this.notNull = notNull;
			this.isNull = null;
			this.nullList = null;
		}
	}

	static class ObjectWithEmptyCollections {
		public String name;
		public List<String> emptyList;
		public Map<String, String> emptyMap;

		public ObjectWithEmptyCollections() {}

		public ObjectWithEmptyCollections(String name) {
			this.name = name;
			this.emptyList = new ArrayList<>();
			this.emptyMap = new HashMap<>();
		}
	}

	static class GenericObject extends HashMap<String, Object> {}

	// ========================================================================
	// Golden YAML Samples
	// ========================================================================

	/**
	 * Known-good YAML for SimpleObject. All backends must be able to read this.
	 */
	private static final String GOLDEN_SIMPLE_OBJECT = """
		name: test
		value: 42
		flag: true
		""";

	/**
	 * Known-good YAML with collections. All backends must be able to read this.
	 */
	private static final String GOLDEN_WITH_COLLECTIONS = """
		format: org.peakaboo.test/v1
		numbers:
		  - 1
		  - 2
		  - 3
		  - 5
		  - 8
		strings:
		  - alpha
		  - beta
		  - gamma
		dictionary:
		  key1: value1
		  key2: value2
		objects:
		  - name: first
		    value: 1
		    flag: true
		  - name: second
		    value: 2
		    flag: false
		""";

	/**
	 * Known-good YAML with empty collections (no anchors).
	 */
	private static final String GOLDEN_EMPTY_COLLECTIONS = """
		name: test
		emptyList: []
		emptyMap: {}
		""";

	/**
	 * Known-good YAML with null values.
	 */
	private static final String GOLDEN_WITH_NULLS = """
		notNull: present
		isNull: null
		nullList: null
		""";

	// ========================================================================
	// Backend Provider (To Be Implemented)
	// ========================================================================

	/**
	 * Returns the backend implementation to test.
	 * Override this method in subclasses to test different backends.
	 */
	protected DruthersSerializerBackend getBackend() {
		// Default implementation for when this class is run directly
		return new DruthersSnakeYamlBackend();
	}

	// ========================================================================
	// Helper Methods
	// ========================================================================

	/**
	 * Verifies that YAML output meets format requirements.
	 */
	private void assertValidYamlFormat(String yaml) {
		assertFalse("YAML should not contain Java class tags (!!java.*)",
			yaml.contains("!!java"));
		assertFalse("YAML should not contain Peakaboo class tags (!!org.peakaboo.*)",
			yaml.contains("!!org.peakaboo"));
	}

	/**
	 * Verifies that YAML does not contain anchors or aliases.
	 */
	private void assertNoAnchors(String yaml) {
		assertFalse("YAML should not contain anchors (&)",
			yaml.contains("&"));
		assertFalse("YAML should not contain aliases (*)",
			yaml.contains("*"));
	}

	/**
	 * Tests round-trip serialization: object → YAML → object.
	 */
	private <T> void testRoundTrip(DruthersSerializerBackend backend, T original, Class<T> cls)
			throws DruthersLoadException {
		String yaml = backend.serialize(original);
		assertValidYamlFormat(yaml);

		T deserialized = backend.deserialize(yaml, false, null, cls);
		assertEquals("Round-trip serialization failed", original, deserialized);
	}

	// ========================================================================
	// Core Functionality Tests
	// ========================================================================

	@Test
	public void testSimpleObjectRoundTrip() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		SimpleObject obj = new SimpleObject("test", 42, true);
		testRoundTrip(backend, obj, SimpleObject.class);
	}

	@Test
	public void testSimpleObjectGolden() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		SimpleObject obj = backend.deserialize(GOLDEN_SIMPLE_OBJECT, false, null, SimpleObject.class);

		assertEquals("test", obj.name);
		assertEquals(42, obj.value);
		assertEquals(true, obj.flag);
	}

	@Test
	public void testCollectionsRoundTrip() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		ObjectWithCollections obj = new ObjectWithCollections(
			"org.peakaboo.test/v1",
			List.of(1, 2, 3, 5, 8),
			List.of("alpha", "beta", "gamma"),
			Map.of("key1", "value1", "key2", "value2"),
			List.of(
				new SimpleObject("first", 1, true),
				new SimpleObject("second", 2, false)
			)
		);

		testRoundTrip(backend, obj, ObjectWithCollections.class);
	}

	@Test
	public void testCollectionsGolden() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		ObjectWithCollections obj = backend.deserialize(
			GOLDEN_WITH_COLLECTIONS, false, null, ObjectWithCollections.class
		);

		assertEquals("org.peakaboo.test/v1", obj.format);
		assertEquals(List.of(1, 2, 3, 5, 8), obj.numbers);
		assertEquals(List.of("alpha", "beta", "gamma"), obj.strings);
		assertEquals(Map.of("key1", "value1", "key2", "value2"), obj.dictionary);
		assertEquals(2, obj.objects.size());
		assertEquals(new SimpleObject("first", 1, true), obj.objects.get(0));
		assertEquals(new SimpleObject("second", 2, false), obj.objects.get(1));
	}

	@Test
	public void testNestedObjectsRoundTrip() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		NestedObject obj = new NestedObject(
			"parent",
			new SimpleObject("child", 100, false),
			Map.of(
				"first", new SimpleObject("one", 1, true),
				"second", new SimpleObject("two", 2, false)
			)
		);

		testRoundTrip(backend, obj, NestedObject.class);
	}

	@Test
	public void testEmptyCollectionsNoAnchors() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		ObjectWithEmptyCollections obj = new ObjectWithEmptyCollections("test");

		String yaml = backend.serialize(obj);
		assertValidYamlFormat(yaml);
		assertNoAnchors(yaml);

		ObjectWithEmptyCollections deserialized = backend.deserialize(
			yaml, false, null, ObjectWithEmptyCollections.class
		);
		assertEquals("test", deserialized.name);
		assertNotNull(deserialized.emptyList);
		assertNotNull(deserialized.emptyMap);
		assertTrue(deserialized.emptyList.isEmpty());
		assertTrue(deserialized.emptyMap.isEmpty());
	}

	@Test
	public void testEmptyCollectionsGolden() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		ObjectWithEmptyCollections obj = backend.deserialize(
			GOLDEN_EMPTY_COLLECTIONS, false, null, ObjectWithEmptyCollections.class
		);

		assertEquals("test", obj.name);
		assertNotNull(obj.emptyList);
		assertNotNull(obj.emptyMap);
		assertTrue(obj.emptyList.isEmpty());
		assertTrue(obj.emptyMap.isEmpty());
	}

	@Test
	public void testNullValuesRoundTrip() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		ObjectWithNulls obj = new ObjectWithNulls("present");

		String yaml = backend.serialize(obj);
		assertValidYamlFormat(yaml);

		ObjectWithNulls deserialized = backend.deserialize(yaml, false, null, ObjectWithNulls.class);
		assertEquals("present", deserialized.notNull);
		assertNull(deserialized.isNull);
		assertNull(deserialized.nullList);
	}

	@Test
	public void testNullValuesGolden() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		ObjectWithNulls obj = backend.deserialize(
			GOLDEN_WITH_NULLS, false, null, ObjectWithNulls.class
		);

		assertEquals("present", obj.notNull);
		assertNull(obj.isNull);
		assertNull(obj.nullList);
	}

	@Test
	public void testListSerialization() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();
		List<String> list = List.of("alpha", "beta", "gamma", "delta");

		String yaml = backend.serializeList(list);
		assertValidYamlFormat(yaml);

		@SuppressWarnings("unchecked")
		List<String> deserialized = backend.deserialize(yaml, false, null, ArrayList.class);
		assertEquals(list, deserialized);
	}

	@Test
	public void testGenericMapRoundTrip() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		GenericObject map = new GenericObject();
		map.put("string", "value");
		map.put("number", 42);
		map.put("list", List.of(1, 2, 3));
		map.put("nested", Map.of("key", "value"));

		String yaml = backend.serialize(map);
		assertValidYamlFormat(yaml);

		GenericObject deserialized = backend.deserialize(yaml, false, null, GenericObject.class);
		assertEquals("value", deserialized.get("string"));
		assertEquals(42, deserialized.get("number"));
		assertNotNull(deserialized.get("list"));
		assertNotNull(deserialized.get("nested"));
	}

	@Test
	public void testNoClassTagsInOutput() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		SimpleObject obj = new SimpleObject("test", 42, true);
		Map<String, Object> map = Map.of("key", obj);
		List<SimpleObject> list = List.of(obj);

		assertValidYamlFormat(backend.serialize(obj));
		assertValidYamlFormat(backend.serialize(map));
		assertValidYamlFormat(backend.serializeList(list));
	}

	@Test
	public void testStrictModeFormatValidation() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		ObjectWithCollections obj = new ObjectWithCollections(
			"org.peakaboo.test/v1",
			List.of(1, 2, 3),
			List.of("test"),
			Map.of(),
			List.of()
		);

		String yaml = backend.serialize(obj);

		// Correct format should succeed in strict mode
		ObjectWithCollections validResult = backend.deserialize(
			yaml, true, "org.peakaboo.test/v1", ObjectWithCollections.class
		);
		assertNotNull(validResult);

		// Wrong format should fail in strict mode
		try {
			backend.deserialize(yaml, true, "wrong.format/v99", ObjectWithCollections.class);
			fail("Should throw DruthersLoadException for format mismatch in strict mode");
		} catch (DruthersLoadException e) {
			// Expected
			assertTrue(e.getMessage().contains("format") || e.getMessage().contains("mismatch"));
		}
	}

	@Test
	public void testNonStrictModeExtraFields() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		String yamlWithExtra = """
			name: test
			value: 42
			flag: true
			extraField: extra
			anotherExtra: 999
			""";

		SimpleObject obj = backend.deserialize(yamlWithExtra, false, null, SimpleObject.class);
		assertEquals("test", obj.name);
		assertEquals(42, obj.value);
		assertTrue(obj.flag);
	}

	@Test
	public void testNonStrictModeMissingFields() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		String yamlWithMissing = """
			name: partial
			""";

		SimpleObject obj = backend.deserialize(yamlWithMissing, false, null, SimpleObject.class);
		assertEquals("partial", obj.name);
		assertEquals(0, obj.value);  // Default int value
		assertFalse(obj.flag);  // Default boolean value
	}

	@Test
	public void testFieldVsGetterSetterPrecedence() throws DruthersLoadException {
		DruthersSerializerBackend backend = getBackend();

		// Create object and serialize it
		FieldVsGetterTest original = new FieldVsGetterTest("test-value");
		String yaml = backend.serialize(original);

		System.out.println("Backend: " + backend.getClass().getSimpleName());
		System.out.println("Serialized YAML:\n" + yaml);
		System.out.println("\n--- Public Field Analysis ---");
		System.out.println("Public field 'value' getter called during serialization: " + original.getterCalled);

		// Deserialize into a new object
		FieldVsGetterTest deserialized = backend.deserialize(yaml, false, null, FieldVsGetterTest.class);

		System.out.println("Public field 'value' setter called during deserialization: " + deserialized.setterCalled);
		System.out.println("Value: " + deserialized.value);

		// Verify the value was correctly deserialized
		assertEquals("Value should be correctly deserialized", "test-value", deserialized.value);

		// Report which accessor was used for public field
		if (original.getterCalled) {
			System.out.println("RESULT (public field): Getter was used for serialization");
		} else {
			System.out.println("RESULT (public field): Field was used for serialization");
		}

		if (deserialized.setterCalled) {
			System.out.println("RESULT (public field): Setter was used for deserialization");
		} else {
			System.out.println("RESULT (public field): Field was used for deserialization");
		}

		// Report which accessor was used for package-private field
		System.out.println("\n--- Package-Private Field Analysis ---");
		System.out.println("Package-private field 'packagePrivateValue' getter called during serialization: " + original.packagePrivateGetterCalled);
		System.out.println("Package-private field 'packagePrivateValue' setter called during deserialization: " + deserialized.packagePrivateSetterCalled);
		System.out.println("Package-private value: " + deserialized.packagePrivateValue);

		// Check if package-private field was serialized at all
		if (deserialized.packagePrivateValue != null) {
			assertEquals("Package-private value should be correctly deserialized",
				"test-value-package", deserialized.packagePrivateValue);

			if (original.packagePrivateGetterCalled) {
				System.out.println("RESULT (package-private field): Getter was used for serialization");
			} else {
				System.out.println("RESULT (package-private field): Field was used for serialization");
			}

			if (deserialized.packagePrivateSetterCalled) {
				System.out.println("RESULT (package-private field): Setter was used for deserialization");
			} else {
				System.out.println("RESULT (package-private field): Field was used for deserialization");
			}
		} else {
			System.out.println("RESULT (package-private field): NOT SERIALIZED - Backend does not serialize package-private fields");
		}
	}
}
