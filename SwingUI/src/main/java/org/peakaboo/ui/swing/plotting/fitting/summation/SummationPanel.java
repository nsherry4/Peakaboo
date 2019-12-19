package org.peakaboo.ui.swing.plotting.fitting.summation;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.listcontrols.SelectionListControls;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;



public class SummationPanel extends JPanel {

	private SummationWidget	summationWidget;
	boolean active = true;

	public SummationPanel(final FittingController controller, final CurveFittingView owner) {
		
		SelectionListControls selControls = new SelectionListControls("Summation", "Add Summation") {

			@Override
			protected void cancel() {
				SummationPanel.this.active = false;
				summationWidget.resetSelectors(active);

				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();

				owner.dialogClose();
			}


			@Override
			protected void approve() {
				//add all of the transition series that come back from the summation widget
				summationWidget.getTransitionSeries().forEach(controller::addTransitionSeries);


				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();

				owner.dialogClose();
			}
		};
		selControls.setOpaque(false);
		
		this.setLayout(new BorderLayout());

		summationWidget = new SummationWidget(controller, this);
		summationWidget.setBorder(Spacing.bMedium());
		JScrollPane scroll = new JScrollPane(summationWidget);
		scroll.setBorder(Spacing.bNone());
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBackground(getBackground());
		scroll.getViewport().setBackground(getBackground());
		
		this.add(scroll, BorderLayout.CENTER);


		this.add(selControls, BorderLayout.NORTH);



	}

	public void setActive(boolean active) {
		this.active = active;
	}
	

	public void resetSelectors() {
		summationWidget.resetSelectors(active);
	}


}
