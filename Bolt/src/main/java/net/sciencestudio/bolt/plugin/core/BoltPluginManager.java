package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
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
			
			
			try {
				BoltClassloaderPluginLoader<? extends P> javaLoader = javaLoader(plugins);
				
				//load plugins from local
				javaLoader.register();
				
				//load plugins from the application plugin dir(s)
				if (directory != null) {
					directory.mkdirs();
					javaLoader.register(directory);	
				}
				
			} catch (ClassInheritanceException e) {
				Bolt.logger().log(Level.SEVERE, "Failed to load plugins", e);
			}

			BoltFilesytstemPluginLoader<? extends P> scriptLoader = scriptLoader(plugins);
			//load script plugins from the application plugin dir(s)
			if (directory != null) {
				directory.mkdirs();
				scriptLoader.scanDirectory(directory);
			}
			
			
			
			//custom work
			loadCustomPlugins();
		}
	}
	
	public synchronized final BoltPluginSet<P> getPlugins() {
		load();
		return plugins;
	}

	
	public BoltPluginSet<P> pluginsInJar(File jar) {
		try {
			//Create a new pluginset and load the jar. 
			//Any plugins in this set after loading will have come from this jar
			BoltPluginSet<P> dummySet = new IBoltPluginSet<>();
			BoltClassloaderPluginLoader<? extends P> javaLoader = javaLoader(dummySet);
			javaLoader.register(jar);
			
			return dummySet;
			
		} catch (ClassInheritanceException e) {
			Bolt.logger().log(Level.WARNING, "Failed to inspect jar", e);
			return new IBoltPluginSet<>();
		}
	}
	
	/**
	 * Does the provided jar contain plugins which can be loaded by this {@link BoltPluginManager}
	 * @param jar the jar file to inspect
	 */
	public boolean jarContainsPlugins(File jar) {
		return (pluginsInJar(jar).getAll().size() > 0);	
	}
	
	
	/**
	 * Imports the given jar file to the directory that plugins are stored. 
	 * This will not perform a refresh of the plugins, that must be done separately.
	 * If the jar does not contain any valid plugins, or if a plugin with the same 
	 * filename already exists, the import will fail.
	 * @param jar the Jar file to import
	 * @return a {@link Optional} containing a {@link BoltPluginSet} if the jar was imported, or empty otherwise
	 */
	public BoltPluginSet<P> importJar(File jar) throws BoltImportException {
		
		BoltPluginSet<P> plugins = pluginsInJar(jar);
		
		
		if (plugins.getAll().size() == 0) {
			String msg = "Importing " + jar.getAbsolutePath() + " failed, it does not contain any plugins";
			Bolt.logger().log(Level.INFO, msg);
			throw new BoltImportException("msg");
		}
		
		File newFilename = new File(directory.getAbsolutePath() + File.separator + jar.getName());
		if (newFilename.exists()) {
			String msg = "Importing " + jar.getAbsolutePath() + " failed, file already exists";
			Bolt.logger().log(Level.INFO, msg);
			throw new BoltImportException(msg);
		}
		
		try {
			Files.copy(jar.toPath(), newFilename.toPath());
		} catch (IOException e) {
			String msg = "Importing " + jar.getAbsolutePath() + " failed";
			Bolt.logger().log(Level.WARNING, msg, e);
			throw new BoltImportException(msg, e);
		}
		
		return plugins;
				
	}
	
	
	
	public boolean removeJar(File jar) {
		try {
			Files.delete(jar.toPath());
			return true;
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Failed to delete plugin jar", e);
			return false;
		}
	}
	

	
	

	protected abstract void loadCustomPlugins();
	
	protected abstract BoltClassloaderPluginLoader<? extends P> javaLoader(BoltPluginSet<P> pluginset) throws ClassInheritanceException ;
	
	protected abstract BoltFilesytstemPluginLoader<? extends P> scriptLoader(BoltPluginSet<P> pluginset);


	
}


