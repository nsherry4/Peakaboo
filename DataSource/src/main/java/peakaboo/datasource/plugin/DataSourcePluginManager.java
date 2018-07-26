package peakaboo.datasource.plugin;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.java.BoltJavaPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.scripting.plugin.IBoltScriptPluginLoader;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.common.PluginManager;
import peakaboo.datasource.plugin.plugins.PlainText;

public class DataSourcePluginManager extends PluginManager<DataSourcePlugin>
{

	public static DataSourcePluginManager SYSTEM = new DataSourcePluginManager();
	
	protected synchronized void loadPlugins() {

		
		BoltJavaPluginLoader<JavaDataSourcePlugin> javaLoader;
		try {
						
			javaLoader = new BoltJavaPluginLoader<JavaDataSourcePlugin>(getPlugins(), JavaDataSourcePlugin.class);
					
			//load local jars
			javaLoader.register();
			
			//load jars in the app data directory
			File appDataDir = Configuration.appDir("Plugins/DataSource");
			appDataDir.mkdirs();
			javaLoader.register(appDataDir);

			//register built-in plugins
			javaLoader.registerPlugin(PlainText.class);
			
			//Loads JS plugins
			IBoltScriptPluginLoader<JavaScriptDataSourcePlugin> jsLoader = new IBoltScriptPluginLoader<>(getPlugins(), JavaScriptDataSourcePlugin.class);
			jsLoader.scanDirectory(appDataDir, ".js");
					
			//Log info for plugins
			for (BoltPluginController<? extends DataSourcePlugin> plugin : getPlugins().getAll()) {
				PeakabooLog.get().info("Found DataSource Plugin " + plugin.getName() + " from " + plugin.getSource());
			}
			
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load Data Source plugins", e);
		}  
		


	}
	
	public synchronized void registerPlugin(Class<? extends JavaDataSourcePlugin> clazz) {
		try {
			BoltJavaPluginLoader<JavaDataSourcePlugin> javaLoader = new BoltJavaPluginLoader<JavaDataSourcePlugin>(super.getPlugins(), JavaDataSourcePlugin.class);
			BoltPluginController<JavaDataSourcePlugin> plugin = javaLoader.registerPlugin(clazz);
			if (plugin != null) {
				PeakabooLog.get().info("Registered DataSource Plugin " + plugin.getName() + " from " + plugin.getSource());
			}
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Error registering data source plugin " + clazz.getName(), e);
		}
	}
	


		
}
