package org.peakaboo.ui.swing.plotting.statusbar;

import java.awt.BorderLayout;

import javax.swing.JPopupMenu;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.ZoomSlider;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;

public class PlotZoomControls extends FluentButton {
	
	private PlotController controller;
	
	private ZoomSlider zoomSlider;
	private ClearPanel zoomPanel;
	
	public PlotZoomControls(PlotController controller) {
		super();
		super.withTooltip("Zoom")
			.withIcon(StockIcon.FIND, Stratus.getTheme().getControlText())
			.withLayout(FluentButtonLayout.IMAGE)
			.withBordered(false);
		
		this.controller = controller;
		
		zoomPanel = new ClearPanel();
		zoomPanel.setBorder(Spacing.bMedium());
		
		zoomSlider = new ZoomSlider(10, 1000, 10, value -> controller.view().setZoom(value / 100f));
		zoomSlider.setOpaque(false);
		zoomSlider.setValue(505);
		zoomPanel.add(zoomSlider, BorderLayout.CENTER);

		JPopupMenu zoomMenu = new JPopupMenu();
		zoomMenu.setBorder(Spacing.bNone());
		zoomMenu.add(zoomPanel);
		
		this.addActionListener(e -> {
			int x = (int) ((-zoomMenu.getPreferredSize().getWidth() + this.getSize().getWidth()) / 2f);
			int y = (int)-zoomMenu.getPreferredSize().getHeight();
			zoomMenu.show(this, x, y);
		});
		
	}
	
	void setWidgetState(boolean hasData) {
		this.setEnabled(hasData);
		zoomSlider.setValueEventless((int)(controller.view().getZoom()*100));
		
	}
	
}
