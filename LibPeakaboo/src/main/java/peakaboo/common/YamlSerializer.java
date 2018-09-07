package peakaboo.controller.settings;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

public class SettingsSerializer {

	/**
	 * Decodes a serialized data object from yaml
	 */
	public static <T> T deserialize(String yaml) {
				
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
