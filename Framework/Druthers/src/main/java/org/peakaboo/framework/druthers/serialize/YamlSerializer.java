package org.peakaboo.framework.druthers.serialize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class YamlSerializer {

	
	/**
	 * Decodes a yaml document to a specific class. 
	 */
	public static <T extends Object> T deserialize(String yaml, Class<T> cls) {
		return deserialize(yaml, cls, true);
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
	public static <T extends Object> T deserialize(String yaml, Class<T> cls, boolean strict) {
		return deserialize(yaml, cls, strict, null);
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
	public static <T extends Object> T deserialize(String yaml, Class<T> cls, boolean strict, String format) {
		yaml = deserializeChecks(yaml, strict, format);
		return deserialize(yaml, buildLoader(cls, strict));
		
	}
	
	public static <T extends Object> T deserialize(String yaml, Yaml y) {
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
	
	/**
	 * Decodes a yaml document to a specific class. Useful for reading yaml documents without !! java class hints 
	 */
	public static <T extends Object> T deserialize(File file, Class<T> cls) throws IOException {
		return deserialize(new String(Files.readAllBytes(file.toPath())), cls);
	}
	
	
	private static String deserializeChecks(String yaml, boolean strict, String format) {

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
		Shallow shallow = deserialize(yaml, Shallow.class, false, null);
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
		var rep = new Representer(options);
		Yaml y = new Yaml(rep);
		
		return y.dumpAs(toSerialize, Tag.MAP, FlowStyle.BLOCK);
	}

}
