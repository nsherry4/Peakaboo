package peakaboo.datasource;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolt.plugin.BoltPluginLoader;
import bolt.plugin.ClassInheritanceException;
import bolt.plugin.ClassInstantiationException;
import commonenvironment.Env;
import peakaboo.common.Version;
import peakaboo.datasource.plugins.PlainText;

public class DataSourceLoader
{

	private static BoltPluginLoader<PluginDataSource> loader;
	
	public synchronized static BoltPluginLoader<PluginDataSource> getPluginLoader() {
		if (loader == null) {
			try {
				initLoader();
			} catch (ClassInheritanceException | ClassInstantiationException e) {
				e.printStackTrace();
			}
		}
		return loader;
	}
	
	private synchronized static void initLoader() throws ClassInheritanceException, ClassInstantiationException {
		
		BoltPluginLoader<PluginDataSource> newLoader = new BoltPluginLoader<PluginDataSource>(PluginDataSource.class);  
		
		//load local jars
		newLoader.register();
		
		//load jars in the app data directory
		File appDataDir = Env.appDataDirectory(Version.program_name, "Plugins");
		appDataDir.mkdirs();
		newLoader.register(appDataDir);
			
		
		//register built-in plugins
		newLoader.registerPlugin(PlainText.class);
					
		loader = newLoader;
	}
	
	public synchronized static List<DataSource> getDataSourcePlugins()
	{

		try
		{
			
			if (loader == null)	{
				initLoader();
			}
			
			return new ArrayList<>(loader.getNewInstancesForAllPlugins());
					
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
