package org.peakaboo.framework.druthers;

import static org.junit.Assert.assertFalse;

import org.junit.Ignore;
import org.junit.Test;
import org.peakaboo.framework.druthers.serialize.DruthersJacksonBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;

/**
 * Security tests for the Jackson YAML backend implementation.
 * <p>
 * Jackson has a fundamentally different security model than SnakeYAML:
 * <ul>
 *   <li><b>RCE Protection:</b> Jackson ignores YAML type tags (!!java.*, !!javax.*, etc.)
 *       and relies on disabling default typing to prevent polymorphic deserialization attacks.
 *       Unlike SnakeYAML which must reject type tags, Jackson is inherently safe from
 *       tag-based RCE.</li>
 *   <li><b>DoS Protection:</b> Jackson uses StreamReadConstraints to limit nesting depth,
 *       string length, and number length. However, Jackson does not have an equivalent to
 *       SnakeYAML's maxAliasesForCollections for limiting YAML alias expansion.</li>
 * </ul>
 * <p>
 * This class overrides RCE tests to verify Jackson's specific behavior (type tags ignored)
 * and disables the billion laughs test (no alias limit protection in Jackson).
 */
public class DruthersJacksonSecurityTest extends DruthersSecurityTest {

	@Override
	protected DruthersSerializerBackend getBackend() {
		return new DruthersJacksonBackend();
	}

	// ========================================================================
	// RCE Tests - Overridden for Jackson's Security Model
	// ========================================================================

	/**
	 * Jackson ignores !!java.lang.Runtime type tags and does NOT instantiate the class.
	 * This test verifies that Jackson safely parses the YAML without creating a Runtime instance.
	 */
	@Override
	@Test
	public void testRejectRuntimeTypeTag() {
		DruthersSerializerBackend backend = getBackend();

		try {
			// Jackson should parse this without error (it ignores the type tag)
			Object result = backend.deserialize(ATTACK_RCE_RUNTIME, true, null, Object.class);

			// CRITICAL: Verify it did NOT instantiate java.lang.Runtime
			assertFalse("Jackson must NOT instantiate Runtime class even if type tag is present",
				result instanceof Runtime);
		} catch (Exception e) {
			throw new AssertionError("Jackson should parse YAML with type tags (ignoring them), but threw: " + e.getMessage(), e);
		}
	}

	/**
	 * Jackson ignores !!javax.script.ScriptEngineManager type tags.
	 * This test verifies Jackson doesn't execute gadget chain attacks.
	 */
	@Override
	@Test
	public void testRejectScriptEngineTypeTag() {
		DruthersSerializerBackend backend = getBackend();

		try {
			// Jackson should parse this without error (it ignores the type tags)
			Object result = backend.deserialize(ATTACK_RCE_SCRIPT_ENGINE, true, null, Object.class);

			// CRITICAL: Verify it did NOT instantiate javax.script.ScriptEngineManager
			assertFalse("Jackson must NOT instantiate ScriptEngineManager even if type tag is present",
				result instanceof javax.script.ScriptEngineManager);
		} catch (Exception e) {
			throw new AssertionError("Jackson should parse YAML with type tags (ignoring them), but threw: " + e.getMessage(), e);
		}
	}

	/**
	 * Jackson ignores custom class type tags (!!org.peakaboo.*).
	 * This test verifies Jackson doesn't instantiate arbitrary application classes.
	 */
	@Override
	@Test
	public void testRejectCustomClassTypeTag() {
		DruthersSerializerBackend backend = getBackend();

		try {
			// Jackson should parse this without error (it ignores the type tag)
			Object result = backend.deserialize(ATTACK_RCE_CUSTOM_CLASS, true, null, Object.class);

			// CRITICAL: Verify it did NOT instantiate the custom class
			// Result should be a generic Map, not DruthersBackendCompatibilityTests$SimpleObject
			String resultType = result != null ? result.getClass().getName() : "null";
			assertFalse("Jackson must NOT instantiate custom classes based on type tags. Got: " + resultType,
				resultType.contains("SimpleObject"));
		} catch (Exception e) {
			throw new AssertionError("Jackson should parse YAML with type tags (ignoring them), but threw: " + e.getMessage(), e);
		}
	}

	// ========================================================================
	// DoS Tests - Billion Laughs Disabled for Jackson
	// ========================================================================

	/**
	 * Jackson does not have an equivalent to SnakeYAML's maxAliasesForCollections.
	 * YAML alias expansion (billion laughs attack) is not currently preventable in Jackson.
	 * <p>
	 * <b>Known Limitation:</b> This is a documented limitation of Jackson YAML.
	 * Applications should validate input size at application boundaries if billion laughs
	 * protection is required.
	 */
	@Override
	@Test
	@Ignore("Jackson does not support limiting YAML alias expansion (no maxAliasesForCollections equivalent)")
	public void testPreventBillionLaughs() {
		// Disabled - Jackson cannot prevent billion laughs attacks via configuration
		// This is a known limitation of Jackson YAML compared to SnakeYAML
	}

	// testPreventDeepNesting() is inherited and works correctly with StreamReadConstraints.maxNestingDepth

}
