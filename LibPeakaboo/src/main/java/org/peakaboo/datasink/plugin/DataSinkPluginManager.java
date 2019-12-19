package org.peakaboo.datasink.plugin;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasink.plugin.plugins.CSV;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class DataSinkPluginManager extends BoltPluginManager<DataSinkPlugin> {

	private static DataSinkPluginManager SYSTEM;
	public static void init(File dataSinkDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new DataSinkPluginManager(dataSinkDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load data sink plugins", e);
		}
	}
	public static DataSinkPluginManager system() {
		return SYSTEM;
	}

	
	private BoltJavaBuiltinLoader<JavaDataSinkPlugin> builtins;
	
	public DataSinkPluginManager(File dataSinkDir) {
		
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSinkPlugin.class, dataSinkDir));
		addLoader(new BoltJarDirectoryLoader<>(JavaDataSinkPlugin.class));
		
		builtins = new BoltJavaBuiltinLoader<>(JavaDataSinkPlugin.class);
		registerCustomPlugins();
		addLoader(builtins);
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
