package org.peakaboo.dataset.sink.plugin;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.PeakabooPluginRegistry;
import org.peakaboo.dataset.sink.plugin.plugins.CSV;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class DataSinkRegistry extends PeakabooPluginRegistry<DataSinkPlugin> {

	private static DataSinkRegistry SYSTEM;
	public static synchronized void init() {
		init(null);
	}
	public static void init(File dataSinkDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new DataSinkRegistry(dataSinkDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load data sink plugins", e);
		}
	}
	public static DataSinkRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------

	public DataSinkRegistry() {
		super("datasink");
		var builtins = new BoltJavaBuiltinLoader<>(this, DataSinkPlugin.class);
		builtins.load(CSV.class);
		addLoader(builtins);

		// Load plugins from within an AIO jar containing the app + plugins
		// Disabled for android compatibility, and because this is unused in the desktop app
		//addLoader(new BoltJarDirectoryLoader<>(this, DataSinkPlugin.class));
	}

	public DataSinkRegistry(File dataSinkDir) {
		this();
		if (dataSinkDir != null) {
			addLoader(new BoltJarDirectoryLoader<>(this, DataSinkPlugin.class, dataSinkDir));
		}
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
