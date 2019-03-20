package org.peakaboo.datasink.plugin;

import java.io.File;

import org.peakaboo.datasink.plugin.plugins.CSV;

import net.sciencestudio.bolt.plugin.java.loader.BoltJavaBuiltinLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.java.loader.BoltJarDirectoryLoader;

public class DataSinkPluginManager extends BoltPluginManager<DataSinkPlugin> {

	public static DataSinkPluginManager SYSTEM;
	public static void init(File dataSinkDir) {
		if (SYSTEM == null) {
			SYSTEM = new DataSinkPluginManager(dataSinkDir);
			SYSTEM.load();
		}
	}
	

	
	private BoltJavaBuiltinLoader<JavaDataSinkPlugin> builtins;
	
	public DataSinkPluginManager(File dataSinkDir) {
		super(DataSinkPlugin.class);
		
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSinkPlugin.class, dataSinkDir));
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSinkPlugin.class));
		
		builtins = new BoltJavaBuiltinLoader<>(JavaDataSinkPlugin.class);
		registerCustomPlugins();
		addLoader(builtins);
		//TODO: Add script loader
	}
	
	private void registerCustomPlugins() {
		builtins.load(CSV.class);
	}

	public synchronized void registerPlugin(Class<? extends JavaDataSinkPlugin> clazz) {
		builtins.load(clazz);
		reload();
	}

	
	@Override
	public String getInterfaceDescription() {
		return "Data Sinks are ways to save data loaded by Peakaboo back to a file (or files). This can be useful for file format conversion.";
	}
	
	@Override
	public String getInterfaceName() {
		return "Data Sink";
	}

		
}
