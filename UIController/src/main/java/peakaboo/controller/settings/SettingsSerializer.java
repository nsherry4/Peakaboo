package peakaboo.controller.settings;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class SettingsSerializer {

	/**
	 * Decodes a serialized data object from yaml
	 */
	public static <T> T deserialize(String yaml) {
		Yaml y = new Yaml();
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
