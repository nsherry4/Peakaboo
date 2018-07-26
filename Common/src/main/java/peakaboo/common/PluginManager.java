package peakaboo.common;

import java.io.File;
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

	private File[] directories = new File[0];
	
	public PluginManager(File... directories) {
		this.directories = directories;
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
				for (File directory : directories) {
					directory.mkdirs();
					javaLoader.register(directory);	
				}
				
			} catch (ClassInheritanceException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to load plugins", e);
			}

			IBoltScriptPluginLoader<? extends P> scriptLoader = scriptLoader(plugins);
			//load script plugins from the application plugin dir(s)
			for (File directory : directories) {
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
	

	protected abstract void loadCustomPlugins();
	
	protected abstract BoltJavaPluginLoader<? extends P> javaLoader(BoltPluginSet<P> pluginset) throws ClassInheritanceException ;
	
	protected abstract IBoltScriptPluginLoader<? extends P> scriptLoader(BoltPluginSet<P> pluginset);

	
	

	
}
