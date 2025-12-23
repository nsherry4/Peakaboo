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
 * <p>
 * See BACKEND_COMPATIBILITY.md for detailed requirements and testing strategy.
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
