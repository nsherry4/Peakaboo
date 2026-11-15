package org.peakaboo.ui.swing.plotting;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.curvefit.curve.fitting.FittingParametersView;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menu.FluentMenuItem;
import org.peakaboo.mapping.Mapping;

public class CanvasPopupMenu extends JPopupMenu {

	private FluentMenuItem quickmap;
		
	public CanvasPopupMenu(PlotPanel plot, PlotController controller, int channel) {
		
		quickmap = new FluentMenuItem("Quick Map Channel " + channel)
				.withAction(() -> SwingUtilities.invokeLater(() -> plot.actionQuickMap(channel)));
		this.add(quickmap);

		// The window is sized from the FWHM, which needs an energy calibration
		FittingParametersView params = controller.fitting().getFittingSelections().getFittingParameters();
		if (!params.getCalibration().isZero()) {
			FluentMenuItem quickmapWindow = new FluentMenuItem("Quick Map " + Mapping.quickMapWindowDescription(params, channel))
					.withAction(() -> SwingUtilities.invokeLater(() -> plot.actionQuickMapWindow(channel)));
			this.add(quickmapWindow);
		}

		ITransitionSeries ts = controller.fitting().selectTransitionSeriesAtChannel(channel);
		if (ts != null) {
			FluentMenuItem annotate = new FluentMenuItem("Annotate " + ts.toString())
					.withAction(() -> plot.actionAddAnnotation(ts));
			this.add(annotate);
		}
		
	}

	
}
