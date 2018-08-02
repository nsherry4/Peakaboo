package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
			
			
			try {
				BoltClassloaderPluginLoader<? extends P> javaLoader = javaLoader(plugins);
				
				//load plugins from local
				javaLoader.register();
				
				//load plugins from the application plugin dir(s)
				ensureManagedDirectory();
				javaLoader.register(directory);
								
			} catch (ClassInheritanceException e) {
				Bolt.logger().log(Level.SEVERE, "Failed to load plugins", e);
			}

			BoltFilesytstemPluginLoader<? extends P> scriptLoader = scriptLoader(plugins);
			ensureManagedDirectory();
			scriptLoader.scanDirectory(directory);
			
			
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
		ensureManagedDirectory();
		
		BoltPluginSet<P> plugins = pluginsInJar(jar);
		
		
		if (plugins.size() == 0) {
			String msg = "Importing " + jar.getAbsolutePath() + " failed, it does not contain any plugins";
			Bolt.logger().log(Level.INFO, msg);
			throw new BoltImportException("msg");
		}
		
		if (hasJar(jar)) {
			String msg = "Importing " + jar.getAbsolutePath() + " failed, file already exists";
			Bolt.logger().log(Level.INFO, msg);
			throw new BoltImportException(msg);
		}
		
		try {
			Files.copy(jar.toPath(), importJarPath(jar).toPath());
		} catch (IOException e) {
			String msg = "Importing " + jar.getAbsolutePath() + " failed";
			Bolt.logger().log(Level.WARNING, msg, e);
			throw new BoltImportException(msg, e);
		}
		
		return plugins;
				
	}
	
	
	private File importJarPath(File jar) {
		ensureManagedDirectory();
		return new File(directory.getAbsolutePath() + File.separator + jar.getName());
	}
	
	/**
	 * Checks to see if the new jar file is an upgrade for the old jar 
	 * in the plugins directory. It does this by making sure that all plugins 
	 * contained in the original jar are contained in the new jar, and that their 
	 * versions are the same or newer.
	 * @param jar the jar to examine
	 * @return true if the given jar is an upgrade for an existing managed jar, false otherwise
	 */
	private boolean jarIsUpgradeFor(File newJar, File oldJar) {
		System.out.println(newJar + ", " + oldJar);
		BoltPluginSet<P> oldSet = pluginsInJar(oldJar);
		BoltPluginSet<P> newSet = pluginsInJar(newJar);
		
		boolean match = newSet.isUpgradeFor(oldSet);
		return match;
	}
	
	/**
	 * Tests if the given jar is an upgrade for any of the existing managed jars.
	 * @param jar the jar to test
	 * @return the File for the upgradable jar if there is a match, or empty otherwise
	 */
	public Optional<File> jarUpgradeTarget(File jar) {
		for (File managedJar : managedJars()) {
			boolean isUpgrade = jarIsUpgradeFor(jar, managedJar);
			if (isUpgrade) {
				return Optional.of(managedJar);
			}
		}

		return Optional.empty();
	}
	
	/**
	 * Returns a list of managed jars from the managed plugin directory
	 */
	private List<File> managedJars() {
		ensureManagedDirectory();
		try {
			return Files.list(directory.toPath())
					.filter(path -> path.toString().toLowerCase().endsWith(".jar"))
					.map(path -> path.toFile())
					.collect(Collectors.toList());
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Cannot list managed plugin directory contents", e);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Checks the name (not the path) of the given jar, and checks to see if an 
	 * identically named jar is in the managed plugins folder
	 * @param jar the jar to examine
	 * @return true if the managed plugins folder already contains a jar with this name, false otherwise
	 */
	public boolean hasJar(File jar) {
		File newFilename = importJarPath(jar);
		return newFilename.exists();
	}
	
	public boolean removeJar(File jar) {
		ensureManagedDirectory();
		try {
			Files.delete(jar.toPath());
			return true;
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Failed to delete plugin jar", e);
			return false;
		}
	}
	
	private boolean ensureManagedDirectory() {
		return directory.mkdirs();
	}

	
	

	protected abstract void loadCustomPlugins();
	
	protected abstract BoltClassloaderPluginLoader<? extends P> javaLoader(BoltPluginSet<P> pluginset) throws ClassInheritanceException ;
	
	protected abstract BoltFilesytstemPluginLoader<? extends P> scriptLoader(BoltPluginSet<P> pluginset);


	
}


