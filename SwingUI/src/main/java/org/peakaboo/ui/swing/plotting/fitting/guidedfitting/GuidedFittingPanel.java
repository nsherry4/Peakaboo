package org.peakaboo.ui.swing.plotting.fitting.guidedfitting;



import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.ui.swing.plotting.PlotCanvas;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;

import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class GuidedFittingPanel extends JPanel
{

	private FittingController		controller;
	private PlotCanvas				canvas;

	private Cursor					canvasCursor;

	private SelectionListControls	selControls;
	private GuidedFittingWidget		guidedWidget;

	private List<ITransitionSeries>	potentials;


	public GuidedFittingPanel(final FittingController controller, final CurveFittingView owner, PlotCanvas canvas)
	{
		this.controller = controller;
		this.canvas = canvas;

		potentials = new ArrayList<ITransitionSeries>();

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
		this.add(new TitlePaintedPanel("Click Plot to Fit", false, selControls), BorderLayout.NORTH);

		

		
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