package peakaboo.fileio.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import peakaboo.common.Version;

import commonenvironment.Env;

import bolt.plugin.BoltPluginLoader;
import bolt.plugin.ClassInheritanceException;

public class DataSourcePluginLoader
{

	public static List<AbstractDataSourcePlugin> getDataSourcePlugins()
	{

		try
		{
			BoltPluginLoader<AbstractDataSourcePlugin> loader;
			loader = new BoltPluginLoader<AbstractDataSourcePlugin>(AbstractDataSourcePlugin.class);
			
			
			//load plugins shipped with peakaboo
			loader.loadLocalPlugins("peakaboo.fileio.datasource.plugins");
						
			//if peakaboo is in a jar file, look in other jar files in the same directory
			if (Env.isClassInJar(AbstractDataSourcePlugin.class))
			{
				File jarfile = Env.getJarForClass(AbstractDataSourcePlugin.class);
				loader.loadPluginsFromJarsInDirectory(jarfile.getParentFile());
			}
						
			//look in peakaboo's application data directory
			File appDataDir = Env.appDataDirectory(Version.program_name);
			appDataDir.mkdirs();
			loader.loadPluginsFromJarsInDirectory(appDataDir);
			
			
			return loader.getNewInstancesForAllPlugins();
			
		}
		catch (ClassInheritanceException e)
		{
			e.printStackTrace();
		}
		
		//failure -- return empty list
		List<AbstractDataSourcePlugin> plugins = new ArrayList<AbstractDataSourcePlugin>();
		return plugins;
	}

	public static Set<String> getSupportedFileExtensions()
	{
		Set<String> exts = new LinkedHashSet<String>();
		
		for (AbstractDataSourcePlugin plugin : getDataSourcePlugins())
		{
			exts.addAll(plugin.getFileExtensions());
		}
		
		return exts;
		
	}
	
	public static void main(String[] args)
	{
		getDataSourcePlugins();
	}
	
}
