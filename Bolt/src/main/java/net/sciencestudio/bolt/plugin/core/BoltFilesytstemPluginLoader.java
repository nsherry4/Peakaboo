package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.net.URL;

public interface BoltFilesytstemPluginLoader<T extends BoltPlugin> {

	void scanDirectory(File directory);
	void registerURL(URL file);

}