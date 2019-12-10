package org.peakaboo.datasource.plugin;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasource.plugin.plugins.PlainText;
import org.peakaboo.datasource.plugin.plugins.SingleColumn;
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
	
	
	private BoltJavaBuiltinLoader<JavaDataSourcePlugin> builtins;
	
	public DataSourcePluginManager(File dataSourceDir) {
	
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSourcePlugin.class, dataSourceDir));
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSourcePlugin.class));
		
		builtins = new BoltJavaBuiltinLoader<>(JavaDataSourcePlugin.class);
		registerCustomPlugins();
		addLoader(builtins);
	}
	
	private void registerCustomPlugins() {
		builtins.load(PlainText.class);
		builtins.load(SingleColumn.class);
	}
	
	public void registerPlugin(Class<? extends JavaDataSourcePlugin> clazz) {
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
