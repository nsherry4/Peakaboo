package org.peakaboo.ui.swing.plotting.fitting.guidedfitting;



import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;
import org.peakaboo.ui.swing.plotting.PlotCanvas;
import org.peakaboo.ui.swing.plotting.fitting.AbstractFittingPanel;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;



public class GuidedFittingPanel extends AbstractFittingPanel {

	private PlotCanvas				canvas;
	private Cursor					canvasCursor;

	private GuidedFittingWidget		guidedWidget;

	private List<ITransitionSeries>	potentials;
	
	private ButtonLinker			shellControls;
	private ButtonGroup				g;
	private Optional<TransitionShell> shellFilter = Optional.empty();


	public GuidedFittingPanel(final FittingController controller, final CurveFittingView owner, PlotCanvas canvas) {
		super(controller, owner, "Fittings", "Click Plot to Fit");
		this.canvas = canvas;

		potentials = new ArrayList<>();

		guidedWidget = new GuidedFittingWidget(controller);
		guidedWidget.setBorder(Spacing.bMedium());

		setBody(guidedWidget);
		
		var shellAll = styleShellButton(new FluentToggleButton("All"));
		var shellK = styleShellButton(new FluentToggleButton("K"));
		var shellL = styleShellButton(new FluentToggleButton("L"));
		var shellM = styleShellButton(new FluentToggleButton("M"));
		
		styleShellButton(shellAll);
		
		shellAll.withAction(() -> setShellFilter(Optional.empty()));
		shellK.withAction(() -> setShellFilter(Optional.of(TransitionShell.K)));
		shellL.withAction(() -> setShellFilter(Optional.of(TransitionShell.L)));
		shellM.withAction(() -> setShellFilter(Optional.of(TransitionShell.M)));
		
		g = new ButtonGroup();
		g.add(shellAll);
		g.add(shellK);
		g.add(shellL);
		g.add(shellM);
		g.setSelected(shellAll.getModel(), true);
		
		shellControls = new ButtonLinker(shellAll, shellK, shellL, shellM);
		shellControls.setBorder(Spacing.bSmall());
		this.add(shellControls, BorderLayout.SOUTH);
		
	}
	
	private void setShellFilter(Optional<TransitionShell> filter) {
		this.shellFilter = filter;
	}
	
	private Optional<TransitionShell> getShellFilter() {
		return shellFilter;
	}
	
	private FluentToggleButton styleShellButton(FluentToggleButton b) {
		b.withButtonSize(FluentButtonSize.COMPACT).withBordered(true);
		b.setFont(b.getFont().deriveFont(Font.BOLD));
		b.setBorder(new EmptyBorder(Spacing.medium, Spacing.huge, Spacing.medium, Spacing.huge));
		return b;
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
						guidedWidget.getActiveTransitionSeries(),
						// So that we look up the active shell filter, not the one that 
						// was set when this callback was created
						this::getShellFilter
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