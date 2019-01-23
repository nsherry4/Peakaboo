package org.peakaboo.calibration;

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
		loader.registerURL(getClass().getResource("/org/peakaboo/calibration/references/NIST610.yaml"));
				
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


	@Override
	public String getInterfaceDescription() {
		return "Z-Calibration References describe standard reference materials. When combined with a data set for that standard reference, they can be used to estimate and correct per-element experimental sensitifity.";
	}


	@Override
	public String getInterfaceName() {
		return "Z-Calibration Reference";
	}

}
