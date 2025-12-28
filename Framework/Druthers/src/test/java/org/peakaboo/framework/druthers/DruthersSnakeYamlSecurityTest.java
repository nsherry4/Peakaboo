package org.peakaboo.framework.druthers;

import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSnakeYamlBackend;

/**
 * Security tests for the SnakeYAML backend implementation.
 * <p>
 * Inherits all security tests from {@link DruthersSecurityTest} and runs them
 * against the SnakeYAML backend. These tests verify that the tag inspector and
 * resource limits configured in {@link DruthersSnakeYamlBackend#buildLoader}
 * properly prevent RCE and DoS attacks.
 */
public class DruthersSnakeYamlSecurityTest extends DruthersSecurityTest {

	@Override
	protected DruthersSerializerBackend getBackend() {
		return new DruthersSnakeYamlBackend();
	}

}
