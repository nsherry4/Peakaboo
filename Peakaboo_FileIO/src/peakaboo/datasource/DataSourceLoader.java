package peakaboo.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolt.plugin.BoltPluginLoader;
import bolt.plugin.ClassInheritanceException;
import bolt.plugin.ClassInstantiationException;
import commonenvironment.Env;
import peakaboo.common.Version;
import peakaboo.datasource.plugins.Emsa;
import peakaboo.datasource.plugins.MCA;
import peakaboo.datasource.plugins.PlainText;
import peakaboo.datasource.plugins.cdfml.CDFMLSax;
import peakaboo.datasource.plugins.sciencestudio.ScienceStudio;

public class DataSourceLoader
{

	public static BoltPluginLoader<AbstractDataSource> loader;
	
	public synchronized static List<AbstractDataSource> getDSPs()
	{

		try
		{
			
			if (loader == null)
			{

				BoltPluginLoader<AbstractDataSource> newLoader = new BoltPluginLoader<AbstractDataSource>(AbstractDataSource.class);  
				
				//load local jars
				newLoader.register();
				
				//load jars in the app data directory
				File appDataDir = Env.appDataDirectory(Version.program_name);
				appDataDir.mkdirs();
				newLoader.register(appDataDir);
					
				
				//register built-in plugins
				newLoader.registerPlugin(CDFMLSax.class);
				newLoader.registerPlugin(MCA.class);
				newLoader.registerPlugin(PlainText.class);
				newLoader.registerPlugin(ScienceStudio.class);
				newLoader.registerPlugin(Emsa.class);
							
				loader = newLoader;
			}
			
			List<AbstractDataSource> filters = loader.getNewInstancesForAllPlugins();
			
			Collections.sort(filters, new Comparator<AbstractDataSource>() {

				@Override
				public int compare(AbstractDataSource f1, AbstractDataSource f2)
				{
					return f1.getDataFormat().compareTo(f1.getDataFormat());
				}});
			
			return filters;
			
		}
		catch (ClassInheritanceException e)
		{
			e.printStackTrace();
		}
		catch (ClassInstantiationException e)
		{
			e.printStackTrace();
		}
		
		//failure -- return empty list
		List<AbstractDataSource> plugins = new ArrayList<AbstractDataSource>();
		return plugins;
	}

	
	
	public static void main(String[] args)
	{
		getDSPs();
	}
	
}
