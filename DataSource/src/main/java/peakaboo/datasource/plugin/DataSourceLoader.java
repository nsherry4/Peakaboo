package peakaboo.datasource.plugin;

import java.io.File;
import java.util.logging.Level;

import bolt.plugin.core.BoltPluginSet;
import bolt.plugin.core.IBoltPluginSet;
import bolt.plugin.java.BoltPluginLoader;
import bolt.plugin.java.ClassInheritanceException;
import bolt.plugin.java.ClassInstantiationException;
import bolt.scripting.plugin.IBoltScriptPluginLoader;
import commonenvironment.Env;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.common.Version;
import peakaboo.datasource.plugin.plugins.PlainText;

public class DataSourceLoader
{

	private static boolean loaded = false; 
	private static BoltPluginSet<DataSourcePlugin> plugins = new IBoltPluginSet<>();
	
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
		return plugins;
	}

	
	private synchronized static void loadPlugins() throws ClassInheritanceException, ClassInstantiationException {
		
		BoltPluginLoader<JavaDataSourcePlugin> javaLoader = new BoltPluginLoader<JavaDataSourcePlugin>(plugins, JavaDataSourcePlugin.class);  
		
		//load local jars
		javaLoader.register();
		
		//load jars in the app data directory
		File appDataDir = Configuration.appDir("Plugins/DataSource");
		appDataDir.mkdirs();
		javaLoader.register(appDataDir);
			
		//register built-in plugins
		javaLoader.registerPlugin(PlainText.class);
		
		
		
		
		IBoltScriptPluginLoader<JavaScriptDataSourcePlugin> jsLoader = new IBoltScriptPluginLoader<>(plugins, JavaScriptDataSourcePlugin.class);
		jsLoader.scanDirectory(appDataDir, ".js");
		

		loaded = true;
	}
	
	public synchronized static void registerPlugin(Class<? extends JavaDataSourcePlugin> clazz) {
		try {
			BoltPluginLoader<JavaDataSourcePlugin> javaLoader = new BoltPluginLoader<JavaDataSourcePlugin>(plugins, JavaDataSourcePlugin.class);
			javaLoader.registerPlugin(clazz);
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Error registering data source plugin " + clazz.getName(), e);
		}
	}
	


		
}
