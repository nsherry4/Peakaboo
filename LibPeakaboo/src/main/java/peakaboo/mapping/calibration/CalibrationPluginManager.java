package peakaboo.mapping.calibration;

import java.io.File;

import net.sciencestudio.bolt.plugin.config.IBoltConfigPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltClassloaderPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import peakaboo.curvefit.peak.table.PeakTable;

public class CalibrationPluginManager extends BoltPluginManager<CalibrationReference> {

	
	public static CalibrationPluginManager SYSTEM;
	
	public static void init(File filterDir) {
		if (SYSTEM == null) {
			SYSTEM = new CalibrationPluginManager(filterDir);
			SYSTEM.load();
		}
	}
	
	
	public CalibrationPluginManager(File directories) {
		super(directories);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadCustomPlugins() {
				
		BoltFilesytstemPluginLoader<? extends CalibrationReference> loader = filesystemLoader(getPlugins());
		loader.registerURL(getClass().getResource("/peakaboo/mapping/references/NIST610.yaml"));
				
	}

	@Override
	protected BoltClassloaderPluginLoader<? extends CalibrationReference> javaLoader(
			BoltPluginSet<CalibrationReference> pluginset) throws ClassInheritanceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BoltFilesytstemPluginLoader<? extends CalibrationReference> filesystemLoader(
			BoltPluginSet<CalibrationReference> pluginset) {
		// TODO Auto-generated method stub
		return new IBoltConfigPluginLoader<CalibrationReference>(getPlugins(), CalibrationReference.class, "yaml", CalibrationReference::load);
	}

}
