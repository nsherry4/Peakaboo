package org.peakaboo.framework.druthers.settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.peakaboo.framework.druthers.Druthers;
import org.peakaboo.framework.druthers.serialize.YamlSerializer;

public class YamlSettingsStore implements SettingsStore {

	private Path filepath;
	private Map<String, String> values = new HashMap<>();
	
	public YamlSettingsStore(File directory) throws IOException {
		this(directory, "store");
	}
	
	public YamlSettingsStore(File directory, String storename) throws IOException {
		directory.mkdirs();
		filepath = Paths.get(directory.getAbsolutePath(), "/" + storename + ".yaml");
		if (!Files.exists(filepath)) {
			write();
		}
		String yaml = Files.readString(filepath);
		values = YamlSerializer.deserialize(yaml);
	}
	
	@Override
	public String get(String key, String fallback) {
		return values.getOrDefault(key, fallback);
	}

	@Override
	public void set(String key, String value) {
		values.put(key, value);
		write();
	}
	
	private void write() {
		String yaml = YamlSerializer.serialize(values);
		try {
			Files.writeString(filepath, yaml, 
					StandardOpenOption.CREATE, 
					StandardOpenOption.DSYNC, 
					StandardOpenOption.WRITE, 
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			Druthers.logger().log(Level.WARNING, "Failed to write persistent settings to disk", e);
		}
	}

}
