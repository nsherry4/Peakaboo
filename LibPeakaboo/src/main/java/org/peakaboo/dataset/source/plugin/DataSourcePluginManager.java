package org.peakaboo.dataset.source.plugin;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.source.plugin.plugins.PlainText;
import org.peakaboo.dataset.source.plugin.plugins.SingleColumn;
import org.peakaboo.dataset.source.plugin.plugins.universalhdf5.UniversalHDF5DataSource;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class DataSourcePluginManager extends BoltPluginManager<DataSourcePlugin> {

	private static DataSourcePluginManager SYSTEM;
	public static synchronized void init(File dataSourceDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new DataSourcePluginManager(dataSourceDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load data source plugins", e);
		}
	}
	public static DataSourcePluginManager system() {
		return SYSTEM;
	}
	
	
	private BoltJavaBuiltinLoader<DataSourcePlugin> builtins;
	
	public DataSourcePluginManager(File dataSourceDir) {
		super("datasource");
		
		addLoader(new BoltJarDirectoryLoader<>(this, DataSourcePlugin.class, dataSourceDir));
		addLoader(new BoltJarDirectoryLoader<>(this, DataSourcePlugin.class));
		
		builtins = new BoltJavaBuiltinLoader<>(this, DataSourcePlugin.class);
		registerCustomPlugins();
		addLoader(builtins);
	}
	
	private void registerCustomPlugins() {
		builtins.load(PlainText.class);
		builtins.load(SingleColumn.class);
		builtins.load(UniversalHDF5DataSource.class);
	}
	
	public void registerPlugin(Class<? extends DataSourcePlugin> clazz) {
		builtins.load(clazz);
		reload();
	}


	
	@Override
	public String getInterfaceDescription() {
		return "Data Sources are ways to load data into Peakaboo from a file (or files). This can be used to add support for new file formats.";
	}
	
	@Override
	public String getInterfaceName() {
		return "Data Source";
	}

		
}