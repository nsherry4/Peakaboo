package org.peakaboo.framework.druthers;

import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSnakeYamlBackend;

/**
 * Tests for the SnakeYAML backend implementation.
 */
public class DruthersSnakeYamlBackendTest extends DruthersBackendCompatibilityTest {

	@Override
	protected DruthersSerializerBackend getBackend() {
		return new DruthersSnakeYamlBackend();
	}

}
