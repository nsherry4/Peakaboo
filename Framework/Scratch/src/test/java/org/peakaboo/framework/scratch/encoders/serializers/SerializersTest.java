package org.peakaboo.framework.scratch.encoders.serializers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.peakaboo.framework.scratch.ScratchEncoder;

public class SerializersTest {

	@Test
	public void testJavaSerializerFactory() {
		ScratchEncoder<String> encoder = Serializers.java();
		assertNotNull(encoder);
		assertTrue(encoder instanceof JavaSerializingEncoder);
		assertEquals("Java Serializer", encoder.toString());
	}
	
	@Test
	public void testKryoSerializerFactory() {
		ScratchEncoder<String> encoder = Serializers.kryo(String.class);
		assertNotNull(encoder);
		assertTrue(encoder instanceof KryoSerializingEncoder);
		assertEquals("Kryo Serializer", encoder.toString());
	}
	
	@Test
	public void testKryoSerializerWithMultipleClasses() {
		ScratchEncoder<String> encoder = Serializers.kryo(String.class, Integer.class);
		assertNotNull(encoder);
		assertTrue(encoder instanceof KryoSerializingEncoder);
	}
	
	@Test
	public void testFactoryMethodsReturnNewInstances() {
		ScratchEncoder<String> encoder1 = Serializers.java();
		ScratchEncoder<String> encoder2 = Serializers.java();
		assertNotSame("Java factory should return new instances", encoder1, encoder2);
		
		ScratchEncoder<String> kryoEncoder1 = Serializers.kryo(String.class);
		ScratchEncoder<String> kryoEncoder2 = Serializers.kryo(String.class);
		assertNotSame("Kryo factory should return new instances", kryoEncoder1, kryoEncoder2);
	}
}