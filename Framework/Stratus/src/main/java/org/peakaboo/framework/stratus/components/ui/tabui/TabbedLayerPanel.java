package org.peakaboo.framework.stratus.components.ui.tabui;

import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;

public abstract class TabbedLayerPanel extends LayerPanel {

	private TabbedInterface<TabbedLayerPanel> tabbedInterface;
	
	public TabbedLayerPanel(TabbedInterface<TabbedLayerPanel> tabbed) {
		super(true);
		this.tabbedInterface = tabbed;
	}

	public TabbedInterface<TabbedLayerPanel> getTabbedInterface() {
		return tabbedInterface;
	}
	
	public abstract String getTabTitle();
	public abstract void titleDoubleClicked();
	
	
	
}
