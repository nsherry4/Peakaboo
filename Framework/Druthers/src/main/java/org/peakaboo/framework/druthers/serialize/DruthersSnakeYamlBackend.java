package org.peakaboo.framework.druthers.serialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * SnakeYAML-based implementation of DruthersSerializerBackend.
 * <p>
 * This implementation uses SnakeYAML 2.x for YAML serialization and deserialization.
 * It is configured to produce clean, readable YAML without Java class tags or
 * unnecessary anchors.
 */
public class DruthersSnakeYamlBackend implements DruthersSerializerBackend {

	/**
	 * Custom SnakeYAML representer with Druthers-specific behaviour.
	 * <ul>
	 *   <li>Prevents anchors/aliases for empty maps (reduces clutter)</li>
	 *   <li>Forces Tag.MAP representation for JavaBeans (cleaner output)</li>
	 * </ul>
	 */
	private static class DruthersYamlRepresenter extends Representer {

		public DruthersYamlRepresenter(DumperOptions options) {
			super(options);

			// We do this to prevent SnakeYAML from adding anchors and aliases for empty
			// maps. From what I understand, SnakeYAML stores refs for maps to prevent
			// infinite loops whilst serialising, but empty maps shouldn't have that problem.
			this.multiRepresenters.put(Map.class, new RepresentMap() {

				@Override
				public Node representData(Object data) {
					// Make the representation by calling super's method
					Node n = super.representData(data);

					// If this data really is a map, and the map is empty, then remove this object
					// from the inventory of already-represented objects
					if (data instanceof Map<?,?> mapdata && mapdata.isEmpty()) {
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

	/**
	 * Shallow HashMap for extracting the format field.
	 */
	private static class Shallow extends HashMap<String, String> {}

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

		// Build loader and deserialize
		Yaml y = buildLoader(cls, strict);
		try {
			return y.load(yaml);
		} catch (YAMLException e) {
			throw new DruthersLoadException(e);
		}
	}

	@Override
	public String serialize(Object toSerialize) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		var rep = new DruthersYamlRepresenter(options);
		Yaml y = new Yaml(rep);

		return y.dumpAs(toSerialize, Tag.MAP, FlowStyle.BLOCK);
	}

	@Override
	public <T> String serializeList(List<T> toSerialize) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.FLOW);
		var rep = new DruthersYamlRepresenter(options);
		Yaml y = new Yaml(rep);

		return y.dump(toSerialize);
	}

	/**
	 * Builds a SnakeYAML loader configured for the target class and mode.
	 * <p>
	 * <b>Security:</b> Configured to prevent common YAML deserialization attacks:
	 * <ul>
	 *   <li>Blocks all explicit type tags to prevent arbitrary code execution</li>
	 *   <li>Limits nesting depth to prevent stack overflow attacks</li>
	 *   <li>Limits aliases to prevent billion laughs attacks</li>
	 *   <li>Limits document size to prevent memory exhaustion</li>
	 * </ul>
	 */
	private <T> Yaml buildLoader(Class<T> cls, boolean strict) {
		var loaderopts = new LoaderOptions();

		// SECURITY: Reject ALL explicit type tags (!!java.*, !!javax.*, etc.)
		// Only allow implicit YAML tags (maps, sequences, scalars)
		loaderopts.setTagInspector(tag -> tag.startsWith(Tag.PREFIX));

		// SECURITY: Set resource limits to prevent DoS attacks
		loaderopts.setMaxAliasesForCollections(50);      // Prevent billion laughs attack
		loaderopts.setNestingDepthLimit(50);             // Prevent stack overflow
		loaderopts.setCodePointLimit(50 * 1024 * 1024); // 50MB document size limit

		var constructor = new Constructor(cls, loaderopts);
		constructor.getPropertyUtils().setSkipMissingProperties(!strict);

		var dumperopts = new DumperOptions();
		var representer = new DruthersYamlRepresenter(dumperopts);
		representer.getPropertyUtils().setSkipMissingProperties(!strict);

		return new Yaml(constructor, representer);
	}

	/**
	 * Extracts the format field from a YAML document.
	 * Returns null if the format field is not present or cannot be read.
	 */
	private String getFormat(String yaml) {
		Shallow shallow;
		try {
			Yaml y = buildLoader(Shallow.class, false);
			shallow = y.load(yaml);
		} catch (YAMLException e) {
			// If we can't read it, it might as well not have a format string
			return null;
		}
		String format = shallow.getOrDefault("format", null);
		if (format != null) {
			format = format.trim();
		}
		return format;
	}

}
