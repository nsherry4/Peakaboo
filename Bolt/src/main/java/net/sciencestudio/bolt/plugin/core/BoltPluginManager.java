package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.exceptions.BoltImportException;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;


/**
 * High-level manager for a plugin system. Provides functionality 
 * for loading, reloading, importing, deleting, etc.
 * @author NAS
 *
 * @param <P>
 */
public abstract class BoltPluginManager<P extends BoltPlugin> {

	private boolean loaded = false;
	private BoltPluginSet<P> plugins = new IBoltPluginSet<>();

	private File directory = null;
	
	public BoltPluginManager(File directories) {
		this.directory = directories;
		if (this.directory == null) {
			throw new IllegalArgumentException("directory cannot be null");
		}
	}
	
	public synchronized final void reload() {
		clear();
		load();
	}
	
	public synchronized final void clear() {
		plugins = new IBoltPluginSet<>();
		loaded = false;
	}
	
	public synchronized final void load() {
		if (loaded == false) {
			loaded = true;
			
			loadClasspath();
			loadFilesystem();
			
			//custom work
			loadCustomPlugins();
		}
	}
	
	private void loadClasspath() {
		try {
			BoltClassloaderPluginLoader<? extends P> classpathLoader = classpathLoader(plugins);
			BoltDirectoryManager<? extends P> classpathManager = classloaderDirectoryManager();
			
			if (classpathLoader != null && classpathManager != null) {
				//load plugins from local
				classpathLoader.registerBuiltIn();
				
				//load plugins from the application plugin dir(s)
				classpathManager.ensure();
				classpathLoader.scanDirectory(directory);
			}
							
		} catch (ClassInheritanceException e) {
			Bolt.logger().log(Level.SEVERE, "Failed to load plugins", e);
		}
	}
	
	private void loadFilesystem() {
		BoltFilesytstemPluginLoader<? extends P> filesystemLoader = filesystemLoader(plugins);
		BoltDirectoryManager<P> filesystemManager = filesystemDirectoryManager();
		if (filesystemLoader != null && filesystemManager != null) {
			filesystemManager.ensure();
			filesystemLoader.scanDirectory(directory);
		}
	}
	
	public synchronized final BoltPluginSet<P> getPlugins() {
		load();
		return plugins;
	}

	protected File getDirectory() {
		return this.directory;
	}
	

	
	public BoltPluginSet<P> pluginsInFile(File file) {
		BoltDirectoryManager<P> classloaderManager = classloaderDirectoryManager();
		BoltDirectoryManager<P> filesystemManager = filesystemDirectoryManager();
		
		//ONE of them could be null
		if (classloaderManager == null) {
			return filesystemManager.pluginsInFile(file);
		}
		if (filesystemManager == null) {
			return classloaderManager.pluginsInFile(file);
		}
		
		BoltPluginSet<P> filesystemPlugins = filesystemManager.pluginsInFile(file);
		BoltPluginSet<P> classloaderPlugins = classloaderManager.pluginsInFile(file);
		BoltPluginSet<P> merged = new IBoltPluginSet<>();
		for (BoltPluginPrototype<? extends P> plugin : classloaderPlugins.getAll()) {
			merged.addPlugin(plugin);
		}
		for (BoltPluginPrototype<? extends P> plugin : filesystemPlugins.getAll()) {
			merged.addPlugin(plugin);
		}
		return merged;
		
		
	}
	
	public void removeFile(File file) {
		BoltDirectoryManager<P> classloaderManager = classloaderDirectoryManager();
		BoltDirectoryManager<P> filesystemManager = filesystemDirectoryManager();
		
		if (filesystemManager != null) {
			filesystemManager.removeFile(file);
		}
		if (classloaderManager != null) {
			classloaderManager.removeFile(file);
		}
	}
	
	public boolean fileContainsPlugins(File file) {
		BoltDirectoryManager<P> classloaderManager = classloaderDirectoryManager();
		BoltDirectoryManager<P> filesystemManager = filesystemDirectoryManager();
		
		boolean contains = false;
		if (filesystemManager != null) {
			contains |= filesystemManager.fileContainsPlugins(file);
		}
		if (classloaderManager != null) {
			contains |= classloaderManager.fileContainsPlugins(file);
		}
		
		return contains;
		
	}
	
	public BoltPluginSet<? extends BoltPlugin> importOrUpgradeFile(File file) throws BoltImportException {
		BoltDirectoryManager<P> classloaderManager = classloaderDirectoryManager();
		BoltDirectoryManager<P> filesystemManager = filesystemDirectoryManager();
		
		//ONE of them could be null
		if (classloaderManager == null) {
			return filesystemManager.importOrUpgradeFile(file);
		}
		if (filesystemManager == null) {
			return classloaderManager.importOrUpgradeFile(file);
		}
		
		boolean forClassloader = classloaderManager.fileContainsPlugins(file);
		boolean forFilesystem = filesystemManager.fileContainsPlugins(file);
		
		if (forClassloader && forFilesystem) {
			throw new BoltImportException("Cannot determine which type of plugin is being modified");
		}
		
		if (forClassloader) {
			return classloaderManager.importOrUpgradeFile(file);
		}
		
		if (forFilesystem) {
			return filesystemManager.importOrUpgradeFile(file);
		}
		
		throw new BoltImportException("Cannot determine which type of plugin is being modified");
		
	}
	

	protected abstract void loadCustomPlugins();
	
	protected abstract BoltClassloaderPluginLoader<? extends P> classpathLoader(BoltPluginSet<P> pluginset) throws ClassInheritanceException ;
	protected abstract BoltFilesytstemPluginLoader<? extends P> filesystemLoader(BoltPluginSet<P> pluginset);


	protected abstract BoltDirectoryManager<P> classloaderDirectoryManager();
	protected abstract BoltDirectoryManager<P> filesystemDirectoryManager();


	/**
	 * Provides a name for the kind of plugins managed by this manager
	 * @return
	 */
	public abstract String getInterfaceName();
	/**
	 * Provides a description for the kind of plugins managed by this manager
	 */
	public abstract String getInterfaceDescription();
	




	

	
	

	
}


