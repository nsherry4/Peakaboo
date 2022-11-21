package org.peakaboo.ui.swing.plotting.statusbar;

import java.awt.BorderLayout;

import javax.swing.JPopupMenu;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.ZoomSlider;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;

public class PlotZoomControls extends FluentButton {
	
	private PlotController controller;
	
	private ZoomSlider zoomSlider;
	private ClearPanel zoomPanel;
	
	public PlotZoomControls(PlotController controller) {
		super(StockIcon.FIND);
		super.withTooltip("Zoom")
			.withLayout(FluentButtonLayout.IMAGE)
			.withBordered(false);
		
		this.controller = controller;
		
		zoomPanel = new ClearPanel();
		zoomPanel.setBorder(Spacing.bMedium());
		
		zoomSlider = new ZoomSlider(10, 1000, 10, value -> controller.view().setZoom(value / 100f));
		zoomSlider.setOpaque(false);
		zoomSlider.setValue(100);
		zoomPanel.add(zoomSlider, BorderLayout.CENTER);

		
		FluentToggleButton lockHorizontal = new FluentToggleButton("", StockIcon.MISC_LOCKED)
				.withTooltip("Lock Vertical Zoom to Window Size")
				.withSelected(true)
				.withAction(controller.view()::setLockPlotHeight);
		zoomPanel.add(lockHorizontal, BorderLayout.EAST);
		
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
