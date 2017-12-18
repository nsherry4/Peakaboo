package peakaboo.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bolt.plugin.core.BoltPluginSet;
import bolt.plugin.core.IBoltPluginSet;
import bolt.plugin.java.BoltPluginLoader;
import bolt.plugin.java.ClassInheritanceException;
import bolt.plugin.java.ClassInstantiationException;
import bolt.scripting.plugin.IBoltScriptPluginController;
import commonenvironment.Env;
import peakaboo.common.Version;
import peakaboo.datasource.plugins.PlainText;

public class DataSourceLoader
{

	private static BoltPluginLoader<JavaPluginDataSource> loader;
	private static BoltPluginSet<PluginDataSource> plugins = new IBoltPluginSet<>();
	
	public static void load() {
		try {
			initLoader();
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static BoltPluginSet<PluginDataSource> getPluginSet() {
		if (loader == null) {
			try {
				initLoader();
			} catch (ClassInheritanceException | ClassInstantiationException e) {
				e.printStackTrace();
			}
		}
		return plugins;
	}
	
	private synchronized static void initLoader() throws ClassInheritanceException, ClassInstantiationException {
		
		BoltPluginLoader<JavaPluginDataSource> newLoader = new BoltPluginLoader<JavaPluginDataSource>(plugins, JavaPluginDataSource.class);  
		
		//load local jars
		newLoader.register();
		
		//load jars in the app data directory
		File appDataDir = Env.appDataDirectory(Version.program_name, "Plugins/DataSource");
		appDataDir.mkdirs();
		newLoader.register(appDataDir);
			
		
		//register built-in plugins
		newLoader.registerPlugin(PlainText.class);
		
		for (File file : appDataDir.listFiles()) {
			if (! file.getName().endsWith(".js")) continue;
			IBoltScriptPluginController<JavaScriptPluginDataSource> plugin = new IBoltScriptPluginController<>(file, JavaScriptPluginDataSource.class, JavaScriptPluginDataSource.class);
			plugins.addPlugin(plugin);
		}
					
		loader = newLoader;
	}
	
	public synchronized static List<DataSource> getDataSourcePlugins()
	{

		try
		{
			
			if (loader == null)	{
				initLoader();
			}
			
			return new ArrayList<>(plugins.getNewInstancesForAllPlugins());
					
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			e.printStackTrace();
		}
		
		//failure -- return empty list
		return new ArrayList<DataSource>();
	}

	
	
	public static void main(String[] args)
	{
		getDataSourcePlugins();
	}
	
}
