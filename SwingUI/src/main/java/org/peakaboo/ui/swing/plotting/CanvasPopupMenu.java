package org.peakaboo.ui.swing.plotting;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public class CanvasPopupMenu extends JPopupMenu {

	private JMenuItem quickmap;
		
	public CanvasPopupMenu(PlotPanel plot, PlotController controller, int channel) {
		
		quickmap = new JMenuItem("Quick Map Channel " + channel);
		quickmap.addActionListener(e -> SwingUtilities.invokeLater(() -> plot.actionQuickMap(channel)));
		this.add(quickmap);
		
		ITransitionSeries ts = controller.fitting().selectTransitionSeriesAtChannel(channel);
		if (ts != null) {
			JMenuItem annotate = new JMenuItem("Annotate " + ts.toString());
			annotate.addActionListener(e -> plot.actionAddAnnotation(ts));
			this.add(annotate);
		}
		
		
	}
	
	
	
}
