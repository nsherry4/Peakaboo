package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;

public class BoltClassloaderDirectoryManager<P extends BoltPlugin> extends BoltDirectoryManager<P> {

	private File directory;
	private BoltPluginManager<P> parent;
	
	public BoltClassloaderDirectoryManager(BoltPluginManager<P> parent, File directory) {
		this.directory = directory;
		this.parent = parent;
	}
	
	@Override
	public File getDirectory() {
		return this.directory;
	}

	@Override
	public BoltPluginSet<P> pluginsInFile(File file) {
		try {
			//Create a new pluginset and load the jar. 
			//Any plugins in this set after loading will have come from this jar
			BoltPluginSet<P> dummySet = new IBoltPluginSet<>();
			BoltClassloaderPluginLoader<? extends P> loader = parent.classpathLoader(dummySet);
			if (loader == null) {
				return dummySet;
			}
			loader.register(file);
			
			return dummySet;
			
		} catch (ClassInheritanceException e) {
			Bolt.logger().log(Level.WARNING, "Failed to inspect jar", e);
			return new IBoltPluginSet<>();
		}
	}


	@Override
	public List<File> managedFiles() {
		return scanFiles(path -> path.toString().toLowerCase().endsWith(".jar"));
	}

}
