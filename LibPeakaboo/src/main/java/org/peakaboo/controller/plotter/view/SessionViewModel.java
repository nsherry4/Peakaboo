package org.peakaboo.controller.plotter.view;

import org.peakaboo.controller.plotter.view.mode.ChannelViewMode;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;

public class SessionViewModel {

	public int					scanNumber;
	ChannelViewMode				channelView;
	public boolean				backgroundShowOriginal;
	public float				zoom;
	public boolean				logTransform;
	public boolean				showTitle;
	
	public SessionViewModel() {

		scanNumber = 0;
		channelView = ChannelViewModeRegistry.system().getPresetInstance();
		backgroundShowOriginal = false;
		zoom = 1.0f;
		logTransform = true;
		showTitle = false;
		
	}

	// TODO replace these methods with better plugin serialization? There are no
	// parameters to serialize with this, which makes it easier to do by UUID for
	// now.
	public SavedPlugin getChannelView() {
		return new SavedPlugin(this.channelView);
	}
	public void setChannelView(SavedPlugin plugin) {
		setChannelView(plugin.uuid);
	}
	public void setChannelView(String uuid) {
		var lookup = ChannelViewModeRegistry.system().getByUUID(uuid);
		if (lookup.isPresent()) {
			this.channelView = lookup.get().create();
		} else {
			this.channelView = ChannelViewModeRegistry.system().getPresetInstance();
		}
	}
	
	public ChannelViewMode channelView() {
		return this.channelView;
	}
	
	public void copy(SessionViewModel view) {
		this.scanNumber = view.scanNumber;
		this.channelView = view.channelView;
		this.backgroundShowOriginal = view.backgroundShowOriginal;
		this.zoom = view.zoom;
		this.logTransform = view.logTransform;
		this.showTitle = view.showTitle;
	}
	
}
