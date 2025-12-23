package org.peakaboo.framework.druthers.serialize;

import java.util.List;

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
			.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		// Configure strict vs non-strict mode
		if (strict) {
			builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			builder.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
		} else {
			builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			builder.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
		}

		// Important: Don't use default typing (prevents @class/@type annotations)
		// Build and return the mapper
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
