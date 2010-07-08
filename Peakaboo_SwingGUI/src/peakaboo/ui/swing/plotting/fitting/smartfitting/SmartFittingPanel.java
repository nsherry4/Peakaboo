package peakaboo.ui.swing.plotting.fitting.smartfitting;



import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JScrollPane;

import fava.signatures.FunctionEach;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.ui.swing.plotting.PlotCanvas;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;

import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class SmartFittingPanel extends ClearPanel
{

	private FittingController		controller;
	private PlotCanvas				canvas;

	private Cursor					canvasCursor;

	private SelectionListControls	selControls;
	private SmartFittingWidget		smartWidget;

	List<TransitionSeries>			potentials;


	public SmartFittingPanel(final FittingController controller, final CurveFittingView owner, PlotCanvas canvas)
	{
		this.controller = controller;
		this.canvas = canvas;

		potentials = DataTypeFactory.<TransitionSeries> list();

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

		this.setLayout(new BorderLayout());

		smartWidget = new SmartFittingWidget(controller, canvas);
		JScrollPane scroll = new JScrollPane(smartWidget);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBorder(Spacing.bMedium());

		this.add(scroll, BorderLayout.CENTER);
		this.add(new TitleGradientPanel("Click Plot to Fit", true, selControls), BorderLayout.NORTH);

	}


	public void setSelectionMode(boolean mode)
	{
		if (mode)
		{
			smartWidget.setTransitionSeriesOptions(null);
			canvas.grabChannelFromClick(new FunctionEach<Integer>() {

				public void f(Integer channel)
				{
					potentials = controller.proposeTransitionSeriesFromChannel(
							channel, 
							smartWidget.getActiveTransitionSeries()
						);
					smartWidget.setTransitionSeriesOptions(potentials);

				}
			});

			canvasCursor = canvas.getCursor();
			canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

		}
		else
		{
			canvas.grabChannelFromClick(null);
			smartWidget.setTransitionSeriesOptions(null);
			canvas.setCursor(canvasCursor);

		}
	}
	
	public void resetSelectors()
	{
		smartWidget.resetSelectors();
	}

}