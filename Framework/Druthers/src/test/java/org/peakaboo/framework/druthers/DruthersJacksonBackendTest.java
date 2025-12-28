package org.peakaboo.framework.druthers;

import org.peakaboo.framework.druthers.serialize.DruthersJacksonBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;

/**
 * Tests for the Jackson YAML backend implementation.
 */
public class DruthersJacksonBackendTest extends DruthersBackendCompatibilityTest {

	@Override
	protected DruthersSerializerBackend getBackend() {
		return new DruthersJacksonBackend();
	}

}
