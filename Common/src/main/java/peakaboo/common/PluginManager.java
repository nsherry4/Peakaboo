package peakaboo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.IBoltPluginSet;
import net.sciencestudio.bolt.plugin.java.BoltJavaPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.scripting.plugin.IBoltScriptPluginLoader;



public abstract class PluginManager<P extends BoltPlugin> {

	private boolean loaded = false;
	private BoltPluginSet<P> plugins = new IBoltPluginSet<>();

	private File directory = null;
	
	public PluginManager(File directories) {
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
				BoltJavaPluginLoader<? extends P> javaLoader = javaLoader(plugins);
				
				//load plugins from local
				javaLoader.register();
				
				//load plugins from the application plugin dir(s)
				if (directory != null) {
					directory.mkdirs();
					javaLoader.register(directory);	
				}
				
			} catch (ClassInheritanceException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to load plugins", e);
			}

			IBoltScriptPluginLoader<? extends P> scriptLoader = scriptLoader(plugins);
			//load script plugins from the application plugin dir(s)
			if (directory != null) {
				directory.mkdirs();
				scriptLoader.scanDirectory(directory, ".js");
			}
			
			
			
			//custom work
			loadCustomPlugins();
		}
	}
	
	public synchronized final BoltPluginSet<P> getPlugins() {
		load();
		return plugins;
	}

	
	/**
	 * Does the provided jar contain plugins which can be loaded by this {@link PluginManager}
	 * @param jar the jar file to inspect
	 */
	public boolean jarContainsPlugins(File jar) {
		try {
			//Create a new pluginset and load the jar. 
			//Any plugins in this set after loading will have come from this jar
			BoltPluginSet<P> dummySet = new IBoltPluginSet<>();
			BoltJavaPluginLoader<? extends P> javaLoader = javaLoader(dummySet);
			javaLoader.register(jar);
			
			return (dummySet.getAll().size() > 0);
			
		} catch (ClassInheritanceException e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to inspect jar", e);
			return false;
		}
	}
	
	
	/**
	 * Imports the given jar file to the directory that plugins are stored. 
	 * This will not perform a refresh of the plugins, that must be done separately.
	 * If the jar does not contain any valid plugins, or if a plugin with the same 
	 * filename already exists, the import will fail.
	 * @param jar the Jar file to import
	 * @return true if the jar was imported, false otherwise
	 */
	public boolean importJar(File jar) {
		if (!jarContainsPlugins(jar)) {
			PeakabooLog.get().log(Level.INFO, "Importing " + jar.getAbsolutePath() + " failed, it does not contain any plugins");
			return false;
		}
		
		File newFilename = new File(directory.getAbsolutePath() + File.separator + jar.getName());
		if (newFilename.exists()) {
			PeakabooLog.get().log(Level.INFO, "Importing " + jar.getAbsolutePath() + " failed, file already exists");
			return false;
		}
		
		try {
			Files.copy(jar.toPath(), newFilename.toPath());
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Importing " + jar.getAbsolutePath() + " failed", e);
		}
		
		return true;
				
	}
	
	

	protected abstract void loadCustomPlugins();
	
	protected abstract BoltJavaPluginLoader<? extends P> javaLoader(BoltPluginSet<P> pluginset) throws ClassInheritanceException ;
	
	protected abstract IBoltScriptPluginLoader<? extends P> scriptLoader(BoltPluginSet<P> pluginset);

	
	

	
}
