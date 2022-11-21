package org.peakaboo.ui.swing.plotting.fitting;



import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.TrayIcon.MessageType;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.swing.ExecutorSetViewLayer;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.ui.swing.plotting.PlotCanvas;
import org.peakaboo.ui.swing.plotting.PlotPanel;
import org.peakaboo.ui.swing.plotting.fitting.fitted.FittingPanel;
import org.peakaboo.ui.swing.plotting.fitting.guidedfitting.GuidedFittingPanel;
import org.peakaboo.ui.swing.plotting.fitting.lookup.LookupPanel;
import org.peakaboo.ui.swing.plotting.fitting.summation.SummationPanel;




public class CurveFittingView extends ClearPanel implements Changeable {

	private FittingController	controller;
	private PlotController	 	plotController;
	private PlotPanel			plotPanel;

	private static final String		FITTED		= "Fitted";
	private static final String		LOOKUP		= "Lookup";
	private static final String		SUMMATION	= "Summation";
	private static final String		SMART		= "Smart";


	private FittingPanel		fittedPanel;
	private LookupPanel			proposalPanel;
	private SummationPanel		summationPanel;
	private GuidedFittingPanel	smartPanel;
	
	
	private JPanel				cardPanel;
	private CardLayout			card;

	
	

	public CurveFittingView(FittingController controller, PlotController plotController, PlotPanel plotPanel, PlotCanvas canvas) {
		super();
		
		this.controller = controller;
		this.plotController = plotController;
		this.plotPanel = plotPanel;

		setPreferredSize(new Dimension(200, getPreferredSize().height));

		fittedPanel = new FittingPanel(controller, this, plotPanel);
		proposalPanel = new LookupPanel(controller, this);
		summationPanel = new SummationPanel(controller, this);
		smartPanel = new GuidedFittingPanel(controller, this, canvas);

		cardPanel = createCardPanel();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(cardPanel);

		
		controller.addListener(b -> {
			//b will be true if the fitting model has been changed in some way
			//other than through the FittingController (ie load session, undo, etc)
			if (b) {
				changed();
			}
		});

	}


	@Override
	public String getName() {
		return "Peak Fitting";
	}

	
	


	public void changed() {
		fittedPanel.changed();
		proposalPanel.changed();
	}

	public void elementalAdd() {
		card.show(cardPanel, LOOKUP);
		changed();
	}
	
	public void summationAdd() {
		summationPanel.setActive(true);
		summationPanel.resetSelectors();
		card.show(cardPanel, SUMMATION);
		changed();
	}
	
	public void guidedAdd() 
	{
		if (plotController.data().hasDataSet() && plotController.fitting().getMaxEnergy() > 0f) {
			smartPanel.resetSelectors();
			smartPanel.setSelectionMode(true);
			card.show(cardPanel, SMART);
			changed();
		} else {
			new LayerDialog(
					"Missing Data Set or Energy Calibration", 
					"Guided fitting cannot proceed without a data set and energy calibration.", 
					StockIcon.BADGE_WARNING
				).showIn(plotPanel);
			
		}

	}
	
	public void autoAdd() {
		
		if (plotController.data().hasDataSet() && plotController.fitting().getMaxEnergy() > 0f) {
			ExecutorSet<List<ITransitionSeries>> exec = controller.autodetectPeaks();
			ExecutorSetViewLayer layer = new ExecutorSetViewLayer(plotPanel, exec);
			
			Mutable<Boolean> ran = new Mutable<>(false);
			exec.addListener(() -> {
				if (ran.get()) {
					return;
				}
				
				if (exec.getCompleted()) {
					ran.set(true);
					changed();
					exec.discard();
				} else if (exec.isAborted()) {
					ran.set(true);
					exec.discard();
				}
			});		
			
			plotPanel.pushLayer(layer);
			exec.startWorking();
			
		} else {
			new LayerDialog(
					"Misisng Data Set or Energy Calibration", 
					"Automatic fitting cannot proceed without a data set and energy calibration.", 
					StockIcon.BADGE_WARNING
				).showIn(plotPanel);
			
		}
	}
	

	
	
	public void dialogClose() {
		card.show(cardPanel, FITTED);
		smartPanel.setSelectionMode(false);
		summationPanel.setActive(false);
		changed();
	}
	


	private JPanel createCardPanel() {
		JPanel panel = new ClearPanel();
		card = new CardLayout();
		panel.setLayout(card);

		panel.add(fittedPanel, FITTED);
		panel.add(proposalPanel, LOOKUP);
		panel.add(summationPanel, SUMMATION);
		panel.add(smartPanel, SMART);

		return panel;
	}

}

