package org.peakaboo.framework.druthers.serialize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.peakaboo.framework.accent.Platform;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Platform-independent YAML serialization facade for Druthers.
 * <p>
 * This class automatically selects the appropriate YAML backend based on the
 * runtime platform:
 * <ul>
 *   <li>Android: Jackson YAML (SnakeYAML not available on Android)</li>
 *   <li>Other platforms: SnakeYAML</li>
 * </ul>
 * <p>
 * All public methods delegate to the selected backend, ensuring consistent
 * behaviour across platforms whilst accommodating platform-specific constraints.
 */
public class DruthersSerializer {

	/** The active serialization backend */
	private static DruthersSerializerBackend backend;

	/** Initialization guard */
	private static boolean initialized = false;

	/**
	 * Lazy initialization of the serialization backend based on platform detection.
	 */
	private static synchronized void initialize() {
		if (initialized) return;

		if (Platform.getOS() == Platform.OS.ANDROID) {
			backend = new DruthersJacksonBackend();
		} else {
			backend = new DruthersSnakeYamlBackend();
		}

		initialized = true;
	}

	/**
	 * Gets the active backend, initialising if necessary.
	 */
	private static DruthersSerializerBackend getBackend() {
		if (!initialized) {
			initialize();
		}
		return backend;
	}


	// ========================================================================
	// Public API - Deserialization
	// ========================================================================

	/**
	 * Decodes a yaml document to a specific class.
	 */
	@Deprecated(since="6", forRemoval = true)
	public static <T extends Object> T deserialize(String yaml, Class<T> cls) throws DruthersLoadException {
		return deserialize(yaml, false, cls);
	}


	/**
	 * Decodes a yaml document to a specific class. Useful for reading yaml documents without !! java class hints
	 */
	@Deprecated(since="6", forRemoval = true)
	public static <T extends Object> T deserialize(File file, Class<T> cls) throws IOException, DruthersLoadException {
		return deserialize(new String(Files.readAllBytes(file.toPath())), cls);
	}


	/**
	 * Decodes a yaml document to a specific class. In strict mode:<br/>
	 * <br/>
	 *
	 * <ul>
	 * <li>!!org.peakaboo class handles (from Peakaboo 5's files) will cause an
	 * error.</li>
	 * <li>Missing entries will be skipped.</li>
	 * </ul>
	 * As a general rule, if the app (or plugin) provides the file then use strict
	 * mode. If the user supplies the file, strongly consider non-strict mode.
	 */
	public static <T extends Object> T deserialize(String yaml, boolean strict, Class<T> cls) throws DruthersLoadException {
		return deserialize(yaml, strict, null, cls);
	}

	/**
	 * Decodes a yaml document to a specific class. In strict mode:<br/>
	 * <br/>
	 *
	 * <ul>
	 * <li>!!org.peakaboo class handles (from Peakaboo 5's files) will cause an
	 * error.</li>
	 * <li>Missing entries will be skipped.</li>
	 * </ul>
	 * As a general rule, if the app (or plugin) provides the file then use strict
	 * mode. If the user supplies the file, strongly consider non-strict mode.
	 * <br/><br/>
	 * When given a format String, the "format" field will be compared during loading.
	 */
	public static <T extends Object> T deserialize(String yaml, boolean strict, String format, Class<T> cls) throws DruthersLoadException {
		yaml = deserializeChecks(yaml, strict, format);
		return getBackend().deserialize(yaml, strict, format, cls);
	}

	/**
	 * Format loader record for multi-version deserialization strategies.
	 */
	public record FormatLoader<T> (String format, Class<T> cls, Consumer<T> callback) {
		void load(String yaml, boolean strict) throws DruthersLoadException {
			T loaded = deserialize(yaml, strict, format, cls);
			callback.accept(loaded);
		}
	}

	/**
	 * Accepts yaml and a set of {@link FormatLoader}s which provide strategies for
	 * handling yaml with different `format` IDs. Attempts to match the document's
	 * `format` to a FormatLoader and implement it's strategy. Each strategy may
	 * have a different deserialized type, so instead of returning it, each loader
	 * also supplies a callback {@link Consumer} which will be run on a successful
	 * load. If there is no successful strategy, a DruthersLoadException is thrown.
	 */
	public static void deserialize(String yaml, boolean strict, FormatLoader<?>... formats) throws DruthersLoadException {

		//Figure out the format in this file
		String format = "";
		if (hasFormat(yaml)) {
			format = getFormat(yaml);
		}

		//Use the format to pick a loader
		FormatLoader<?> loader = null;
		for (var l : formats) {
			if (format.equals(l.format)) {
				loader = l;
				break;
			}
		}

		//Fail if there's no loader
		if (loader == null) {
			throw new DruthersLoadException("Failed to deserialize, could not find loader for '" + format + "'");
		}

		//Remove some v5 !! artifacts and perform some checks
		yaml = deserializeChecks(yaml, strict, format);

		//Get the loader to try loading the file and running its callback
		loader.load(yaml, strict);

		//Success
	}


	// ========================================================================
	// Public API - Serialization
	// ========================================================================

