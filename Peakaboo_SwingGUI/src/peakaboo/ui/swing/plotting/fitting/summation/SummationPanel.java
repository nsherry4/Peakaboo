package peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fava.Fn;
import fava.signatures.FnEach;

import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;

import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class SummationPanel extends JPanel
{

	protected SummationWidget	summationWidget;


	public SummationPanel(final IFittingController controller, final CurveFittingView owner)
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
				//add all of the transition series that come back from the summation widget
				Fn.each(summationWidget.getTransitionSeries(), new FnEach<TransitionSeries>() {

					public void f(TransitionSeries ts)
					{
						controller.addTransitionSeries(ts);
					}
				});


				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();

				owner.dialogClose();

			}
		};
		selControls.setOpaque(false);
		
		this.setLayout(new BorderLayout());

		summationWidget = new SummationWidget(controller);
		summationWidget.setBorder(Spacing.bMedium());
		JScrollPane scroll = new JScrollPane(summationWidget);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.getViewport().setOpaque(false);
		
		this.add(scroll, BorderLayout.CENTER);


		this.add(new TitleGradientPanel("Add Summation Fitting", true, selControls), BorderLayout.NORTH);



	}


	public void resetSelectors()
	{
		summationWidget.resetSelectors();
	}


}
