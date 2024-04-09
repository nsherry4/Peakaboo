package org.peakaboo.ui.swing.plotting.fitting.summation;


import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.ui.swing.plotting.PlotCanvas;
import org.peakaboo.ui.swing.plotting.fitting.AbstractFittingPanel;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;



public class SummationPanel extends AbstractFittingPanel {

	private SummationWidget	summationWidget;
	private PlotCanvas canvas;
	boolean active = true;

	public SummationPanel(final FittingController controller, final CurveFittingView owner, PlotCanvas canvas) {
		super(controller, owner, "Summation", "Add Summation");
		this.canvas = canvas;
		
		summationWidget = new SummationWidget(controller, this);
		summationWidget.setBorder(Spacing.bMedium());
		
		setBody(summationWidget);
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
		
		if (!active) {
			this.canvas.setSingleClickCallback(null);
		} else {
			// This is a bad way of signalling to the canvas that someone is doing something
			// with proposed fittings and to avoid clearing them
			this.canvas.setSingleClickCallback((i, c) -> {});
		}
		
	}
	

	public void resetSelectors() {
		summationWidget.clearSelectors(active);
	}
	
	@Override
	protected void onAccept() {
		//add all of the transition series that come back from the summation widget
		summationWidget.getTransitionSeries().forEach(controller::addTransitionSeries);

		controller.clearProposedTransitionSeries();
		controller.fittingProposalsInvalidated();
		
		this.summationWidget.clearSelectors(false);

		owner.dialogClose();
	}
	@Override
	protected void onCancel() {
		controller.clearProposedTransitionSeries();
		controller.fittingProposalsInvalidated();
		
		this.summationWidget.clearSelectors(false);

		owner.dialogClose();
	}


}
