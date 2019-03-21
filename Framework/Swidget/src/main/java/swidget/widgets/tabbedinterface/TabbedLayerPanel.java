package swidget.widgets.tabbedinterface;

import swidget.widgets.layerpanel.LayerPanel;

public abstract class TabbedLayerPanel extends LayerPanel {

	private TabbedInterface<TabbedLayerPanel> tabbedInterface;
	
	public TabbedLayerPanel(TabbedInterface<TabbedLayerPanel> tabbed) {
		this.tabbedInterface = tabbed;
	}

	public TabbedInterface<TabbedLayerPanel> getTabbedInterface() {
		return tabbedInterface;
	}
	
	public abstract String getTabTitle();
	public abstract void titleDoubleClicked();
	
	
	
}
