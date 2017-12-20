package peakaboo.datasource;

import java.io.File;

import bolt.plugin.core.BoltPluginSet;
import bolt.plugin.core.IBoltPluginSet;
import bolt.plugin.java.BoltPluginLoader;
import bolt.plugin.java.ClassInheritanceException;
import bolt.plugin.java.ClassInstantiationException;
import bolt.scripting.plugin.IBoltScriptPluginLoader;
import commonenvironment.Env;
import peakaboo.common.Version;
import peakaboo.datasource.framework.JavaPluginDataSource;
import peakaboo.datasource.framework.JavaScriptPluginDataSource;
import peakaboo.datasource.framework.PluginDataSource;
import peakaboo.datasource.plugins.PlainText;

public class DataSourceLoader
{

	private static boolean loaded = false; 
	private static BoltPluginSet<PluginDataSource> plugins = new IBoltPluginSet<>();
	
	public static void load() {
		if (!loaded) {
			try {
				loadPlugins();
			} catch (ClassInheritanceException | ClassInstantiationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized static BoltPluginSet<PluginDataSource> getPluginSet() {
		load();
		return plugins;
	}

	
	private synchronized static void loadPlugins() throws ClassInheritanceException, ClassInstantiationException {
		
		BoltPluginLoader<JavaPluginDataSource> javaLoader = new BoltPluginLoader<JavaPluginDataSource>(plugins, JavaPluginDataSource.class);  
		
		//load local jars
		javaLoader.register();
		
		//load jars in the app data directory
		File appDataDir = Env.appDataDirectory(Version.program_name, "Plugins/DataSource");
		appDataDir.mkdirs();
		javaLoader.register(appDataDir);
			
		//register built-in plugins
		javaLoader.registerPlugin(PlainText.class);
		
		
		
		
		IBoltScriptPluginLoader<JavaScriptPluginDataSource> jsLoader = new IBoltScriptPluginLoader<>(plugins, JavaScriptPluginDataSource.class);
		jsLoader.scanDirectory(appDataDir, ".js");
		

		loaded = true;
	}
	


		
}
