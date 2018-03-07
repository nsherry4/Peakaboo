package peakaboo.datasink.plugin;

import java.io.File;

import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.IBoltPluginSet;
import net.sciencestudio.bolt.plugin.java.BoltPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.scripting.plugin.IBoltScriptPluginLoader;
import peakaboo.common.Configuration;
import peakaboo.datasink.plugin.plugins.CSV;

public class DataSinkLoader
{

	private static boolean loaded = false; 
	private static BoltPluginSet<DataSinkPlugin> plugins = new IBoltPluginSet<>();
	
	public static void load() {
		if (!loaded) {
			try {
				loadPlugins();
			} catch (ClassInheritanceException | ClassInstantiationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized static BoltPluginSet<DataSinkPlugin> getPluginSet() {
		load();
		return plugins;
	}

	
	private synchronized static void loadPlugins() throws ClassInheritanceException, ClassInstantiationException {
		
		BoltPluginLoader<JavaDataSinkPlugin> javaLoader = new BoltPluginLoader<JavaDataSinkPlugin>(plugins, JavaDataSinkPlugin.class);  
		
		//load local jars
		javaLoader.register();
		
		//load jars in the app data directory
		File appDataDir = Configuration.appDir("Plugins/DataSink");
		appDataDir.mkdirs();
		javaLoader.register(appDataDir);
			
		//register built-in plugins
		javaLoader.registerPlugin(CSV.class);
		
		
		
		
		IBoltScriptPluginLoader<JavaScriptDataSinkPlugin> jsLoader = new IBoltScriptPluginLoader<>(plugins, JavaScriptDataSinkPlugin.class);
		jsLoader.scanDirectory(appDataDir, ".js");
		

		loaded = true;
	}
	


		
}