	/**
	 * Encodes the serialized data as yaml
	 */
	public static String serialize(Object toSerialize) {
		return getBackend().serialize(toSerialize);
	}

	/**
	 * Encodes the list as yaml
	 */
	public static <T> String serializeList(List<T> toSerialize) {
		return getBackend().serializeList(toSerialize);
	}


	// ========================================================================
	// Public API - Utilities
	// ========================================================================

	/**
	 * Reinterpret a section of a data structure as a different class by serializing
	 * the given object and deserializing as the given class.
	 */
	public static <T> T cast(Object generic, Class<T> cls) throws DruthersLoadException {
		return deserialize(serialize(generic), false, cls);
	}

	/**
	 * Shallow HashMap for extracting the format field.
	 * Uses Object values to handle nested structures that may exist in YAML.
	 */
	private static class Shallow extends HashMap<String, Object> {}

	/**
	 * Extracts the format field from a YAML document.
	 */
	public static String getFormat(String yaml) {
		Shallow shallow;
		try {
			shallow = deserialize(yaml, false, null, Shallow.class);
		} catch (DruthersLoadException e) {
			// If we can't read it, it might as well not have a format string
			return null;
		}
		Object formatObj = shallow.getOrDefault("format", null);
		if (formatObj == null) {
			return null;
		}
		String format = formatObj.toString().trim();
		return format;
	}

	/**
	 * Checks if a YAML document contains a format field.
	 */
	public static boolean hasFormat(String yaml) {
		return getFormat(yaml) != null;
	}


	// ========================================================================
	// Backward Compatibility - SnakeYAML-specific API
	// ========================================================================

	/**
	 * Builds a SnakeYAML Yaml loader configured for the target class and mode.
	 * <p>
	 * <b>Note:</b> This method is SnakeYAML-specific and exists for backward
	 * compatibility. It will throw UnsupportedOperationException on Android.
	 * New code should use the deserialize() methods instead.
	 *
	 * @throws UnsupportedOperationException on Android where SnakeYAML is not available
	 */
	public static <T extends Object> Yaml buildLoader(Class<T> cls, boolean strict) {
		if (Platform.getOS() == Platform.OS.ANDROID) {
			throw new UnsupportedOperationException(
				"buildLoader() is not supported on Android. Use deserialize() methods instead."
			);
		}

		// This is SnakeYAML-specific code for backward compatibility
		var loaderopts = new LoaderOptions();
		var constructor = new Constructor(cls, loaderopts);
		constructor.getPropertyUtils().setSkipMissingProperties(!strict);

		var dumperopts = new DumperOptions();
		var representer = new SnakeYamlRepresenter(dumperopts);
		representer.getPropertyUtils().setSkipMissingProperties(!strict);

		return new Yaml(constructor, representer);
	}

	/**
	 * SnakeYAML representer for backward compatibility with buildLoader().
	 * This is the same implementation that was previously DruthersYamlRepresenter.
	 */
	private static class SnakeYamlRepresenter extends Representer {

		public SnakeYamlRepresenter(DumperOptions options) {
			super(options);

			// Prevent SnakeYAML from adding anchors and aliases for empty maps
			this.multiRepresenters.put(java.util.Map.class, new RepresentMap() {

				@Override
				public org.yaml.snakeyaml.nodes.Node representData(Object data) {
					// Make the representation by calling super's method
					org.yaml.snakeyaml.nodes.Node n = super.representData(data);

					// If this data really is a map, and the map is empty, then remove this object
					// from the inventory of already-represented objects
					if (data instanceof java.util.Map<?,?> mapdata && mapdata.isEmpty()) {
						representedObjects.remove(data);
					}

					// Return our new node
					return n;
				 }
			});
		}

		@Override
		protected MappingNode representJavaBean(Set<Property> props, Object bean) {
			// If we don't have a preferred form for a class set, set it to MAP now.
			var cls = bean.getClass();
			if (!classTags.containsKey(cls)) {
				addClassTag(bean.getClass(), Tag.MAP);
			}
			return super.representJavaBean(props, bean);
		}
	}


	// ========================================================================
	// Internal Helpers
	// ========================================================================

	/**
	 * Performs pre-deserialization checks and transformations.
	 * <ul>
	 *   <li>In non-strict mode: filters out old !!org.peakaboo class tags (Peakaboo 5 compatibility)</li>
	 *   <li>If format is specified: validates the format field matches</li>
	 * </ul>
	 */
	private static String deserializeChecks(String yaml, boolean strict, String format) throws DruthersLoadException {

		if (!strict) {
			// Remove old !!org.peakaboo.Class identifiers, these are no longer accepted by the parser
			yaml = yaml.lines().filter(l -> !l.startsWith("!!org.peakaboo")).collect(Collectors.joining("\n"));
		}

		if (format != null) {
			String reportedFormat = getFormat(yaml);
			if (strict && reportedFormat == null) {
				throw new DruthersLoadException("YAML document contained no format field");
			} else if (reportedFormat != null && !format.equals(reportedFormat)) {
				throw new DruthersLoadException("YAML document format mismatch: Expected '" + format + "', found '" + reportedFormat + "'");
			}
		}

		return yaml;
	}

}
