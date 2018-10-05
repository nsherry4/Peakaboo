package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;

public interface BoltFilesytstemPluginLoader<T extends BoltPlugin> {

	void scanDirectory(File directory);
	default void scanDirectory(BoltDirectoryManager<T> directory) {
		for (File f : directory.managedFiles()) {
			register(f);
		}
	}	
	void registerURL(URL file);
	
	default void register(File file) {
		try {
			registerURL(file.toURI().toURL());
		} catch (MalformedURLException e) {
			Bolt.logger().log(Level.WARNING, "Failed to load plugin " + file.toString(), e);
		}
	}

}