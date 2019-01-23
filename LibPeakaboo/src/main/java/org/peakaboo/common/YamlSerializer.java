package org.peakaboo.common;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
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
				
		
		//be forgiving of fields which are unfamiliar, this may 
		//be a peakaboo session file from the future
		Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);
		Yaml y = new Yaml(representer);
		
		T data = (T)y.load(yaml);
		return data;
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
