package org.peakaboo.framework.druthers.serialize;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

/**
 * Jackson YAML-based implementation of DruthersSerializerBackend.
 * <p>
 * This implementation uses Jackson YAML for serialization and deserialization,
 * primarily for Android compatibility where SnakeYAML is not available.
 * It is configured to produce clean, readable YAML without type information
 * or unnecessary anchors, matching the output characteristics of SnakeYAML.
 */
public class DruthersJacksonBackend implements DruthersSerializerBackend {

	@Override
	public <T> T deserialize(String yaml, boolean strict, String format, Class<T> cls)
			throws DruthersLoadException {

		// Validate format if required
		if (format != null) {
			String reportedFormat = getFormat(yaml);
			if (strict && reportedFormat == null) {
				throw new DruthersLoadException("YAML document contained no format field");
			} else if (reportedFormat != null && !format.equals(reportedFormat)) {
				throw new DruthersLoadException(
					"YAML document format mismatch: Expected '" + format + "', found '" + reportedFormat + "'"
				);
			}
		}

		// Build mapper and deserialize
		ObjectMapper mapper = buildMapper(strict);
		try {
			return mapper.readValue(yaml, cls);
		} catch (JacksonException e) {
			throw new DruthersLoadException(e);
		}
	}

	@Override
	public String serialize(Object toSerialize) {
		ObjectMapper mapper = buildMapper(false);
		try {
			return mapper.writeValueAsString(toSerialize);
		} catch (JacksonException e) {
			throw new RuntimeException("Failed to serialize object", e);
		}
	}

	@Override
	public <T> String serializeList(List<T> toSerialize) {
		// Jackson doesn't have separate flow/block style configuration for lists vs objects
		// like SnakeYAML does, but it will produce reasonable compact output for lists
		ObjectMapper mapper = buildMapper(false);
		try {
			return mapper.writeValueAsString(toSerialize);
		} catch (JacksonException e) {
			throw new RuntimeException("Failed to serialize list", e);
		}
	}

	/**
	 * Builds a Jackson ObjectMapper configured for Druthers requirements.
	 * <p>
	 * Configured to match SnakeYAML behaviour by using direct field access
	 * instead of getters/setters. This ensures compatibility when deserializing
	 * YAML that was serialized by SnakeYAML.
	 */
	private ObjectMapper buildMapper(boolean strict) {
		YAMLFactory yamlFactory = YAMLFactory.builder()
			// Disable features that would add clutter
			.disable(YAMLWriteFeature.WRITE_DOC_START_MARKER)  // Don't write "---"
			.disable(YAMLWriteFeature.USE_NATIVE_TYPE_ID)       // Don't write type tags
			// Use literal block style (|) for multiline strings to match SnakeYAML format
			.enable(YAMLWriteFeature.LITERAL_BLOCK_STYLE)
			.build();

		var builder = YAMLMapper.builder(yamlFactory)
			// Disable serialization issues with empty beans
			.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
			// COMPATIBILITY: Match SnakeYAML's PropertyUtils default behaviour
			//
			// SnakeYAML uses JavaBean introspection which serializes:
			// 1. Public fields via direct access
			// 2. JavaBean properties via public getter/setter pairs
			// 3. When both exist, getters/setters take precedence over fields
			//
			// Configure Jackson to replicate this behaviour:
			.changeDefaultVisibility(vc -> vc
				// PropertyAccessor.FIELD with PUBLIC_ONLY visibility:
				// - Enables serialization/deserialization of public fields via direct access
				// - Fields without getters/setters will be accessed directly
				// - When a getter/setter exists for a field, the getter/setter takes precedence (see below)
				.withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)

				// PropertyAccessor.GETTER with PUBLIC_ONLY visibility:
				// - Enables serialization via public getXxx() methods
				// - Takes precedence over FIELD access when both a public field and public getter exist
				// - Allows getters to return transformed/wrapped values different from the raw field type
				//   (e.g., returning a DTO wrapper around an interface/abstract field)
				.withVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)

				// PropertyAccessor.SETTER with PUBLIC_ONLY visibility:
				// - Enables deserialization via public setXxx() methods
				// - Pairs with GETTER - when a getter exists, Jackson uses the matching setter for deserialization
				// - Allows setters to accept transformed/wrapped values and convert them to internal types
				//   (e.g., accepting a DTO wrapper and extracting the actual object)
				.withVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)

				// PropertyAccessor.CREATOR with NONE visibility:
				// - Disables constructor-based deserialization
				// - Jackson will use the default no-arg constructor and then set fields/properties
				// - Matches SnakeYAML's approach which doesn't use constructor parameters for deserialization
				.withVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE)

				// PropertyAccessor.IS_GETTER with NONE visibility:
				// - Disables special handling of isXxx() boolean getters
				// - Only standard getXxx() methods will be used as getters
				// - Simplifies behaviour and matches SnakeYAML's PropertyUtils defaults
				.withVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE));

		// Configure strict vs non-strict mode
		if (strict) {
			builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			builder.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
		} else {
			builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			builder.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
		}

		// Important: Don't use default typing (prevents @class/@type annotations)
		return builder.build();
	}

	/**
	 * Extracts the format field from a YAML document.
	 * Returns null if the format field is not present or cannot be read.
	 */
	private String getFormat(String yaml) {
		try {
			ObjectMapper mapper = buildMapper(false);
			// Use tree model to extract just the format field
			var rootNode = mapper.readTree(yaml);
			if (rootNode.has("format")) {
				String format = rootNode.get("format").asText();
				if (format != null) {
					format = format.trim();
				}
				return format;
			}
			return null;
		} catch (JacksonException e) {
			// If we can't read it, it might as well not have a format string
			return null;
		}
	}

}
