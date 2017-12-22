package peakaboo.datasink.plugin;

import java.io.File;

import bolt.plugin.core.BoltPluginSet;
import bolt.plugin.core.IBoltPluginSet;
import bolt.plugin.java.BoltPluginLoader;
import bolt.plugin.java.ClassInheritanceException;
import bolt.plugin.java.ClassInstantiationException;
import bolt.scripting.plugin.IBoltScriptPluginLoader;
import commonenvironment.Env;
import peakaboo.common.Version;
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
		File appDataDir = Env.appDataDirectory(Version.program_name + Version.versionNoMajor, "Plugins/DataSink");
		appDataDir.mkdirs();
		javaLoader.register(appDataDir);
			
		//register built-in plugins
		javaLoader.registerPlugin(CSV.class);
		
		
		
		
		IBoltScriptPluginLoader<JavaScriptDataSinkPlugin> jsLoader = new IBoltScriptPluginLoader<>(plugins, JavaScriptDataSinkPlugin.class);
		jsLoader.scanDirectory(appDataDir, ".js");
		

		loaded = true;
	}
	


		
}
