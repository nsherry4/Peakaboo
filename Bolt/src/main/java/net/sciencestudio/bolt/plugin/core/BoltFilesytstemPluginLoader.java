package net.sciencestudio.bolt.plugin.core;

import java.io.File;

public interface BoltFilesytstemPluginLoader<T extends BoltPlugin> {

	void scanDirectory(File directory);

}