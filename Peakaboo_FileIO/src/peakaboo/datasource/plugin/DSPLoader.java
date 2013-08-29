package peakaboo.datasource.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import peakaboo.common.Version;
import peakaboo.datasource.plugin.plugins.CDFMLSaxDSP;
import peakaboo.datasource.plugin.plugins.MCA_DSP;
import peakaboo.datasource.plugin.plugins.PlainTextDSP;
import peakaboo.datasource.plugin.plugins.ScienceStudioDSP;
import bolt.plugin.BoltPluginLoader;
import bolt.plugin.ClassInheritanceException;
import bolt.plugin.ClassInstantiationException;

import commonenvironment.Env;

public class DSPLoader
{

	private static BoltPluginLoader<AbstractDSP> loader;
	
	public synchronized static List<AbstractDSP> getDSPs()
	{

		try
		{
			
			if (loader == null)
			{

				BoltPluginLoader<AbstractDSP> newLoader = new BoltPluginLoader<AbstractDSP>(AbstractDSP.class);  
				
				//load local jars
				newLoader.register();
				
				//load jars in the app data directory
				File appDataDir = Env.appDataDirectory(Version.program_name);
				appDataDir.mkdirs();
				newLoader.register(appDataDir);
					
				
				//register built-in plugins
				newLoader.registerPlugin(CDFMLSaxDSP.class);
				newLoader.registerPlugin(MCA_DSP.class);
				newLoader.registerPlugin(PlainTextDSP.class);
				newLoader.registerPlugin(ScienceStudioDSP.class);
				
				loader = newLoader;
			}
			
			List<AbstractDSP> filters = loader.getNewInstancesForAllPlugins();
			
			Collections.sort(filters, new Comparator<AbstractDSP>() {

				@Override
				public int compare(AbstractDSP f1, AbstractDSP f2)
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
		List<AbstractDSP> plugins = new ArrayList<AbstractDSP>();
		return plugins;
	}

	
	
	public static void main(String[] args)
	{
		getDSPs();
	}
	
}
