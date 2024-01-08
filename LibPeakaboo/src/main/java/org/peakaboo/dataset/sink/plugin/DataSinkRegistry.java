package org.peakaboo.dataset.sink.plugin;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.sink.plugin.plugins.CSV;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class DataSinkRegistry extends BoltPluginRegistry<DataSinkPlugin> {

	private static DataSinkRegistry SYSTEM;
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
	
	public DataSinkRegistry(File dataSinkDir) {
		super("datasink");
		
		addLoader(new BoltJarDirectoryLoader<>(this, DataSinkPlugin.class, dataSinkDir));
		addLoader(new BoltJarDirectoryLoader<>(this, DataSinkPlugin.class));
		
		var builtins = new BoltJavaBuiltinLoader<>(this, DataSinkPlugin.class);
		builtins.load(CSV.class);
		addLoader(builtins);
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
