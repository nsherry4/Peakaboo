package org.peakaboo.dataset.source.plugin;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.source.plugin.plugins.PlainText;
import org.peakaboo.dataset.source.plugin.plugins.SingleColumn;
import org.peakaboo.dataset.source.plugin.plugins.universalhdf5.UniversalHDF5DataSource;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class DataSourceRegistry extends BoltPluginRegistry<DataSourcePlugin> {

	private static DataSourceRegistry SYSTEM;
	public static synchronized void init(File dataSourceDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new DataSourceRegistry(dataSourceDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load data source plugins", e);
		}
	}
	public static DataSourceRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	public DataSourceRegistry(File dataSourceDir) {
		super("datasource");
		
		addLoader(new BoltJarDirectoryLoader<>(this, DataSourcePlugin.class, dataSourceDir));
		
		var builtins = new BoltJavaBuiltinLoader<>(this, DataSourcePlugin.class);
		builtins.load(PlainText.class);
		builtins.load(SingleColumn.class);
		builtins.load(UniversalHDF5DataSource.class);
		addLoader(builtins);
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
