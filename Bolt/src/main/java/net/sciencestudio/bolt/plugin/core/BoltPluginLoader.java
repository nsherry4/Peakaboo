package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;

public interface BoltPluginLoader<T extends BoltPlugin> {

	default void scanDirectory(BoltDirectoryManager<T> directory) {
		for (File f : directory.managedFiles()) {
			register(f);
		}
	}	
	
	default void register(File file) {
		//Something went wrong.
		if (!file.exists()) {
			Bolt.logger().log(Level.WARNING, "File " + file + " does not exist");
			return;
		}
		
		File[] files;
		if (file.isDirectory())	{
			files = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
		} else {
			files = new File[1];
			files[0] = file;
		}
		
		//Something went wrong.
		if (files == null) {
			Bolt.logger().log(Level.WARNING, "File " + file + " could not be loaded");
			return;
		}
		
		for (int i = 0; i < files.length; i++) {
			try	{
				registerURL(files[i].toURI().toURL());
			} catch (Exception e) {
				Bolt.logger().log(Level.WARNING, "Unable to load plugin at " + files[i], e);
			}
		}
	}
	
	default void scanDirectory(File directory) {
		if (!directory.isDirectory()) {
			Bolt.logger().log(Level.WARNING, "Directory " + directory + " is not a valid directory");
			return;
		}
		register(directory);
	}

	void registerURL(URL file);


}