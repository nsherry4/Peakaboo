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

import commonenvironment.Env;

import bolt.plugin.BoltPluginLoader;
import bolt.plugin.ClassInheritanceException;
import bolt.plugin.ClassInstantiationException;

public class DSPLoader
{

	private static BoltPluginLoader<AbstractDSP> loader;
	
	public static List<AbstractDSP> getDSPs()
	{

		try
		{
			
			if (loader == null)
			{

				loader = new BoltPluginLoader<AbstractDSP>(AbstractDSP.class);
				
				//load local jars
				loader.register();
				
				//load jars in the app data directory
				File appDataDir = Env.appDataDirectory(Version.program_name);
				appDataDir.mkdirs();
				loader.register(appDataDir);
					
				
				//register built-in plugins
				loader.registerPlugin(CDFMLSaxDSP.class);
				loader.registerPlugin(MCA_DSP.class);
				loader.registerPlugin(PlainTextDSP.class);
				loader.registerPlugin(ScienceStudioDSP.class);
								
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
