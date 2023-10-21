package org.peakaboo.framework.druthers.serialize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

public class DruthersSerializer {

	private static class DruthersYamlRepresenter extends Representer {

		public DruthersYamlRepresenter(DumperOptions options) {
			super(options);
			
			// We do this to prevent SnakeYAML from adding anchors and aliases for empty
			// maps. From what I understand, SnakeYAML stores refs for maps to prevent
			// infinite loops while serializing, but empty maps shouldn't have that problem.
			this.multiRepresenters.put(Map.class, new RepresentMap() {
				public Node representData(Object data) {
					//Make the representation by calling super's mehtod
					Node n = super.representData(data);
					
					// If this data really is a map, and the map is empty, then remove this object
					// from the inventory of already-represented objects
					if (data instanceof Map mapdata) {
						if (mapdata.isEmpty()) {
							representedObjects.remove(data);
						}
					}
					
					//Return our new node
					return n;
				 }
			});
			
		}

		
	    @Override
	    protected MappingNode representJavaBean(Set<Property> props, Object bean) {
	        //If we don't have a preferred form for a class set, set it to MAP now. 
	    	var cls = bean.getClass();
	    	if (!classTags.containsKey(cls)) {
	            addClassTag(bean.getClass(), Tag.MAP);
	        }
	        MappingNode mapping = super.representJavaBean(props, bean);
	        return mapping;
	    }
		
		
	}
	
	
	/**
	 * Decodes a yaml document to a specific class. 
	 */
	@Deprecated
	public static <T extends Object> T deserialize(String yaml, Class<T> cls) throws DruthersLoadException {
		return deserialize(yaml, false, cls);
	}
	
	
	/**
	 * Decodes a yaml document to a specific class. Useful for reading yaml documents without !! java class hints 
	 */
	@Deprecated
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
		return deserialize(yaml, buildLoader(cls, strict));
	}
	
	public record FormatLoader<T> (String format, Class<T> cls, Consumer<T> callback) {
		void load(String yaml, boolean strict) throws DruthersLoadException {
			T loaded = deserialize(yaml, strict, format, cls);
			callback.accept(loaded);
		}
	};
	
	public static boolean deserialize(String yaml, boolean strict, List<FormatLoader<?>> formats) throws DruthersLoadException {
		
		//Figure out the format in this file
		String format = null;
		if (hasFormat(yaml)) {
			format = getFormat(yaml);
		} else {
			format = "";
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
			return false;
		}
		
		//Remove some v5 !! artifacts and perform some checks
		yaml = deserializeChecks(yaml, strict, format);
		
		//Get the loader to try loading the file and running its callback
		loader.load(yaml, strict);
		
		//Success
		return true;
	}
	
	private static <T extends Object> T deserialize(String yaml, Yaml y) throws DruthersLoadException {
		try {
			T loaded = y.load(yaml);
			return loaded;
		} catch (YAMLException e) {
			throw new DruthersLoadException(e);
		}
	}

	
	public static <T extends Object> Yaml buildLoader(Class<T> cls, boolean strict) {
		var loaderopts = new LoaderOptions();
		var constructor = new Constructor(cls, loaderopts);
		constructor.getPropertyUtils().setSkipMissingProperties(strict);
		Yaml y = new Yaml(constructor);
		return y;
	}
	

	
	
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
	
	
	
	private static class Shallow extends HashMap<String, String> {};
	
	public static String getFormat(String yaml) {
		Shallow shallow;
		try {
			shallow = deserialize(yaml, false, null, Shallow.class);
		} catch (DruthersLoadException e) {
			// If we can't read it, it might as well not have a format string
			return null;
		}
		String format = shallow.getOrDefault("format", null);
		if (format != null) {
			format = format.trim();
		}
		return format;
	}
	
	public static boolean hasFormat(String yaml) {
		return getFormat(yaml) != null;	
	}
	

	/**
	 * Encodes the serialized data as yaml
	 */
	public static String serialize(Object toSerialize) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		var rep = new DruthersYamlRepresenter(options);
		Yaml y = new Yaml(rep);
		
		return y.dumpAs(toSerialize, Tag.MAP, FlowStyle.BLOCK);
	}

	/**
	 * Reinterpret a section of a data structure as a different class by serializing
	 * the given object and deserializing as the given class.
	 */
	public static <T> T cast(Object generic, Class<T> cls) throws DruthersLoadException {
		return deserialize(serialize(generic), false, cls);
	}

}
