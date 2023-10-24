package org.peakaboo.framework.druthers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

public class DruthersSerializerTests {

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
	
	@DisplayName("Deserialize")
	@Test
	public void deserialize() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1, true, YAML1.class);
		assertEquals(des.name, "test");
		assertEquals(des.list, List.of(1, 3, 5));
		assertEquals(des.dict, Map.of("a", "A", "b", "B", "c", "C"));
	}
	
	@DisplayName("Format")
	@Test
	public void format() throws DruthersLoadException {
		assertTrue(DruthersSerializer.hasFormat(YAML1));
		assertEquals(DruthersSerializer.getFormat(YAML1), "org.peakaboo.framework.druthers.test/v1");
	}
	
	
	@DisplayName("Cast")
	@Test
	public void cast() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1, true, YAML1.class);
		Extended ext = DruthersSerializer.cast(des.extended, Extended.class);
		assertEquals(ext.name, "extended");
	}
	
	@DisplayName("Serialize")
	@Test
	public void serialize() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1, true, YAML1.class);
		String ser = DruthersSerializer.serialize(des);
		assertFalse(ser.contains("!!"));
		ser = DruthersSerializer.serialize(Map.of("key", des));
		assertFalse(ser.contains("!!"));
	}
	
	
	@DisplayName("Forward Compat")
	@Test
	public void foreward() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize("newkey: newvalue\n" + YAML1, false, YAML1.class);
		assertEquals(des.name, "test");
		assertEquals(des.list, List.of(1, 3, 5));
		assertEquals(des.dict, Map.of("a", "A", "b", "B", "c", "C"));
	}
	
	@DisplayName("Backward Compat")
	@Test
	public void backward() throws DruthersLoadException {
		YAML1 des = DruthersSerializer.deserialize(YAML1.replace("name: test\n", ""), false, YAML1.class);
		assertEquals(des.list, List.of(1, 3, 5));
		assertEquals(des.dict, Map.of("a", "A", "b", "B", "c", "C"));
	}
	
	
}
