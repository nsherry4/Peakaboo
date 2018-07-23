package peakaboo.datasource.plugin;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.IBoltPluginSet;
import net.sciencestudio.bolt.plugin.java.BoltPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.scripting.plugin.IBoltScriptPluginLoader;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.datasource.plugin.plugins.PlainText;

public class DataSourceLoader
{

	private static boolean loaded = false; 
	private static BoltPluginSet<DataSourcePlugin> __plugins = new IBoltPluginSet<>();
	private static BoltPluginLoader<JavaDataSourcePlugin> __javaLoader;
	
	public static void load() {
		if (!loaded) {
			try {
				loadPlugins();
			} catch (ClassInheritanceException | ClassInstantiationException e) {
				PeakabooLog.get().log(Level.WARNING, "Error loading data source plugins", e);
			}
		}
	}
	
	public synchronized static BoltPluginSet<DataSourcePlugin> getPluginSet() {
		load();
		return __plugins;
	}

	private static BoltPluginLoader<JavaDataSourcePlugin> javaLoader() throws ClassInheritanceException {
		if (__javaLoader == null) {
			__javaLoader = new BoltPluginLoader<JavaDataSourcePlugin>(__plugins, JavaDataSourcePlugin.class);
		}
		return __javaLoader;
	}
	
	private synchronized static void loadPlugins() throws ClassInheritanceException, ClassInstantiationException {
		if (loaded == true) {
			return;
		}
		
		BoltPluginLoader<JavaDataSourcePlugin> javaLoader = javaLoader();  
		
		//load local jars
		javaLoader.register();
		
		//load jars in the app data directory
		File appDataDir = Configuration.appDir("Plugins/DataSource");
		appDataDir.mkdirs();
		javaLoader.register(appDataDir);
			
		//register built-in plugins
		javaLoader.registerPlugin(PlainText.class);
		
		
		
		IBoltScriptPluginLoader<JavaScriptDataSourcePlugin> jsLoader = new IBoltScriptPluginLoader<>(__plugins, JavaScriptDataSourcePlugin.class);
		jsLoader.scanDirectory(appDataDir, ".js");
		
		
		//Log info for plugins
		for (BoltPluginController<? extends DataSourcePlugin> plugin : __plugins.getAll()) {
			PeakabooLog.get().info("Found DataSource Plugin " + plugin.getName() + " from " + plugin.getSource());
		}
		
		

		loaded = true;
	}
	
	public synchronized static void registerPlugin(Class<? extends JavaDataSourcePlugin> clazz) {
		try {
			BoltPluginLoader<JavaDataSourcePlugin> javaLoader = javaLoader();
			BoltPluginController<JavaDataSourcePlugin> plugin = javaLoader.registerPlugin(clazz);
			if (plugin != null) {
				PeakabooLog.get().info("Registered DataSource Plugin " + plugin.getName() + " from " + plugin.getSource());
			}
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Error registering data source plugin " + clazz.getName(), e);
		}
	}
	


		
}
