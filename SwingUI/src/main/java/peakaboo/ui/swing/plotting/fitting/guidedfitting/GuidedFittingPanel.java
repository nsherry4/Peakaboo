package peakaboo.ui.swing.plotting.fitting.guidedfitting;



import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.ui.swing.plotting.PlotCanvas;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class GuidedFittingPanel extends JPanel
{

	private IFittingController		controller;
	private PlotCanvas				canvas;

	private Cursor					canvasCursor;

	private SelectionListControls	selControls;
	private GuidedFittingWidget		guidedWidget;

	List<TransitionSeries>			potentials;


	public GuidedFittingPanel(final IFittingController controller, final CurveFittingView owner, PlotCanvas canvas)
	{
		this.controller = controller;
		this.canvas = canvas;

		potentials = new ArrayList<TransitionSeries>();

		selControls = new SelectionListControls("Fittings") {

			@Override
			protected void cancel()
			{
				controller.clearProposedTransitionSeries();
				owner.dialogClose();
			}


			@Override
			protected void approve()
			{
				controller.commitProposedTransitionSeries();
				owner.dialogClose();
			}
		};
		selControls.setOpaque(false);

		this.setLayout(new BorderLayout());

		guidedWidget = new GuidedFittingWidget(controller);
		guidedWidget.setBorder(Spacing.bMedium());
		JScrollPane scroll = new JScrollPane(guidedWidget);
		scroll.setBorder(Spacing.bNone());
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBackground(getBackground());
		scroll.getViewport().setBackground(getBackground());
		

		this.add(scroll, BorderLayout.CENTER);
		this.add(new TitleGradientPanel("Click Plot to Fit", true, selControls), BorderLayout.NORTH);

		

		
	}


	public void setSelectionMode(boolean mode)
	{
		if (mode)
		{
			guidedWidget.setTransitionSeriesOptions(null);
			canvas.grabChannelFromClick((Integer channel) -> {
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
			canvas.grabChannelFromClick(null);
			guidedWidget.setTransitionSeriesOptions(null);
			canvas.setCursor(canvasCursor);

		}
	}
	
	public void resetSelectors()
	{
		guidedWidget.resetSelectors(true);
	}

}