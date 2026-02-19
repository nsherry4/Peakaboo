package org.peakaboo.framework.druthers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

public class DruthersSerializerTest {

	public static final String YAML1 = 
"""
format: org.peakaboo.framework.druthers.test/v1
name: test
list:
  - 1
  - 3
  - 5
dict:
  a: A
  b: B
  c: C
extended:
  key: value
  name: extended
  version: 1
""";

	static class YAML1 {
		public String format;
		public String name;
		public List<Integer> list;
		public Map<String, String> dict;
		public Object extended;
	}
	
	static class Extended {
		public String key;
		public String name;
		public int version;
	}
	
	@Test
	public void deserialize() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1, true, YAML1.class);
		assertEquals("test", des.name);
		assertEquals(List.of(1, 3, 5), des.list);
		assertEquals(Map.of("a", "A", "b", "B", "c", "C"), des.dict);
	}
	
	@Test
	public void format() throws DruthersLoadException {
		assertTrue(DruthersSerializer.hasFormat(YAML1));
		assertEquals("org.peakaboo.framework.druthers.test/v1", DruthersSerializer.getFormat(YAML1));
	}


	@Test
	public void cast() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1, true, YAML1.class);
		Extended ext = DruthersSerializer.cast(des.extended, Extended.class);
		assertEquals("extended", ext.name);
	}
	
	@Test
	public void serialize() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1, true, YAML1.class);
		String ser = DruthersSerializer.serialize(des);
		assertFalse(ser.contains("!!"));
		ser = DruthersSerializer.serialize(Map.of("key", des));
		assertFalse(ser.contains("!!"));
	}
	
	
	@Test
	public void foreward() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize("newkey: newvalue\n" + YAML1, false, YAML1.class);
		assertEquals("test", des.name);
		assertEquals(List.of(1, 3, 5), des.list);
		assertEquals(Map.of("a", "A", "b", "B", "c", "C"), des.dict);
	}

	@Test
	public void backward() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1.replace("name: test\n", ""), false, YAML1.class);
		assertEquals(List.of(1, 3, 5), des.list);
		assertEquals(Map.of("a", "A", "b", "B", "c", "C"), des.dict);
	}


	// ========================================================================
	// FormatLoader Tests
	// ========================================================================

	@Test
	public void testFormatLoaderSingleMatch() throws DruthersLoadException {
		String yaml = """
			format: org.peakaboo.framework.druthers.test/v1
			name: test
			list:
			  - 1
			  - 3
			  - 5
			""";

		// Track callback execution using array for lambda mutation
		boolean[] called = new boolean[1];
		YAML1[] result = new YAML1[1];

		DruthersSerializer.deserialize(yaml, true,
			new DruthersSerializer.FormatLoader<>(
				"org.peakaboo.framework.druthers.test/v1",
				YAML1.class,
				obj -> {
					called[0] = true;
					result[0] = obj;
				}
			)
		);

		assertTrue("Callback should be invoked for matching format", called[0]);
		assertNotNull("Object should be deserialized", result[0]);
		assertEquals("test", result[0].name);
		assertEquals(List.of(1, 3, 5), result[0].list);
	}

	// Test data classes for FormatLoader multi-version test
	static class FormatV1 {
		public String format;
		public String oldField;
	}

	static class FormatV2 {
		public String format;
		public String newField;
	}

	@Test
	public void testFormatLoaderMultipleFormats() throws DruthersLoadException {
		String yamlV2 = """
			format: org.peakaboo.framework.druthers.test/v2
			newField: modern
			""";

		boolean[] v1Called = new boolean[1];
		boolean[] v2Called = new boolean[1];
		FormatV2[] v2Result = new FormatV2[1];

		DruthersSerializer.deserialize(yamlV2, true,
			new DruthersSerializer.FormatLoader<>("org.peakaboo.framework.druthers.test/v1", FormatV1.class,
				obj -> v1Called[0] = true),
			new DruthersSerializer.FormatLoader<>("org.peakaboo.framework.druthers.test/v2", FormatV2.class,
				obj -> {
					v2Called[0] = true;
					v2Result[0] = obj;
				})
		);

		assertFalse("V1 loader should not be called for V2 document", v1Called[0]);
		assertTrue("V2 loader should be called for V2 document", v2Called[0]);
		assertNotNull("V2 object should be deserialized", v2Result[0]);
		assertEquals("modern", v2Result[0].newField);
	}

	@Test
	public void testFormatLoaderNoMatch() {
		String yaml = """
			format: org.peakaboo.framework.druthers.test/v999
			data: something
			""";

		boolean[] v1Called = new boolean[1];
		boolean[] v2Called = new boolean[1];

		try {
			DruthersSerializer.deserialize(yaml, true,
				new DruthersSerializer.FormatLoader<>("org.peakaboo.framework.druthers.test/v1", Map.class,
					obj -> v1Called[0] = true),
				new DruthersSerializer.FormatLoader<>("org.peakaboo.framework.druthers.test/v2", Map.class,
					obj -> v2Called[0] = true)
			);
			fail("Should throw DruthersLoadException when no format matches");
		} catch (DruthersLoadException e) {
			// Expected exception
			assertFalse("V1 callback should not be invoked", v1Called[0]);
			assertFalse("V2 callback should not be invoked", v2Called[0]);
			assertTrue("Exception message should indicate format mismatch",
				e.getMessage().contains("v999") || e.getMessage().contains("loader"));
		}
	}

	@Test
	public void testFormatLoaderMissingFormat() {
		String yaml = """
			name: test
			value: 42
			""";

		boolean[] called = new boolean[1];

		try {
			DruthersSerializer.deserialize(yaml, true,
				new DruthersSerializer.FormatLoader<>("org.peakaboo.framework.druthers.test/v1", Map.class,
					obj -> called[0] = true)
			);
			fail("Should fail when document has no format field and loader expects one");
		} catch (DruthersLoadException e) {
			// Expected exception
		}

		assertFalse("Callback should not be invoked for format mismatch", called[0]);
	}


}
