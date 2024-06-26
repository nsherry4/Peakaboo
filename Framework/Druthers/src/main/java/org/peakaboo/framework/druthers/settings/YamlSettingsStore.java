package org.peakaboo.framework.druthers.settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.logging.Level;

import org.peakaboo.framework.druthers.Druthers;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

public class YamlSettingsStore implements SettingsStore {

	public static final String DRUTHERS_SETTINGS_FORMAT = "org.peakaboo.framework.druthers.settings/v1";
	
	static class KeyValueStore extends HashMap<String, String> {}
	static class SettingsStoreYaml {
		public String format;
		public KeyValueStore entries;
		public SettingsStoreYaml() {
			format = DRUTHERS_SETTINGS_FORMAT;
			entries = new KeyValueStore();
		}
		public SettingsStoreYaml(String format, KeyValueStore entries) {
			this.format = format;
			this.entries = entries;
		}
	}
	
	private Path filepath;
	private KeyValueStore values = new KeyValueStore();
	
	
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
		
		if (DruthersSerializer.hasFormat(yaml)) {
			values = DruthersSerializer.deserialize(yaml, false, DRUTHERS_SETTINGS_FORMAT, SettingsStoreYaml.class).entries;
		} else {
			//This is an older P5 settings file
			//TODO: remove this in Peakaboo 7
			values = DruthersSerializer.deserialize(yaml, false, KeyValueStore.class);
		}
		
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
		String yaml = DruthersSerializer.serialize(new SettingsStoreYaml(DRUTHERS_SETTINGS_FORMAT, values));
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
