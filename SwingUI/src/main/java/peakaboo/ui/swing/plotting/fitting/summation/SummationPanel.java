package peakaboo.ui.swing.plotting.fitting.summation;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class SummationPanel extends JPanel
{

	private SummationWidget	summationWidget;
	boolean active = true;

	public SummationPanel(final FittingController controller, final CurveFittingView owner)
	{
		
		SelectionListControls selControls = new SelectionListControls("Summation") {

			@Override
			protected void cancel()
			{
				SummationPanel.this.active = false;
				summationWidget.resetSelectors(active);

				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();

				owner.dialogClose();

			}


			@Override
			protected void approve()
			{
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


		this.add(new TitlePaintedPanel("Add Summation Fitting", false, selControls), BorderLayout.NORTH);



	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
	

	public void resetSelectors()
	{
		summationWidget.resetSelectors(active);
	}


}
