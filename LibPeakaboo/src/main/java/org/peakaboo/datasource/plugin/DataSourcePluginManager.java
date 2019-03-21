package org.peakaboo.datasource.plugin;

import java.io.File;

import org.peakaboo.datasource.plugin.plugins.PlainText;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class DataSourcePluginManager extends BoltPluginManager<DataSourcePlugin> {

	public static DataSourcePluginManager SYSTEM;
	public synchronized static void init(File dataSourceDir) {
		if (SYSTEM == null) {
			SYSTEM = new DataSourcePluginManager(dataSourceDir);
			SYSTEM.load();
		}
	}
	
	
	
	private BoltJavaBuiltinLoader<JavaDataSourcePlugin> builtins;
	
	public DataSourcePluginManager(File dataSourceDir) {
		super(DataSourcePlugin.class);
		
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSourcePlugin.class, dataSourceDir));
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSourcePlugin.class));
		
		builtins = new BoltJavaBuiltinLoader<>(JavaDataSourcePlugin.class);
		registerCustomPlugins();
		addLoader(builtins);
		//TODO: Add script loader
	}
	
	private void registerCustomPlugins() {
		builtins.load(PlainText.class);
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
