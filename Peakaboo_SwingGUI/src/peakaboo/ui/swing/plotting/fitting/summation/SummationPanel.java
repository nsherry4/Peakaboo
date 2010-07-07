package peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import peakaboo.controller.plotter.FittingController;
import peakaboo.ui.swing.plotting.fitting.Changeable;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;

import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class SummationPanel extends ClearPanel
{

	protected SummationWidget		summationWidget;

	public SummationPanel(final FittingController controller, final CurveFittingView owner)
	{
		
		SelectionListControls selControls = new SelectionListControls("Summation") {

			@Override
			protected void cancel()
			{
				summationWidget.resetSelectors();
				
				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();
				
				owner.dialogClose();

			}


			@Override
			protected void approve()
			{
				controller.addTransitionSeries(summationWidget.getTransitionSeries());

				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();

				owner.dialogClose();
				
			}
		};

		this.setLayout(new BorderLayout());

		summationWidget = new SummationWidget(controller);
		JScrollPane scroll = new JScrollPane(summationWidget);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBorder(Spacing.bMedium());

		this.add(scroll, BorderLayout.CENTER);


		this.add(new TitleGradientPanel("Add Summation Fitting", true, selControls), BorderLayout.NORTH);



	}


	public void resetSelectors()
	{
		summationWidget.resetSelectors();
	}
	

}
