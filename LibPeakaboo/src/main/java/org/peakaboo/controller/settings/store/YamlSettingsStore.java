package org.peakaboo.controller.settings.store;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.common.YamlSerializer;

public class YamlSettingsStore implements SettingsStore {

	private Path filepath;
	private Map<String, String> values = new HashMap<>();
	
	public YamlSettingsStore(File directory) throws IOException {
		directory.mkdirs();
		filepath = Paths.get(directory.getAbsolutePath(), "/store.yaml");
		if (!Files.exists(filepath)) {
			write();
		}
		//TODO: replace with Files.readString in Java11+
		String yaml = new String(Files.readAllBytes(filepath));
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
			//TODO: replace with Files.writeString in Java11+
			Files.write(filepath, yaml.getBytes(), 
					StandardOpenOption.CREATE, 
					StandardOpenOption.DSYNC, 
					StandardOpenOption.WRITE, 
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to write persistent settings to disk", e);
		}
	}

}
