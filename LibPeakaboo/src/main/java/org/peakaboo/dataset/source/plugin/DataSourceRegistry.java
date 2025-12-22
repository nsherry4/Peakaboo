package org.peakaboo.dataset.source.plugin;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooPluginRegistry;
import org.peakaboo.dataset.source.plugin.plugins.PlainText;
import org.peakaboo.dataset.source.plugin.plugins.SingleColumn;
import org.peakaboo.dataset.source.plugin.plugins.universalhdf5.UniversalHDF5DataSource;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class DataSourceRegistry extends PeakabooPluginRegistry<DataSourcePlugin> {

	private static DataSourceRegistry SYSTEM;
    public static synchronized void init() {
        init(null);
    }
	public static synchronized void init(File dataSourceDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new DataSourceRegistry(dataSourceDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			OneLog.log(Level.SEVERE, "Failed to load data source plugins", e);
		}
	}
	public static DataSourceRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------

    public DataSourceRegistry() {
        super("datasource");

        var builtins = new BoltJavaBuiltinLoader<>(this, DataSourcePlugin.class);
        builtins.load(PlainText.class);
        builtins.load(SingleColumn.class);
        builtins.load(UniversalHDF5DataSource.class);
        // Used to test how Peakaboo handles conflicting data sources. This should normally be commented out
        //builtins.load(YesToEverything.class, PluginDescriptor.WEIGHT_HIGHEST);
        addLoader(builtins);

        // Load plugins from within an AIO jar containing the app + plugins
        addLoader(new BoltJarDirectoryLoader<>(this, DataSourcePlugin.class));

    }

	public DataSourceRegistry(File dataSourceDir) {
		this();
        if (dataSourceDir != null) {
            addLoader(new BoltJarDirectoryLoader<>(this, DataSourcePlugin.class, dataSourceDir));
        }
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
