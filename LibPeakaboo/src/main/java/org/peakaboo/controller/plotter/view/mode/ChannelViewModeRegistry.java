package org.peakaboo.controller.plotter.view.mode;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooPluginRegistry;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginPreset;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;

public class ChannelViewModeRegistry extends PeakabooPluginRegistry<ChannelViewMode> implements PluginPreset<ChannelViewMode> {


	private static ChannelViewModeRegistry systemImpl;
	public static void init() {
		try {
			if (systemImpl == null) {
				systemImpl = new ChannelViewModeRegistry();
			}
		} catch (Exception e) {
			OneLog.log(Level.SEVERE, "Failed to load channel view plugins", e);
		}
	}
	public static ChannelViewModeRegistry system() {
		return systemImpl;
	}
	
	//--------------------------------
	
	public ChannelViewModeRegistry() {
		super("channelview");
		
		var builtins = new BoltJavaBuiltinLoader<>(this, ChannelViewMode.class);
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
	@Override
	public PluginDescriptor<ChannelViewMode> getPreset() {
		return this.getByClass(AverageViewMode.class).orElseThrow();
	}

	
}
