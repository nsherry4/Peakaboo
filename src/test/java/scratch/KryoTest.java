package scratch;

import org.junit.Test;
import org.junit.Assert;

import scratch.encoders.serializers.Serializers;
import scratch.single.Compressed;

public class KryoTest {

	
	@Test
	public void string() {
		String s = "hello";
		Compressed<String> c = Compressed.create(s, Serializers.kryo(String.class));
		Assert.assertEquals(c.get(), s);
	}
	
}
