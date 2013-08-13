package peakaboo.ui.swing.plotting.fitting.summation;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fava.signatures.FnEach;
import peakaboo.curvefit.controller.IFittingController;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class SummationPanel extends JPanel
{

	protected SummationWidget	summationWidget;
	boolean active = true;

	public SummationPanel(final IFittingController controller, final CurveFittingView owner)
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
				summationWidget.getTransitionSeries().each(new FnEach<TransitionSeries>() {

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

		summationWidget = new SummationWidget(controller, this);
		summationWidget.setBorder(Spacing.bMedium());
		JScrollPane scroll = new JScrollPane(summationWidget);
		scroll.setBorder(Spacing.bNone());
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBackground(getBackground());
		scroll.getViewport().setBackground(getBackground());
		
		this.add(scroll, BorderLayout.CENTER);


		this.add(new TitleGradientPanel("Add Summation Fitting", true, selControls), BorderLayout.NORTH);



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
