package org.peakaboo.calibration;

import java.io.File;

import org.peakaboo.framework.bolt.plugin.config.loader.BoltConfigBuiltinLoader;
import org.peakaboo.framework.bolt.plugin.config.loader.BoltConfigDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;

public class CalibrationPluginManager extends BoltPluginManager<CalibrationReference> {

	public static CalibrationPluginManager SYSTEM;
	public static void init(File filterDir) {
		if (SYSTEM == null) {
			SYSTEM = new CalibrationPluginManager(filterDir);
			SYSTEM.load();
		}
	}
	
	
	private BoltConfigBuiltinLoader<CalibrationReference> builtins;
	
	public CalibrationPluginManager(File directories) {
		super(CalibrationReference.class);
		
		addLoader(new BoltConfigDirectoryLoader<>(
				CalibrationReference.class, 
				directories, 
				".yaml", 
				CalibrationReference::load
			));
		
		builtins = new BoltConfigBuiltinLoader<>(
				CalibrationReference.class, 
				CalibrationReference::load
			);
		registerCustomPlugins();
		addLoader(builtins);
		
	}

	private void registerCustomPlugins() {
		builtins.load(getClass().getResource("/org/peakaboo/calibration/references/NIST610.yaml"));			
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
