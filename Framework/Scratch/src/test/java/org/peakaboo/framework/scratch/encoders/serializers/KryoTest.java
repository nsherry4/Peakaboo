package org.peakaboo.framework.scratch.encoders.serializers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.single.Compressed;

import com.esotericsoftware.kryo.serializers.FieldSerializer;

public class KryoTest {

	@Test
	public void testStringEncoding() {
		String original = "hello world";
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		
		byte[] encoded = encoder.encode(original);
		String decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
		assertNotNull(encoded);
		assertTrue(encoded.length > 0);
	}

	@Test
	public void testStringWithCompressed() {
		String s = "hello";
		Compressed<String> c = Compressed.create(s, Serializers.kryo(String.class));
		assertEquals(c.get(), s);
	}
	
	@Test
	public void testIntegerEncoding() {
		Integer original = 42;
		ScratchEncoder<Integer> encoder = Serializers.kryo(Integer.class);
		
		byte[] encoded = encoder.encode(original);
		Integer decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void testListEncoding() {
		ArrayList<String> original = new ArrayList<>(Arrays.asList("apple", "banana", "cherry"));
		ScratchEncoder encoder = Serializers.kryo(ArrayList.class, String.class);
		
		byte[] encoded = encoder.encode(original);
		ArrayList<String> decoded = (ArrayList<String>) encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	public void testMapEncoding() {
		Map<String, Integer> original = new HashMap<>();
		original.put("one", 1);
		original.put("two", 2);
		original.put("three", 3);
		
		ScratchEncoder<Map> encoder = Serializers.kryo(HashMap.class, String.class, Integer.class);
		
		byte[] encoded = encoder.encode(original);
		Map<String, Integer> decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullHandling() {
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		encoder.encode(null);
	}
	
	@Test
	public void testEmptyString() {
		String original = "";
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		
		byte[] encoded = encoder.encode(original);
		String decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	public void testLargeString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			sb.append("This is a test string with some content. ");
		}
		String original = sb.toString();
		
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		
		byte[] encoded = encoder.encode(original);
		String decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	public void testUnicodeString() {
		String original = "Hello 世界 🌍 émojis and ñoñó";
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		
		byte[] encoded = encoder.encode(original);
		String decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test(expected = ScratchException.class)
	public void testInvalidData() {
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		byte[] invalidData = {1, 2, 3, 4, 5};
		encoder.decode(invalidData);
	}
	
	@Test
	public void testEncoderToString() {
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		assertEquals("Kryo Serializer", encoder.toString());
	}
	
	@Test
	public void testRoundTripConsistency() {
		String original = "test data";
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		
		byte[] encoded1 = encoder.encode(original);
		byte[] encoded2 = encoder.encode(original);
		
		assertArrayEquals(encoded1, encoded2);
		
		String decoded1 = encoder.decode(encoded1);
		String decoded2 = encoder.decode(encoded2);
		
		assertEquals(original, decoded1);
		assertEquals(original, decoded2);
		assertEquals(decoded1, decoded2);
	}
	
	public static class TestPojo {
		private String name;
		private int value;
		
		public TestPojo() {}
		
		public TestPojo(String name, int value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public int getValue() { return value; }
		public void setValue(int value) { this.value = value; }
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			TestPojo testPojo = (TestPojo) obj;
			return value == testPojo.value && 
				   (name != null ? name.equals(testPojo.name) : testPojo.name == null);
		}
		
		@Override
		public int hashCode() {
			return (name != null ? name.hashCode() : 0) * 31 + value;
		}
	}
	
	@Test
	public void testCustomObjectEncoding() {
		TestPojo original = new TestPojo("test", 123);
		ScratchEncoder<TestPojo> encoder = Serializers.kryo(TestPojo.class);
		
		byte[] encoded = encoder.encode(original);
		TestPojo decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
		assertEquals(original.getName(), decoded.getName());
		assertEquals(original.getValue(), decoded.getValue());
	}
}
