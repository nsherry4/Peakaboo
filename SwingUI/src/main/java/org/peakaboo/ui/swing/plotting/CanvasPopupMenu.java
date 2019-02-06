package org.peakaboo.ui.swing.plotting;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.peakaboo.controller.plotter.PlotController;

public class CanvasPopupMenu extends JPopupMenu {

	private JMenuItem quickmap;
		
	public CanvasPopupMenu(PlotCanvas canvas, PlotPanel plot, PlotController controller, int channel) {
		
		quickmap = new JMenuItem("Quick Map");
		quickmap.addActionListener(e -> {
			SwingUtilities.invokeLater(() -> {
				plot.actionQuickMap(channel);
			});
		});
		this.add(quickmap);
		
	}
	
	
	
}
