package peakaboo.datasink.plugin;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.java.BoltJavaPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.scripting.plugin.IBoltScriptPluginLoader;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.common.PluginManager;
import peakaboo.datasink.plugin.plugins.CSV;

public class DataSinkPluginManager extends PluginManager<DataSinkPlugin>
{

	public static DataSinkPluginManager SYSTEM = new DataSinkPluginManager();
	

	@Override
	protected void loadPlugins() {
		try {
			
			BoltJavaPluginLoader<JavaDataSinkPlugin> javaLoader = new BoltJavaPluginLoader<JavaDataSinkPlugin>(getPlugins(), JavaDataSinkPlugin.class);  
			
			//load local jars
			javaLoader.register();
			
			//load jars in the app data directory
			File appDataDir = Configuration.appDir("Plugins/DataSink");
			appDataDir.mkdirs();
			javaLoader.register(appDataDir);
				
			//register built-in plugins
			javaLoader.registerPlugin(CSV.class);
			
			
			//Load plugins
			IBoltScriptPluginLoader<JavaScriptDataSinkPlugin> jsLoader = new IBoltScriptPluginLoader<>(getPlugins(), JavaScriptDataSinkPlugin.class);
			jsLoader.scanDirectory(appDataDir, ".js");
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load Data Sink plugins", e);
		}  
		

	}
	


		
}
