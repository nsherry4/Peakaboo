package peakaboo.datasource.plugin;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltClassloaderPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.java.IBoltJavaPluginLoader;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import peakaboo.common.PeakabooLog;
import peakaboo.datasource.plugin.plugins.PlainText;

public class DataSourcePluginManager extends BoltPluginManager<DataSourcePlugin>
{

	public static DataSourcePluginManager SYSTEM;
	
	public synchronized static void init(File dataSourceDir) {
		if (SYSTEM == null) {
			SYSTEM = new DataSourcePluginManager(dataSourceDir);
			SYSTEM.load();
		}
	}
	
	public DataSourcePluginManager(File dataSourceDir) {
		super(dataSourceDir);
	}
	
	protected synchronized void loadCustomPlugins() {

		
		;
		try {
						
			BoltClassloaderPluginLoader<JavaDataSourcePlugin> javaLoader = javaLoader(getPlugins());
					
			//register built-in plugins
			javaLoader.registerPlugin(PlainText.class);

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
			BoltClassloaderPluginLoader<JavaDataSourcePlugin> javaLoader = javaLoader(getPlugins());
			BoltPluginController<JavaDataSourcePlugin> plugin = javaLoader.registerPlugin(clazz);
			if (plugin != null) {
				PeakabooLog.get().info("Registered DataSource Plugin " + plugin.getName() + " from " + plugin.getSource());
			}
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.WARNING, "Error registering data source plugin " + clazz.getName(), e);
		}
	}

	@Override
	protected BoltClassloaderPluginLoader<JavaDataSourcePlugin> javaLoader(BoltPluginSet<DataSourcePlugin> pluginset) throws ClassInheritanceException {
		return new IBoltJavaPluginLoader<JavaDataSourcePlugin>(pluginset, JavaDataSourcePlugin.class);
	}

	@Override
	protected BoltFilesytstemPluginLoader<? extends DataSourcePlugin> filesystemLoader(BoltPluginSet<DataSourcePlugin> pluginset) {
		return null;
	}
	


		
}
