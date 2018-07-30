package peakaboo.ui.swing.plotting.statusbar;

import java.awt.BorderLayout;

import javax.swing.JPopupMenu;

import eventful.EventfulListener;
import peakaboo.controller.plotter.PlotController;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.ZoomSlider;
import swidget.widgets.toggle.ImageToggleButton;

public class PlotZoomControls extends ImageButton {
	
	private PlotController controller;
	
	private ZoomSlider zoomSlider;
	private ClearPanel zoomPanel;
	
	public PlotZoomControls(PlotController controller) {
		super(StockIcon.FIND, "Zoom", Layout.IMAGE, false);
		
		this.controller = controller;
		
		zoomPanel = new ClearPanel();
		zoomPanel.setBorder(Spacing.bMedium());
		
		zoomSlider = new ZoomSlider(10, 1000, 10);
		zoomSlider.setOpaque(false);
		zoomSlider.setValue(100);
		zoomSlider.addListener(new EventfulListener() {
			
			public void change()
			{
				controller.view().setZoom(zoomSlider.getValue() / 100f);
			}
		});
		zoomPanel.add(zoomSlider, BorderLayout.CENTER);

		
		final ImageToggleButton lockHorizontal = new ImageToggleButton(StockIcon.MISC_LOCKED, "", "Lock Vertical Zoom to Window Size");
		lockHorizontal.setSelected(true);
		lockHorizontal.addActionListener(e -> {
			controller.view().setLockPlotHeight(lockHorizontal.isSelected());
		});
		zoomPanel.add(lockHorizontal, BorderLayout.EAST);
		
		JPopupMenu zoomMenu = new JPopupMenu();
		zoomMenu.setBorder(Spacing.bNone());
		zoomMenu.add(zoomPanel);
		
		this.addActionListener(e -> {
			zoomMenu.show(this, (int)((-zoomMenu.getPreferredSize().getWidth()+this.getSize().getWidth())/2f), (int)-zoomMenu.getPreferredSize().getHeight());
		});
		
	}
	
	void setWidgetState(boolean hasData) {
		this.setEnabled(hasData);
		zoomSlider.setValueEventless((int)(controller.view().getZoom()*100));
		
	}
	
}
