package org.peakaboo.framework.scratch;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;
import org.peakaboo.framework.scratch.single.Compressed;

public class KryoTest {

	
	@Test
	public void string() {
		String s = "hello";
		Compressed<String> c = Compressed.create(s, Serializers.kryo(String.class));
		Assert.assertEquals(c.get(), s);
	}
	
}
