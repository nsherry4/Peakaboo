package org.peakaboo.framework.scratch.encoders.serializers;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.single.Compressed;

public class JavaSerializingEncoderTest {

	@Test
	public void testStringEncoding() {
		String original = "hello world";
		ScratchEncoder<String> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		String decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
		assertNotNull(encoded);
		assertTrue(encoded.length > 0);
	}

	@Test
	public void testStringWithCompressed() {
		String s = "hello";
		Compressed<String> c = Compressed.create(s, Serializers.java());
		assertEquals(c.get(), s);
	}
	
	@Test
	public void testIntegerEncoding() {
		Integer original = 42;
		ScratchEncoder<Integer> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		Integer decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	public void testListEncoding() {
		ArrayList<String> original = new ArrayList<>(Arrays.asList("apple", "banana", "cherry"));
		ScratchEncoder<ArrayList<String>> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		ArrayList<String> decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	public void testMapEncoding() {
		HashMap<String, Integer> original = new HashMap<>();
		original.put("one", 1);
		original.put("two", 2);
		original.put("three", 3);
		
		ScratchEncoder<HashMap<String, Integer>> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		HashMap<String, Integer> decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	public void testNullHandling() {
		ScratchEncoder<String> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(null);
		String decoded = encoder.decode(encoded);
		
		assertNull(decoded);
	}
	
	@Test
	public void testEmptyString() {
		String original = "";
		ScratchEncoder<String> encoder = Serializers.java();
		
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
		
		ScratchEncoder<String> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		String decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	public void testUnicodeString() {
		String original = "Hello 世界 🌍 émojis and ñoñó";
		ScratchEncoder<String> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		String decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test(expected = ScratchException.class)
	public void testInvalidData() {
		ScratchEncoder<String> encoder = Serializers.java();
		byte[] invalidData = {1, 2, 3, 4, 5};
		encoder.decode(invalidData);
	}
	
	@Test
	public void testEncoderToString() {
		ScratchEncoder<String> encoder = Serializers.java();
		assertEquals("Java Serializer", encoder.toString());
	}
	
	@Test
	public void testRoundTripConsistency() {
		String original = "test data";
		ScratchEncoder<String> encoder = Serializers.java();
		
		byte[] encoded1 = encoder.encode(original);
		byte[] encoded2 = encoder.encode(original);
		
		assertArrayEquals(encoded1, encoded2);
		
		String decoded1 = encoder.decode(encoded1);
		String decoded2 = encoder.decode(encoded2);
		
		assertEquals(original, decoded1);
		assertEquals(original, decoded2);
		assertEquals(decoded1, decoded2);
	}
	
	public static class TestSerializablePojo implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String name;
		private int value;
		private double score;
		
		public TestSerializablePojo() {}
		
		public TestSerializablePojo(String name, int value, double score) {
			this.name = name;
			this.value = value;
			this.score = score;
		}
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public int getValue() { return value; }
		public void setValue(int value) { this.value = value; }
		public double getScore() { return score; }
		public void setScore(double score) { this.score = score; }
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			TestSerializablePojo that = (TestSerializablePojo) obj;
			return value == that.value && 
				   Double.compare(that.score, score) == 0 &&
				   (name != null ? name.equals(that.name) : that.name == null);
		}
		
		@Override
		public int hashCode() {
			int result = name != null ? name.hashCode() : 0;
			result = 31 * result + value;
			long temp = Double.doubleToLongBits(score);
			result = 31 * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
	}
	
	@Test
	public void testCustomSerializableObjectEncoding() {
		TestSerializablePojo original = new TestSerializablePojo("test", 123, 45.67);
		ScratchEncoder<TestSerializablePojo> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		TestSerializablePojo decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
		assertEquals(original.getName(), decoded.getName());
		assertEquals(original.getValue(), decoded.getValue());
		assertEquals(original.getScore(), decoded.getScore(), 0.001);
	}
	
	@Test
	public void testArrayEncoding() {
		String[] original = {"apple", "banana", "cherry"};
		ScratchEncoder<String[]> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		String[] decoded = encoder.decode(encoded);
		
		assertArrayEquals(original, decoded);
	}
	
	@Test
	public void testComplexNestedStructure() {
		HashMap<String, ArrayList<String>> original = new HashMap<>();
		original.put("fruits", new ArrayList<>(Arrays.asList("apple", "banana")));
		original.put("vegetables", new ArrayList<>(Arrays.asList("carrot", "broccoli")));
		
		ScratchEncoder<HashMap<String, ArrayList<String>>> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		HashMap<String, ArrayList<String>> decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
		assertEquals(original.get("fruits"), decoded.get("fruits"));
		assertEquals(original.get("vegetables"), decoded.get("vegetables"));
	}
	
	@Test(expected = ScratchException.class)
	public void testCorruptedData() {
		ScratchEncoder<String> encoder = Serializers.java();
		
		String original = "test";
		byte[] encoded = encoder.encode(original);
		
		encoded[5] = (byte) (encoded[5] ^ 0xFF);
		
		encoder.decode(encoded);
	}
	
	@Test
	public void testEmptyCollection() {
		ArrayList<String> original = new ArrayList<>();
		ScratchEncoder<ArrayList<String>> encoder = Serializers.java();
		
		byte[] encoded = encoder.encode(original);
		ArrayList<String> decoded = encoder.decode(encoded);
		
		assertEquals(original, decoded);
		assertTrue(decoded.isEmpty());
	}
}