package org.peakaboo.ui.swing.plotting.fitting.guidedfitting;



import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.ui.swing.plotting.PlotCanvas;
import org.peakaboo.ui.swing.plotting.fitting.AbstractFittingPanel;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;



public class GuidedFittingPanel extends AbstractFittingPanel {

	private PlotCanvas				canvas;
	private Cursor					canvasCursor;

	private GuidedFittingWidget		guidedWidget;

	private List<ITransitionSeries>	potentials;


	public GuidedFittingPanel(final FittingController controller, final CurveFittingView owner, PlotCanvas canvas) {
		super(controller, owner, "Fittings", "Click Plot to Fit");
		this.canvas = canvas;

		potentials = new ArrayList<>();

		guidedWidget = new GuidedFittingWidget(controller);
		guidedWidget.setBorder(Spacing.bMedium());

		setBody(guidedWidget);
	}


	@Override
	public void setActive(boolean isActive) {
		if (isActive)
		{
			guidedWidget.setTransitionSeriesOptions(null);
			canvas.setSingleClickCallback((channel, coords) -> {
				canvas.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				
				potentials = controller.proposeTransitionSeriesFromChannel(
						channel, 
						guidedWidget.getActiveTransitionSeries()
					);
				guidedWidget.setTransitionSeriesOptions(potentials);
				
				canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

			});

			canvasCursor = canvas.getCursor();
			canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

		}
		else
		{
			canvas.setSingleClickCallback(null);
			guidedWidget.setTransitionSeriesOptions(null);
			canvas.setCursor(canvasCursor);

		}
	}
	
	public void resetSelectors() {
		guidedWidget.clearSelectors(true);
	}


	@Override
	protected void onAccept() {
		this.controller.commitProposedTransitionSeries();
		this.guidedWidget.clearSelectors(false);
		this.owner.dialogClose();
	}


	@Override
	protected void onCancel() {
		this.controller.clearProposedTransitionSeries();
		this.guidedWidget.clearSelectors(false);
		this.owner.dialogClose();
	}

}