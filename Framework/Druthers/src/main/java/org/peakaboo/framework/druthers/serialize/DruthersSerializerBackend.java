package org.peakaboo.framework.druthers.serialize;

import java.util.List;

/**
 * Platform-specific YAML serialization backend for Druthers.
 * <p>
 * Implementations provide the core serialization and deserialization functionality
 * for different platforms (e.g., SnakeYAML for desktop/server, Jackson YAML for Android).
 * <p>
 * All implementations must produce clean, readable YAML that:
 * <ul>
 *   <li>Contains no Java class type tags (no !!java.* or !!org.peakaboo.*)</li>
 *   <li>Uses no anchors/aliases for empty collections</li>
 *   <li>Represents JavaBeans as maps rather than tagged objects</li>
 *   <li>Can be read by any other backend implementation</li>
 * </ul>
 *
 * <h2>JavaBean Serialization Expectations</h2>
 * <p>
 * Backends must follow this visibility-based strategy for cross-compatibility:
 * <ul>
 *   <li><b>Public fields (no getters/setters):</b> Use direct field access</li>
 *   <li><b>Package-private/private fields (with public getters/setters):</b> Use getter/setter methods</li>
 *   <li><b>Public fields WITH public getters/setters:</b> AMBIGUOUS - behavior differs between backends.
 *       SnakeYAML uses field access, Jackson prefers getters/setters. This pattern should be
 *       AVOIDED in Druthers-serialized classes.</li>
 * </ul>
 * <p>
 * The Peakaboo codebase uses two patterns: (1) public fields without accessors, or
 * (2) package-private fields with public accessors. The ambiguous case does not occur
 * and should be avoided.
 * <p>
 */
public interface DruthersSerializerBackend {

	/**
	 * Deserializes a YAML string to a specific class.
	 *
	 * @param <T> the target type
	 * @param yaml the YAML string to deserialize
	 * @param strict if true, missing or extra fields cause errors; if false, they are ignored
	 * @param format expected format string for validation (null to skip format validation)
	 * @param cls the target class to deserialize into
	 * @return the deserialized object
	 * @throws DruthersLoadException if deserialization fails or format validation fails
	 */
	<T> T deserialize(String yaml, boolean strict, String format, Class<T> cls)
		throws DruthersLoadException;

	/**
	 * Serializes an object to YAML.
	 * <p>
	 * Output should use block style formatting for readability.
	 * Must not include Java class type information.
	 *
	 * @param toSerialize the object to serialize
	 * @return YAML string representation
	 */
	String serialize(Object toSerialize);

	/**
	 * Serializes a list to YAML.
	 * <p>
	 * Output should use flow style formatting (compact inline format).
	 * Must not include Java class type information.
	 *
	 * @param <T> the element type
	 * @param toSerialize the list to serialize
	 * @return YAML string representation
	 */
	<T> String serializeList(List<T> toSerialize);

}
