package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.util.List;

public class BoltFilesystemDirectoryManager<P extends BoltPlugin> extends BoltDirectoryManager<P> {

	private File directory;
	private BoltPluginManager<P> parent;
	
	public BoltFilesystemDirectoryManager(BoltPluginManager<P> parent, File directory) {
		this.directory = directory;
		this.parent = parent;
	}
	
	@Override
	public File getDirectory() {
		return this.directory;
	}

	@Override
	public BoltPluginSet<P> pluginsInFile(File file) {
		//Create a new pluginset and load the jar. 
		//Any plugins in this set after loading will have come from this jar
		BoltPluginSet<P> dummySet = new IBoltPluginSet<>();
		BoltFilesytstemPluginLoader<? extends P> loader = parent.filesystemLoader(dummySet);
		if (loader == null) {
			return dummySet;
		}
		loader.register(file);
		return dummySet;
	}


	@Override
	public List<File> managedFiles() {
		return scanFiles(path -> !path.toString().toLowerCase().endsWith(".jar"));
	}

}
