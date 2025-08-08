package org.peakaboo.ui.swing.plotting.fitting.auto;

import java.util.List;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.ui.swing.plotting.PlotCanvas;
import org.peakaboo.ui.swing.plotting.fitting.AbstractFittingPanel;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;

public class AutoFittingPanel extends AbstractFittingPanel {
	
	private AutoFittingWidget listing;
	private PlotCanvas canvas;
	
	public AutoFittingPanel(FittingController controller, CurveFittingView owner, PlotCanvas canvas) {
		super(controller, owner, "Fittings", "Auto-Fittings");	
		this.canvas = canvas;
		
		listing = new AutoFittingWidget(controller);
		listing.setBorder(Spacing.bLarge());
		
		setBody(listing);
		
	}

	public void setResults(List<ITransitionSeries> results) {
		listing.clearSelectors(false);
		for (var result : results) {
			listing.addTSSelector(result).setEnabled(false);
		}
	}

	@Override
	protected void onAccept() {
		this.controller.commitProposedTransitionSeries();
		this.listing.clearSelectors(false);
		this.owner.dialogClose();
	}

	@Override
	protected void onCancel() {
		this.controller.clearProposedTransitionSeries();
		this.listing.clearSelectors(false);
		this.owner.dialogClose();
	}

	@Override
	public void setActive(boolean isActive) {
		if (!isActive) {
			this.canvas.setSingleClickCallback(null);
		} else {
			// This is a bad way of signalling to the canvas that someone is doing something
			// with proposed fittings and to avoid clearing them
			this.canvas.setSingleClickCallback((i, c) -> {});
		}
	}

}
