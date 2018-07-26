package peakaboo.datasink.plugin;

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
	
	public DataSinkPluginManager() {
		super(Configuration.appDir("Plugins/DataSink"));
	}

	@Override
	protected void loadCustomPlugins() {
		try {
			
			BoltJavaPluginLoader<JavaDataSinkPlugin> javaLoader = javaLoader(getPlugins());
			
			//register built-in plugins
			javaLoader.registerPlugin(CSV.class);

			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load Data Sink plugins", e);
		}  
		

	}

	@Override
	protected BoltJavaPluginLoader<JavaDataSinkPlugin> javaLoader(BoltPluginSet<DataSinkPlugin> pluginset) throws ClassInheritanceException {
		return new BoltJavaPluginLoader<JavaDataSinkPlugin>(pluginset, JavaDataSinkPlugin.class);
	}

	@Override
	protected IBoltScriptPluginLoader<? extends DataSinkPlugin> scriptLoader(BoltPluginSet<DataSinkPlugin> pluginset) {
		return new IBoltScriptPluginLoader<>(pluginset, JavaScriptDataSinkPlugin.class);
	}
	


		
}
