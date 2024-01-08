package org.peakaboo.controller.plotter.view.mode;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class ChannelViewModeRegistry extends BoltPluginRegistry<ChannelViewMode> {


	private static ChannelViewModeRegistry SYSTEM;
	public static void init() {
		try {
			if (SYSTEM == null) {
				SYSTEM = new ChannelViewModeRegistry();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load channel view plugins", e);
		}
	}
	public static ChannelViewModeRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	private BoltJavaBuiltinLoader<ChannelViewMode> builtins;
	
	public ChannelViewModeRegistry() {
		super("channelview");
		
		builtins = new BoltJavaBuiltinLoader<>(this, ChannelViewMode.class);
		builtins.load(AverageViewMode.class);
		builtins.load(MaximumViewMode.class);
		builtins.load(SingleViewMode.class);
		this.addLoader(builtins);
		
	}

	@Override
	public String getInterfaceName() {
		return "Channel View Modes";
	}

	@Override
	public String getInterfaceDescription() {
		return "Channel view modes determine how the spectra in a dataset are presented to the user";
	}

	
}
