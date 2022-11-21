package org.peakaboo.ui.swing.plotting;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menuitem.FluentMenuItem;

public class CanvasPopupMenu extends JPopupMenu {

	private FluentMenuItem quickmap;
		
	public CanvasPopupMenu(PlotPanel plot, PlotController controller, int channel) {
		
		quickmap = new FluentMenuItem("Quick Map Channel " + channel)
				.withAction(() -> SwingUtilities.invokeLater(() -> plot.actionQuickMap(channel)));
		this.add(quickmap);
		
		ITransitionSeries ts = controller.fitting().selectTransitionSeriesAtChannel(channel);
		if (ts != null) {
			FluentMenuItem annotate = new FluentMenuItem("Annotate " + ts.toString())
					.withAction(() -> plot.actionAddAnnotation(ts));
			this.add(annotate);
		}
		
	}

	
}
