package peakaboo.ui.swing.plotting.fitting;



import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import eventful.EventfulTypeListener;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.ui.swing.plotting.PlotCanvas;
import peakaboo.ui.swing.plotting.PlotPanel;
import peakaboo.ui.swing.plotting.fitting.fitted.FittingPanel;
import peakaboo.ui.swing.plotting.fitting.guidedfitting.GuidedFittingPanel;
import peakaboo.ui.swing.plotting.fitting.lookup.LookupPanel;
import peakaboo.ui.swing.plotting.fitting.summation.SummationPanel;
import swidget.widgets.ClearPanel;
import swidget.widgets.tabbedinterface.TabbedInterfaceDialog;




public class CurveFittingView extends ClearPanel implements Changeable
{

	private FittingController	controller;
	private PlotController	 	plotController;
	private PlotPanel			plotPanel;

	private final String		FITTED		= "Fitted";
	private final String		LOOKUP		= "Lookup";
	private final String		SUMMATION	= "Summation";
	private final String		SMART		= "Smart";


	private FittingPanel		fittedPanel;
	private LookupPanel			proposalPanel;
	private SummationPanel		summationPanel;
	private GuidedFittingPanel	smartPanel;
	
	
	private JPanel				cardPanel;
	private CardLayout			card;

	
	

	public CurveFittingView(FittingController _controller, PlotController plotController, PlotPanel plotPanel, PlotCanvas canvas)
	{
		super();
		
		this.controller = _controller;
		this.plotController = plotController;
		this.plotPanel = plotPanel;

		setPreferredSize(new Dimension(200, getPreferredSize().height));

		fittedPanel = new FittingPanel(controller, this);
		proposalPanel = new LookupPanel(controller, this);
		summationPanel = new SummationPanel(controller, this);
		smartPanel = new GuidedFittingPanel(controller, this, canvas);

		cardPanel = createCardPanel();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(cardPanel);

		
		controller.addListener(new EventfulTypeListener<Boolean>() {

			public void change(Boolean b)
			{
				//b will be true if the fitting model has been changed in some way
				//other than through the FittingController (ie load session, undo, etc)
				if (b) {
					changed();
				}
			}
		});

	}


	@Override
	public String getName()
	{
		return "Peak Fitting";
	}

	
	


	public void changed()
	{
		fittedPanel.changed();
		proposalPanel.changed();
	}

	public void elementalAdd()
	{
		card.show(cardPanel, LOOKUP);
		changed();
	}
	
	public void summationAdd()
	{
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
			new TabbedInterfaceDialog(
					"No Energy Calibration", 
					"Guided fitting cannot proceed without energy calibration.", 
					JOptionPane.WARNING_MESSAGE, 
					JOptionPane.DEFAULT_OPTION, 
					result -> {}
				).showIn(plotPanel);
			
		}
		

	}
	

	
	
	public void dialogClose()
	{
		card.show(cardPanel, FITTED);
		smartPanel.setSelectionMode(false);
		summationPanel.setActive(false);
		changed();
	}
	


	private JPanel createCardPanel()
	{
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

