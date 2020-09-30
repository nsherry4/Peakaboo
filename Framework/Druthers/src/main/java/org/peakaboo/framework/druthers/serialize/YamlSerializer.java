package org.peakaboo.framework.druthers.serialize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

public class YamlSerializer {

	/**
	 * Decodes a serialized data object from yaml
	 */
	public static <T> T deserialize(String yaml) {

		/*
		 * Between 5.2 and 5.3 package names were changed to use the peakaboo.org domain
		 * name. This results in problems when loading older serialized files which
		 * specify classnames with the old peakaboo top level package name. We do a
		 * simple string find and replace to try and keep things working between
		 * versions
		 */
		//TODO: remove in Peakaboo 6
		yaml = yaml.replace("!!peakaboo.", "!!org.peakaboo.");
		yaml = yaml.replace(": peakaboo.", ": org.peakaboo.");
		yaml = yaml.replace("!!org.peakaboo.controller.settings.SavedSession", "!!org.peakaboo.controller.plotter.SavedSession");
		
		//be forgiving of fields which are unfamiliar, this may 
		//be a peakaboo session file from the future
		Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);
		Yaml y = new Yaml(representer);
		
		try {
			return (T)y.load(yaml);
		} catch (YAMLException e) {
			throw new DruthersLoadException(e);
		}
		
	}
	public static <T> T deserialize(File file) throws IOException {
		return deserialize(new String(Files.readAllBytes(file.toPath())));
	}
	
	/**
	 * Strip out the custom class markers and load as a generic data structure
	 */
	public static <T> T deserializeGeneric(String yaml) {
		String generic = yaml.lines().filter(l -> !l.startsWith("!!")).collect(Collectors.joining("\n"));
		System.out.println(generic);
		return deserialize(generic);
	}
	public static <T> T deserializeGeneric(File file) throws IOException {
		return deserializeGeneric(new String(Files.readAllBytes(file.toPath())));
	}
	
	
	
	/**
	 * Encodes the serialized data as yaml
	 */
	public static String serialize(Object toSerialize) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		Yaml y = new Yaml(options);
		return y.dump(toSerialize);
	}
	
}
