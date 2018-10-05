package peakaboo.mapping.calibration;

import java.io.File;

import net.sciencestudio.bolt.plugin.config.IBoltConfigPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltClassloaderPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltDirectoryManager;
import net.sciencestudio.bolt.plugin.core.BoltFilesystemDirectoryManager;
import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;

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
	protected BoltClassloaderPluginLoader<? extends CalibrationReference> classpathLoader(
			BoltPluginSet<CalibrationReference> pluginset) throws ClassInheritanceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BoltFilesytstemPluginLoader<? extends CalibrationReference> filesystemLoader(
			BoltPluginSet<CalibrationReference> pluginset) {
		// TODO Auto-generated method stub
		return new IBoltConfigPluginLoader<CalibrationReference>(pluginset, CalibrationReference.class, "yaml", CalibrationReference::load);
	}
	
	@Override
	protected BoltDirectoryManager<CalibrationReference> classloaderDirectoryManager() {
		return null;
	}

	@Override
	protected BoltDirectoryManager<CalibrationReference> filesystemDirectoryManager() {
		return new BoltFilesystemDirectoryManager<>(this, getDirectory());
	}

}
